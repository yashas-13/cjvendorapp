-- ============================================================================
-- Migration: 002_rfq_workflow_and_milestones.sql
-- Project: CJ Fabrications Marketplace
-- ============================================================================

BEGIN;

-- Notifications Queue for WhatsApp / SMS / Email Dispatches
CREATE TABLE IF NOT EXISTS notification_queue (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recipient_phone VARCHAR(20) NOT NULL,
    recipient_email VARCHAR(255),
    channel VARCHAR(20) NOT NULL, -- whatsapp, sms, email
    template_id VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending', -- pending, sent, failed
    attempts INT DEFAULT 0,
    last_error TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_notifications_status ON notification_queue(status);

-- Function to record audit events automatically
CREATE OR REPLACE FUNCTION log_rfq_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF (OLD.status IS DISTINCT FROM NEW.status) THEN
        INSERT INTO audit_logs (entity_name, entity_id, action, performed_by, changes)
        VALUES (
            'rfq',
            NEW.id::text,
            'STATUS_CHANGE',
            'system',
            json_build_object('old_status', OLD.status, 'new_status', NEW.status)
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_rfq_status_audit ON rfqs;
CREATE TRIGGER trg_rfq_status_audit
    AFTER UPDATE ON rfqs
    FOR EACH ROW
    EXECUTE FUNCTION log_rfq_status_change();

COMMIT;
