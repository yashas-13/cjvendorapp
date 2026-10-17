// ============================================================================
// Orders & Milestones Route Handlers — CJ Fabrications
// ============================================================================

const express = require('express');
const router = express.Router();
const { query, transaction } = require('../db');

/**
 * GET /api/orders/:id
 * Retrieve order details and milestones
 */
router.get('/:id', async (req, res, next) => {
  try {
    const { id } = req.params;
    const orderSql = `
      SELECT o.*, v.business_name as vendor_name, v.phone as vendor_phone,
             r.reference_number as rfq_reference, r.customer_name
      FROM orders o
      JOIN vendor_profiles v ON o.vendor_id = v.id
      JOIN rfqs r ON o.rfq_id = r.id
      WHERE o.id = $1 LIMIT 1;
    `;
    const orderRes = await query(orderSql, [id]);
    if (!orderRes.rows.length) {
      return res.status(404).json({ success: false, error: 'Order not found.' });
    }

    const order = orderRes.rows[0];
    const milestonesSql = `
      SELECT * FROM order_milestones
      WHERE order_id = $1
      ORDER BY step_index ASC;
    `;
    const milestonesRes = await query(milestonesSql, [id]);

    res.json({
      success: true,
      data: {
        ...order,
        milestones: milestonesRes.rows,
      },
    });
  } catch (err) {
    next(err);
  }
});

/**
 * POST /api/orders/:id/milestones
 * Update milestone status (Vendor/Admin)
 */
router.post('/:id/milestones', async (req, res, next) => {
  try {
    const { id } = req.params;
    const { stepIndex, status, remarks } = req.body;

    const result = await transaction(async (client) => {
      // 1. Update current step
      const updateStepSql = `
        UPDATE order_milestones
        SET state = $1, remarks = $2, completed_at = CASE WHEN $1 = 'completed' THEN CURRENT_TIMESTAMP ELSE completed_at END
        WHERE order_id = $3 AND step_index = $4
        RETURNING *;
      `;
      const stepRes = await client.query(updateStepSql, [status, remarks, id, stepIndex]);

      // 2. If completed, set next step to in_progress
      if (status === 'completed') {
        await client.query(
          `UPDATE order_milestones SET state = 'in_progress' WHERE order_id = $1 AND step_index = $2;`,
          [id, stepIndex + 1]
        );
        await client.query(
          `UPDATE orders SET current_step_index = $1, updated_at = CURRENT_TIMESTAMP WHERE id = $2;`,
          [stepIndex + 1, id]
        );
      }

      return stepRes.rows[0];
    });

    res.json({ success: true, data: result });
  } catch (err) {
    next(err);
  }
});

module.exports = router;
