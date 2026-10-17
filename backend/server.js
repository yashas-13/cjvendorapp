// ============================================================================
// CJ Fabrications — Node.js / Express Marketplace Backend Server
// Target: 127.0.0.1:3010 behind Nginx on /var/www/cjfabrication
// Authoritative Database: PostgreSQL
// ============================================================================

const express = require('express');
const cors = require('cors');
const { pool } = require('./db');

const rfqRoutes = require('./routes/rfqs');
const quoteRoutes = require('./routes/quotes');
const orderRoutes = require('./routes/orders');
const vendorRoutes = require('./routes/vendors');
const catalogRoutes = require('./routes/catalog');

const app = express();
const PORT = process.env.PORT || 3010;
const HOST = process.env.HOST || '127.0.0.1';

// Security & Parsing Middleware
app.use(cors({
  origin: process.env.CORS_ORIGIN || '*',
  methods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization', 'X-RFQ-Token'],
}));

app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

const { authenticateContext } = require('./middleware/auth');
app.use(authenticateContext);

// Health Check Endpoints
app.get('/health', (req, res) => {
  res.json({
    status: 'UP',
    service: 'cj-fabrications-backend',
    timestamp: new Date().toISOString(),
  });
});

app.get('/ready', async (req, res) => {
  try {
    await pool.query('SELECT 1;');
    res.json({
      status: 'READY',
      database: 'connected',
      timestamp: new Date().toISOString(),
    });
  } catch (err) {
    res.status(503).json({
      status: 'DEGRADED',
      database: 'disconnected',
      error: err.message,
    });
  }
});

// API Routes
app.use('/api/rfqs', rfqRoutes);
app.use('/api/quotes', quoteRoutes);
app.use('/api/orders', orderRoutes);
app.use('/api/vendors', vendorRoutes);
app.use('/api', catalogRoutes);

// 404 Handler
app.use((req, res) => {
  res.status(404).json({
    success: false,
    error: `Cannot ${req.method} ${req.originalUrl}`,
  });
});

// Global Central Error Handler
app.use((err, req, res, next) => {
  console.error('[Unhandled Error]', err.stack || err.message);
  res.status(err.status || 500).json({
    success: false,
    error: process.env.NODE_ENV === 'production' ? 'Internal server error occurred.' : err.message,
  });
});

if (require.main === module) {
  app.listen(PORT, HOST, () => {
    console.log(`[CJ Fabrications API] Listening on http://${HOST}:${PORT}`);
  });
}

module.exports = app;
