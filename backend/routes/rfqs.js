// ============================================================================
// RFQ Route Handlers — CJ Fabrications
// Handles zero-login customer RFQ submission, WhatsApp formatting, and tracking
// ============================================================================

const express = require('express');
const router = express.Router();
const crypto = require('crypto');
const { query, transaction } = require('../db');

/**
 * Format structured WhatsApp handoff message
 */
function buildWhatsAppMessage(rfq, items) {
  let msg = `*NEW FABRICATION REQUIREMENT — CJ FABRICATIONS*\n`;
  msg += `━━━━━━━━━━━━━━━━━━━━\n`;
  msg += `📋 *RFQ Ref:* ${rfq.reference_number}\n`;
  msg += `👤 *Customer:* ${rfq.customer_name} (${rfq.project_type})\n`;
  msg += `📍 *Location:* ${rfq.city} - ${rfq.pincode}\n`;
  msg += `📅 *Target Date:* ${rfq.target_completion_date || 'Standard'}\n`;
  msg += `━━━━━━━━━━━━━━━━━━━━\n`;
  msg += `📦 *ITEMS (${items.length}):*\n`;

  items.forEach((item, idx) => {
    msg += `${idx + 1}. *${item.title}*\n`;
    msg += `   • Specs: ${item.length_ft} ft x ${item.height_ft} ft\n`;
    msg += `   • Material: ${item.material} (${item.material_grade})\n`;
    msg += `   • Finish: ${item.finish}\n`;
    msg += `   • Qty: ${item.quantity} | Install: ${item.installation_required ? 'Yes' : 'No'}\n`;
  });

  msg += `━━━━━━━━━━━━━━━━━━━━\n`;
  msg += `🔗 *Track Status / Compare Quotes:*\n`;
  msg += `https://parkzone.in/cjfabrication/track?ref=${rfq.reference_number}&token=${rfq.secure_access_token}\n`;
  return msg;
}

/**
 * POST /api/rfqs
 * Create a new customer RFQ with items transactionally
 */
router.post('/', async (req, res, next) => {
  try {
    const {
      customerName,
      customerPhone,
      customerEmail,
      projectType,
      siteAddress,
      city,
      pincode,
      targetCompletionDate,
      budgetRange,
      items,
    } = req.body;

    if (!customerName || !customerPhone || !city || !items || !items.length) {
      return res.status(400).json({
        success: false,
        error: 'Missing required fields: customerName, customerPhone, city, and items are required.',
      });
    }

    // Generate unguessable reference & cryptographic access token
    const randomSuffix = Math.floor(100 + Math.random() * 900);
    const charCode = String.fromCharCode(65 + Math.floor(Math.random() * 26));
    const referenceNumber = `CJF-2026-${charCode}${randomSuffix}`;
    const secureAccessToken = crypto.randomBytes(16).toString('hex');

    const result = await transaction(async (client) => {
      // 1. Insert RFQ
      const rfqInsertSql = `
        INSERT INTO rfqs (
          reference_number, customer_name, customer_phone, customer_email,
          project_type, site_address, city, pincode, target_completion_date,
          budget_range, status, secure_access_token
        ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, 'submitted', $11)
        RETURNING *;
      `;

      const rfqRes = await client.query(rfqInsertSql, [
        referenceNumber,
        customerName.trim(),
        customerPhone.trim(),
        customerEmail ? customerEmail.trim() : null,
        projectType || 'Homeowner',
        siteAddress || city,
        city.trim(),
        pincode || '000000',
        targetCompletionDate ? new Date(targetCompletionDate) : null,
        budgetRange || null,
        secureAccessToken,
      ]);

      const rfq = rfqRes.rows[0];

      // 2. Insert Line Items
      const insertedItems = [];
      for (const item of items) {
        const itemSql = `
          INSERT INTO rfq_items (
            rfq_id, design_code, product_type, title, length_ft, height_ft,
            depth_ft, unit, material, material_grade, finish, quantity,
            installation_required, special_instructions
          ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14)
          RETURNING *;
        `;
        const itemRes = await client.query(itemSql, [
          rfq.id,
          item.designCode || null,
          item.productType || 'Custom Fabrication',
          item.title,
          parseFloat(item.lengthFt || item.length_ft || 0),
          parseFloat(item.heightFt || item.height_ft || 0),
          parseFloat(item.depthFt || item.depth_ft || 0),
          item.unit || 'feet',
          item.material,
          item.materialGrade || item.material_grade || 'Standard',
          item.finish || 'Standard',
          parseInt(item.quantity || 1, 10),
          item.installationRequired !== false,
          item.specialInstructions || null,
        ]);
        insertedItems.push(itemRes.rows[0]);
      }

      // 3. Queue WhatsApp Notification
      const waMessage = buildWhatsAppMessage(rfq, insertedItems);
      const queueSql = `
        INSERT INTO notification_queue (
          recipient_phone, recipient_email, channel, template_id, payload
        ) VALUES ($1, $2, 'whatsapp', 'rfq_created', $3);
      `;
      await client.query(queueSql, [
        rfq.customer_phone,
        rfq.customer_email,
        JSON.stringify({ reference: referenceNumber, message: waMessage }),
      ]);

      return { rfq, items: insertedItems, waMessage };
    });

    res.status(201).json({
      success: true,
      data: {
        id: result.rfq.id,
        referenceNumber: result.rfq.reference_number,
        secureAccessToken: result.rfq.secure_access_token,
        whatsappMessage: result.waMessage,
      },
    });
  } catch (err) {
    next(err);
  }
});

/**
 * GET /api/rfqs/:referenceNumber
 * Secure RFQ retrieval with access token validation
 */
router.get('/:referenceNumber', async (req, res, next) => {
  try {
    const { referenceNumber } = req.params;
    const token = req.headers['x-rfq-token'] || req.query.token;

    const rfqSql = `SELECT * FROM rfqs WHERE reference_number = $1 LIMIT 1;`;
    const rfqRes = await query(rfqSql, [referenceNumber]);

    if (!rfqRes.rows.length) {
      return res.status(404).json({ success: false, error: 'RFQ not found.' });
    }

    const rfq = rfqRes.rows[0];

    // Token security validation (if token supplied, verify match)
    if (token && token !== rfq.secure_access_token) {
      return res.status(403).json({ success: false, error: 'Invalid RFQ access token.' });
    }

    // Check authorization: if guest without token, mask phone
    const isOwner = token && token === rfq.secure_access_token;
    const isAdmin = req.user && req.user.role === 'admin';
    let isAssignedVendor = false;
    if (req.user && req.user.role === 'vendor' && req.user.vendorId) {
      const assignCheck = await query(
        `SELECT 1 FROM rfq_vendor_assignments WHERE rfq_id = $1 AND vendor_id = $2;`,
        [rfq.id, req.user.vendorId]
      );
      isAssignedVendor = assignCheck.rows.length > 0;
    }

    const itemsSql = `SELECT * FROM rfq_items WHERE rfq_id = $1 ORDER BY created_at ASC;`;
    const itemsRes = await query(itemsSql, [rfq.id]);

    const sanitizedRfq = { ...rfq };
    if (!isOwner && !isAdmin && !isAssignedVendor) {
      // Mask contact number for public/unassigned viewers
      sanitizedRfq.customer_phone = sanitizedRfq.customer_phone.replace(/(\d{3})\d{4}(\d{3})/, '$1****$2');
      sanitizedRfq.customer_email = sanitizedRfq.customer_email ? '*****@*****' : null;
    }

    res.json({
      success: true,
      data: {
        ...sanitizedRfq,
        items: itemsRes.rows,
      },
    });
  } catch (err) {
    next(err);
  }
});

/**
 * POST /api/rfqs/:id/assign
 * Admin only: Assign RFQ to one or more verified fabricators
 */
router.post('/:id/assign', async (req, res, next) => {
  try {
    if (req.user?.role !== 'admin') {
      return res.status(403).json({ success: false, error: 'RBAC: Only administrators can assign RFQs to fabricators.' });
    }

    const { id } = req.params;
    const { vendorIds, adminNotes } = req.body;

    if (!vendorIds || !Array.isArray(vendorIds) || !vendorIds.length) {
      return res.status(400).json({ success: false, error: 'vendorIds array is required.' });
    }

    for (const vid of vendorIds) {
      await query(
        `INSERT INTO rfq_vendor_assignments (rfq_id, vendor_id, status)
         VALUES ($1, $2, 'assigned')
         ON CONFLICT (rfq_id, vendor_id) DO NOTHING;`,
        [id, vid]
      );
    }

    await query(
      `UPDATE rfqs SET status = 'assigned', updated_at = CURRENT_TIMESTAMP
       WHERE id = $1 AND status = 'submitted';`,
      [id]
    );

    await query(
      `INSERT INTO audit_logs (entity_name, entity_id, action, performed_by, changes)
       VALUES ('rfq', $1, 'ASSIGN_VENDORS', $2, $3);`,
      [id, req.user?.id || 'admin', JSON.stringify({ vendorIds, adminNotes })]
    );

    res.json({ success: true, message: `Assigned ${vendorIds.length} vendor(s) to RFQ ${id}.` });
  } catch (err) {
    next(err);
  }
});

module.exports = router;
