-- ============================================================================
-- Rollback Migration: 002_rollback.sql
-- ============================================================================

BEGIN;

DROP TRIGGER IF EXISTS trg_rfq_status_audit ON rfqs;
DROP FUNCTION IF EXISTS log_rfq_status_change();
DROP TABLE IF EXISTS notification_queue CASCADE;

COMMIT;
