// ============================================================================
// Catalog Route Handlers — Categories & Designs
// ============================================================================

const express = require('express');
const router = express.Router();
const { query } = require('../db');

/**
 * GET /api/categories
 * List active fabrication categories
 */
router.get('/categories', async (req, res, next) => {
  try {
    const sql = `SELECT * FROM categories WHERE is_active = TRUE ORDER BY display_order ASC;`;
    const result = await query(sql);
    res.json({ success: true, data: result.rows });
  } catch (err) {
    next(err);
  }
});

/**
 * GET /api/designs
 * List designs with category and material filtering
 */
router.get('/designs', async (req, res, next) => {
  try {
    const { categoryId, search, material } = req.query;
    let sql = `SELECT * FROM designs WHERE is_active = TRUE`;
    const params = [];

    if (categoryId) {
      params.push(categoryId);
      sql += ` AND category_id = $${params.length}`;
    }

    if (search) {
      params.push(`%${search}%`);
      sql += ` AND (title ILIKE $${params.length} OR description ILIKE $${params.length} OR code ILIKE $${params.length})`;
    }

    sql += ` ORDER BY is_popular DESC, created_at DESC;`;

    const result = await query(sql, params);
    res.json({ success: true, data: result.rows });
  } catch (err) {
    next(err);
  }
});

/**
 * GET /api/projects
 * List project listings / designs from PostgreSQL database
 */
router.get('/projects', async (req, res, next) => {
  try {
    const { categoryId, search } = req.query;
    let sql = `SELECT * FROM designs WHERE is_active = TRUE`;
    const params = [];

    if (categoryId) {
      params.push(categoryId);
      sql += ` AND category_id = $${params.length}`;
    }

    if (search) {
      params.push(`%${search}%`);
      sql += ` AND (title ILIKE $${params.length} OR description ILIKE $${params.length} OR code ILIKE $${params.length})`;
    }

    sql += ` ORDER BY is_popular DESC, created_at DESC;`;

    const result = await query(sql, params);
    const projects = result.rows.map(row => ({
      id: row.id,
      category_id: row.category_id,
      code: row.code,
      title: row.title,
      description: row.description,
      materials_available: row.materials_available || [],
      finish_options: row.finish_options || [],
      indicative_rate_min: parseInt(row.indicative_rate_min, 10) || 0,
      indicative_rate_max: parseInt(row.indicative_rate_max, 10) || 0,
      rate_unit: row.rate_unit || 'sq. ft.',
      default_dimensions: row.default_dimensions || '',
      estimated_weight_kg: parseFloat(row.estimated_weight_kg) || 0.0,
      is_popular: row.is_popular || false,
      lead_time_days: row.lead_time_days || 14,
      image_url: row.image_url || '',
      is_active: row.is_active !== false,
      updated_at: row.created_at ? new Date(row.created_at).getTime() : Date.now()
    }));
    res.json({ success: true, data: projects });
  } catch (err) {
    next(err);
  }
});

/**
 * POST /api/projects
 * Upsert a project listing into PostgreSQL
 */
router.post('/projects', async (req, res, next) => {
  try {
    const p = req.body;
    if (!p.id || !p.title) {
      return res.status(400).json({ success: false, error: 'Project id and title are required.' });
    }
    const sql = `
      INSERT INTO designs (
        id, category_id, code, title, description, materials_available, finish_options,
        indicative_rate_min, indicative_rate_max, rate_unit, default_dimensions,
        estimated_weight_kg, is_popular, lead_time_days, image_url, is_active
      ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16)
      ON CONFLICT (id) DO UPDATE SET
        title = EXCLUDED.title,
        description = EXCLUDED.description,
        category_id = EXCLUDED.category_id,
        materials_available = EXCLUDED.materials_available,
        finish_options = EXCLUDED.finish_options,
        indicative_rate_min = EXCLUDED.indicative_rate_min,
        indicative_rate_max = EXCLUDED.indicative_rate_max,
        rate_unit = EXCLUDED.rate_unit,
        default_dimensions = EXCLUDED.default_dimensions,
        estimated_weight_kg = EXCLUDED.estimated_weight_kg,
        is_popular = EXCLUDED.is_popular,
        lead_time_days = EXCLUDED.lead_time_days,
        image_url = EXCLUDED.image_url,
        is_active = EXCLUDED.is_active
      RETURNING *;
    `;
    const params = [
      p.id,
      p.category_id || p.categoryId || 'cat_main_gates',
      p.code || `CJ-${p.id.slice(-6).toUpperCase()}`,
      p.title,
      p.description || '',
      JSON.stringify(p.materials_available || p.materialsAvailable || []),
      JSON.stringify(p.finish_options || p.finishOptions || []),
      p.indicative_rate_min || p.indicativeRateMin || 0,
      p.indicative_rate_max || p.indicativeRateMax || 0,
      p.rate_unit || p.rateUnit || 'sq. ft.',
      p.default_dimensions || p.defaultDimensions || '',
      p.estimated_weight_kg || p.estimatedWeightKg || 0.0,
      p.is_popular || p.isPopular || false,
      p.lead_time_days || p.leadTimeDays || 14,
      p.image_url || p.imageUrl || '',
      p.is_active !== undefined ? p.is_active : (p.isActive !== undefined ? p.isActive : true)
    ];
    const result = await query(sql, params);
    res.json({ success: true, data: result.rows[0] });
  } catch (err) {
    next(err);
  }
});

module.exports = router;
