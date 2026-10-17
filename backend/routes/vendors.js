// ============================================================================
// Vendor Route Handlers — CJ Fabrications
// Strictly enforces assigned RFQs access control, RBAC, and workshop auditing
// ============================================================================

const express = require('express');
const router = express.Router();
const { query } = require('../db');
const { requireRole } = require('../middleware/auth');

/**
 * GET /api/vendors
 * List verified vendors for marketplace overview
 */
router.get('/', async (req, res, next) => {
  try {
    const sql = `
      SELECT id, business_name, city, state, years_in_business, rating,
             completed_projects_count, machinery_capabilities, categories_served,
             verification_status
      FROM vendor_profiles
      WHERE verification_status = 'verified'
      ORDER BY rating DESC;
    `;
    const result = await query(sql);
    res.json({ success: true, data: result.rows });
  } catch (err) {
    next(err);
  }
});

/**
 * GET /api/vendors/:id/rfqs
 * Retrieve strictly assigned RFQs for authenticated vendor.
 * RBAC: Vendor can only see their own assigned RFQs. Admin can inspect any.
 */
router.get('/:id/rfqs', requireRole(['vendor', 'admin']), async (req, res, next) => {
  try {
    const { id } = req.params;

    // RBAC: Vendor cannot query another vendor's assigned leads
    if (req.user.role === 'vendor' && req.user.vendorId !== id) {
      return res.status(403).json({
        success: false,
        error: 'RBAC Access Denied: You cannot inspect another fabricator workshop\'s assigned leads.',
      });
    }

    const sql = `
      SELECT r.*
      FROM rfqs r
      JOIN rfq_vendor_assignments a ON r.id = a.rfq_id
      WHERE a.vendor_id = $1
      ORDER BY r.created_at DESC;
    `;
    const result = await query(sql, [id]);

    res.json({ success: true, data: result.rows });
  } catch (err) {
    next(err);
  }
});

/**
 * PATCH /api/vendors/:id/status
 * Admin only: update vendor verification status (verified, pending_verification, suspended)
 */
router.patch('/:id/status', requireRole(['admin']), async (req, res, next) => {
  try {
    const { id } = req.params;
    const { status, remarks } = req.body;

    if (!['pending_verification', 'verified', 'suspended'].includes(status)) {
      return res.status(400).json({ success: false, error: 'Invalid verification status.' });
    }

    const sql = `
      UPDATE vendor_profiles
      SET verification_status = $1, updated_at = CURRENT_TIMESTAMP
      WHERE id = $2
      RETURNING *;
    `;
    const result = await query(sql, [status, id]);

    if (!result.rows.length) {
      return res.status(404).json({ success: false, error: 'Vendor not found.' });
    }

    await query(
      `INSERT INTO audit_logs (entity_name, entity_id, action, performed_by, changes)
       VALUES ('vendor_profile', $1, 'UPDATE_VERIFICATION_STATUS', $2, $3);`,
      [id, req.user.id, JSON.stringify({ new_status: status, remarks })]
    );

    res.json({ success: true, data: result.rows[0] });
  } catch (err) {
    next(err);
  }
});

module.exports = router;
