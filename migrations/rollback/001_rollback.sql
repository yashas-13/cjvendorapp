-- ============================================================================
-- Rollback Migration: 001_rollback.sql
-- Project: CJ Fabrications Marketplace
-- ============================================================================

BEGIN;

DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS order_milestones CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS quotations CASCADE;
DROP TABLE IF EXISTS rfq_vendor_assignments CASCADE;
DROP TABLE IF EXISTS rfq_attachments CASCADE;
DROP TABLE IF EXISTS rfq_items CASCADE;
DROP TABLE IF EXISTS rfqs CASCADE;
DROP TABLE IF EXISTS designs CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS vendor_profiles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

DROP TYPE IF EXISTS quotation_status_type;
DROP TYPE IF EXISTS rfq_status_type;
DROP TYPE IF EXISTS vendor_status;
DROP TYPE IF EXISTS user_role;

COMMIT;
