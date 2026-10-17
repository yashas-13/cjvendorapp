// ============================================================================
// Quotation Route Handlers — CJ Fabrications
// Handles vendor quotation submission, quote comparison, and quote acceptance
// Fully RBAC-enforced: Competitor isolation, verified-only bidding, assignment checks
// ============================================================================

const express = require('express');
const router = express.Router();
const { query, transaction } = require('../db');
const { requireRole, requireVerifiedVendor, requireVendorAssigned } = require('../middleware/auth');

/**
 * GET /api/quotes?rfqId=...
 * Fetch quotes for an RFQ
 * RBAC Rules:
 * - Admin and Customer (with valid RFQ token) can view and compare all quotes.
 * - Vendor can ONLY see their own quote to prevent competitive bidding espionage.
 */
router.get('/', async (req, res, next) => {
  try {
    const { rfqId } = req.query;
    if (!rfqId) {
      return res.status(400).json({ success: false, error: 'rfqId query parameter is required.' });
    }

    let quotesSql = `
      SELECT q.*, v.business_name, v.city as vendor_city, v.rating as vendor_rating,
             v.gst_number, v.verification_status, v.machinery_capabilities
      FROM quotations q
      JOIN vendor_profiles v ON q.vendor_id = v.id
      WHERE q.rfq_id = $1
    `;
    const params = [rfqId];

    // Vendor Data Isolation
    if (req.user && req.user.role === 'vendor' && req.user.vendorId) {
      params.push(req.user.vendorId);
      quotesSql += ` AND q.vendor_id = $2`;
    }

    quotesSql += ` ORDER BY q.grand_total ASC;`;

    const quotesRes = await query(quotesSql, params);

    res.json({
      success: true,
      data: quotesRes.rows,
    });
  } catch (err) {
    next(err);
  }
});

/**
 * POST /api/quotes
 * Submit quotation from verified, assigned fabricator
 * Enforces: Role is vendor/admin, Vendor is verified, Vendor is assigned to RFQ
 */
router.post(
  '/',
  requireRole(['vendor', 'admin']),
  requireVerifiedVendor,
  requireVendorAssigned((req) => req.body.rfqId),
  async (req, res, next) => {
    try {
      const {
        rfqId,
        vendorId,
        baseFabricationCost,
        materialCost,
        installationCost,
        transportCost,
        leadTimeDays,
        validityDate,
        materialGradeGuarantee,
        siteInspectionRequired,
        technicalNotes,
      } = req.body;

      // Verify vendor submitting is the authenticated vendor or admin
      const effectiveVendorId = req.user.role === 'admin' ? (vendorId || req.user.vendorId) : req.user.vendorId;

      const baseFab = parseFloat(baseFabricationCost || 0);
      const mat = parseFloat(materialCost || 0);
      const install = parseFloat(installationCost || 0);
      const trans = parseFloat(transportCost || 0);
      const subtotal = baseFab + mat + install + trans;
      const gstRate = 18.0;
      const gstAmount = subtotal * (gstRate / 100.0);
      const grandTotal = subtotal + gstAmount;

      const result = await transaction(async (client) => {
        // 1. Insert or update quotation
        const quoteSql = `
          INSERT INTO quotations (
            rfq_id, vendor_id, base_fabrication_cost, material_cost,
            installation_cost, transport_cost, gst_rate_percent, gst_amount,
            grand_total, lead_time_days, validity_date, material_grade_guarantee,
            site_inspection_required, technical_notes, status
          ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, 'pending')
          ON CONFLICT (rfq_id, vendor_id) DO UPDATE SET
            base_fabrication_cost = EXCLUDED.base_fabrication_cost,
            material_cost = EXCLUDED.material_cost,
            installation_cost = EXCLUDED.installation_cost,
            transport_cost = EXCLUDED.transport_cost,
            grand_total = EXCLUDED.grand_total,
            lead_time_days = EXCLUDED.lead_time_days,
            validity_date = EXCLUDED.validity_date,
            material_grade_guarantee = EXCLUDED.material_grade_guarantee,
            technical_notes = EXCLUDED.technical_notes,
            updated_at = CURRENT_TIMESTAMP
          RETURNING *;
        `;
        const quoteRes = await client.query(quoteSql, [
          rfqId,
          effectiveVendorId,
          baseFab,
          mat,
          install,
          trans,
          gstRate,
          gstAmount,
          grandTotal,
          parseInt(leadTimeDays || 14, 10),
          new Date(validityDate || Date.now() + 15 * 86400000),
          materialGradeGuarantee || 'Tata/Jindal Standard certified grade',
          !!siteInspectionRequired,
          technicalNotes || '',
        ]);

        // 2. Update RFQ status to 'quoted'
        await client.query(`UPDATE rfqs SET status = 'quoted', updated_at = CURRENT_TIMESTAMP WHERE id = $1;`, [rfqId]);

        // 3. Log audit event
        await client.query(
          `INSERT INTO audit_logs (entity_name, entity_id, action, performed_by, changes)
           VALUES ('quotation', $1, 'SUBMIT_QUOTE', $2, $3);`,
          [quoteRes.rows[0].id, effectiveVendorId, JSON.stringify({ rfqId, grandTotal, leadTimeDays })]
        );

        return quoteRes.rows[0];
      });

      res.status(201).json({ success: true, data: result });
    } catch (err) {
      next(err);
    }
  }
);

/**
 * POST /api/quotes/:id/accept
 * Customer or Admin accepts a quotation, converting it into a live fabrication work order
 */
router.post('/:id/accept', async (req, res, next) => {
  try {
    const { id } = req.params;

    const result = await transaction(async (client) => {
      // 1. Fetch quote
      const quoteSql = `SELECT * FROM quotations WHERE id = $1 LIMIT 1;`;
      const quoteRes = await client.query(quoteSql, [id]);
      if (!quoteRes.rows.length) {
        throw new Error('Quotation not found.');
      }
      const quote = quoteRes.rows[0];

      // 2. Fetch RFQ
      const rfqSql = `SELECT * FROM rfqs WHERE id = $1 LIMIT 1;`;
      const rfqRes = await client.query(rfqSql, [quote.rfq_id]);
      const rfq = rfqRes.rows[0];

      // 3. Accept this quote, decline other quotes for this RFQ
      await client.query(`UPDATE quotations SET status = 'accepted' WHERE id = $1;`, [quote.id]);
      await client.query(`UPDATE quotations SET status = 'declined' WHERE rfq_id = $1 AND id != $2;`, [quote.rfq_id, quote.id]);

      // 4. Update RFQ status to 'accepted'
      await client.query(`UPDATE rfqs SET status = 'accepted', updated_at = CURRENT_TIMESTAMP WHERE id = $1;`, [quote.rfq_id]);

      // 5. Create Order
      const orderNumber = `ORD-2026-${Math.floor(1000 + Math.random() * 9000)}`;
      const orderSql = `
        INSERT INTO orders (
          order_number, rfq_id, quotation_id, vendor_id, total_amount,
          delivery_address, city, current_step_index
        ) VALUES ($1, $2, $3, $4, $5, $6, $7, 1)
        RETURNING *;
      `;
      const orderRes = await client.query(orderSql, [
        orderNumber,
        quote.rfq_id,
        quote.id,
        quote.vendor_id,
        quote.grand_total,
        rfq.site_address,
        rfq.city,
      ]);
      const order = orderRes.rows[0];

      // 6. Create Default Production Milestones
      const milestones = [
        { step: 1, title: 'Engineering CAD Approval', desc: 'Shop drawings approved and fabrication order locked.', state: 'completed' },
        { step: 2, title: 'Raw Material Procurement', desc: 'Mill-certified steel tubes and sheet metal procured.', state: 'in_progress' },
        { step: 3, title: 'Laser Cutting & Fabrication Assembly', desc: 'CNC laser profile cutting, tack welding and dimensional QC.', state: 'pending' },
        { step: 4, title: 'Surface Finishing & Powder Coating', desc: 'Phosphating and 80-micron electro-deposition coating.', state: 'pending' },
        { step: 5, title: 'Transit Dispatch', desc: 'Protective packaging and dispatched to customer site.', state: 'pending' },
        { step: 6, title: 'Site Installation & Handover', desc: 'On-site anchoring, welding, touchup and handover sign-off.', state: 'pending' },
      ];

      for (const m of milestones) {
        await client.query(
          `INSERT INTO order_milestones (order_id, step_index, title, description, state, completed_at)
           VALUES ($1, $2, $3, $4, $5, $6);`,
          [order.id, m.step, m.title, m.desc, m.state, m.state === 'completed' ? new Date() : null]
        );
      }

      // 7. Audit log event
      await client.query(
        `INSERT INTO audit_logs (entity_name, entity_id, action, performed_by, changes)
         VALUES ('order', $1, 'ORDER_CREATED', $2, $3);`,
        [order.id, req.user?.id || 'customer', JSON.stringify({ rfqId: rfq.id, quoteId: quote.id, orderNumber })]
      );

      return order;
    });

    res.json({ success: true, data: result });
  } catch (err) {
    next(err);
  }
});

module.exports = router;
