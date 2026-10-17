-- ============================================================================
-- Migration: 001_initial_schema.sql
-- Project: CJ Fabrications Marketplace
-- Database: PostgreSQL (Authoritative Business Database)
-- Author: Principal Software Architect
-- ============================================================================

BEGIN;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Roles Enum & Users Table
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('customer', 'vendor', 'admin');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE vendor_status AS ENUM ('pending_verification', 'verified', 'suspended');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE rfq_status_type AS ENUM (
        'draft',
        'submitted',
        'assigned',
        'quoted',
        'accepted',
        'in_production',
        'dispatched',
        'installation',
        'completed',
        'cancelled'
    );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE quotation_status_type AS ENUM ('pending', 'accepted', 'declined', 'expired');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    role user_role NOT NULL DEFAULT 'customer',
    password_hash VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- 2. Vendor Profiles
CREATE TABLE IF NOT EXISTS vendor_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    business_name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    workshop_address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    gst_number VARCHAR(20) UNIQUE NOT NULL,
    verification_status vendor_status NOT NULL DEFAULT 'pending_verification',
    years_in_business INT DEFAULT 1,
    rating NUMERIC(3, 2) DEFAULT 5.00,
    completed_projects_count INT DEFAULT 0,
    machinery_capabilities JSONB NOT NULL DEFAULT '[]'::jsonb,
    categories_served JSONB NOT NULL DEFAULT '[]'::jsonb,
    service_radius_km INT DEFAULT 50,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vendor_status ON vendor_profiles(verification_status);
CREATE INDEX IF NOT EXISTS idx_vendor_city ON vendor_profiles(city);
CREATE INDEX IF NOT EXISTS idx_vendor_gst ON vendor_profiles(gst_number);

-- 3. Product & Service Categories
CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    short_name VARCHAR(50) NOT NULL,
    description TEXT,
    indicative_rate_min NUMERIC(10, 2) NOT NULL,
    indicative_rate_max NUMERIC(10, 2) NOT NULL,
    rate_unit VARCHAR(50) NOT NULL,
    primary_materials JSONB DEFAULT '[]'::jsonb,
    popular_uses JSONB DEFAULT '[]'::jsonb,
    image_url TEXT,
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Designs & Catalogue
CREATE TABLE IF NOT EXISTS designs (
    id VARCHAR(50) PRIMARY KEY,
    category_id VARCHAR(50) REFERENCES categories(id) ON DELETE RESTRICT,
    code VARCHAR(30) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    materials_available JSONB DEFAULT '[]'::jsonb,
    finish_options JSONB DEFAULT '[]'::jsonb,
    indicative_rate_min NUMERIC(10, 2) NOT NULL,
    indicative_rate_max NUMERIC(10, 2) NOT NULL,
    rate_unit VARCHAR(50) NOT NULL,
    default_dimensions VARCHAR(100),
    estimated_weight_kg NUMERIC(8, 2),
    is_popular BOOLEAN DEFAULT FALSE,
    lead_time_days INT DEFAULT 14,
    image_url TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_designs_category ON designs(category_id);
CREATE INDEX IF NOT EXISTS idx_designs_code ON designs(code);

-- 5. RFQs (Request For Quotation)
CREATE TABLE IF NOT EXISTS rfqs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reference_number VARCHAR(30) UNIQUE NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(255),
    project_type VARCHAR(100) DEFAULT 'Homeowner',
    site_address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    target_completion_date DATE,
    budget_range VARCHAR(100),
    status rfq_status_type NOT NULL DEFAULT 'submitted',
    secure_access_token VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_rfqs_reference ON rfqs(reference_number);
CREATE INDEX IF NOT EXISTS idx_rfqs_status ON rfqs(status);
CREATE INDEX IF NOT EXISTS idx_rfqs_phone ON rfqs(customer_phone);
CREATE INDEX IF NOT EXISTS idx_rfqs_token ON rfqs(secure_access_token);

-- 6. RFQ Line Items
CREATE TABLE IF NOT EXISTS rfq_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rfq_id UUID NOT NULL REFERENCES rfqs(id) ON DELETE CASCADE,
    design_code VARCHAR(30),
    product_type VARCHAR(100) NOT NULL,
    title VARCHAR(200) NOT NULL,
    length_ft NUMERIC(8, 2) NOT NULL,
    height_ft NUMERIC(8, 2) NOT NULL,
    depth_ft NUMERIC(8, 2) DEFAULT 0,
    unit VARCHAR(20) DEFAULT 'feet',
    material VARCHAR(100) NOT NULL,
    material_grade VARCHAR(100) NOT NULL,
    finish VARCHAR(100) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    installation_required BOOLEAN NOT NULL DEFAULT TRUE,
    special_instructions TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_rfq_items_rfq ON rfq_items(rfq_id);

-- 7. Private Attachments (Architectural CAD & Drawings)
CREATE TABLE IF NOT EXISTS rfq_attachments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rfq_id UUID NOT NULL REFERENCES rfqs(id) ON DELETE CASCADE,
    rfq_item_id UUID REFERENCES rfq_items(id) ON DELETE SET NULL,
    file_name VARCHAR(255) NOT NULL,
    storage_path TEXT NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    checksum_sha256 VARCHAR(64),
    uploaded_by_ip VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_attachments_rfq ON rfq_attachments(rfq_id);

-- 8. Vendor Assignments (Strict Authorization)
CREATE TABLE IF NOT EXISTS rfq_vendor_assignments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rfq_id UUID NOT NULL REFERENCES rfqs(id) ON DELETE CASCADE,
    vendor_id UUID NOT NULL REFERENCES vendor_profiles(id) ON DELETE CASCADE,
    assigned_by_user_id UUID REFERENCES users(id),
    status VARCHAR(50) DEFAULT 'assigned',
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_rfq_vendor UNIQUE (rfq_id, vendor_id)
);

CREATE INDEX IF NOT EXISTS idx_assignments_rfq ON rfq_vendor_assignments(rfq_id);
CREATE INDEX IF NOT EXISTS idx_assignments_vendor ON rfq_vendor_assignments(vendor_id);

-- 9. Quotations & Breakdown
CREATE TABLE IF NOT EXISTS quotations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rfq_id UUID NOT NULL REFERENCES rfqs(id) ON DELETE CASCADE,
    vendor_id UUID NOT NULL REFERENCES vendor_profiles(id) ON DELETE CASCADE,
    base_fabrication_cost NUMERIC(12, 2) NOT NULL,
    material_cost NUMERIC(12, 2) NOT NULL,
    installation_cost NUMERIC(12, 2) DEFAULT 0,
    transport_cost NUMERIC(12, 2) DEFAULT 0,
    gst_rate_percent NUMERIC(5, 2) DEFAULT 18.00,
    gst_amount NUMERIC(12, 2) NOT NULL,
    grand_total NUMERIC(12, 2) NOT NULL,
    lead_time_days INT NOT NULL,
    validity_date DATE NOT NULL,
    material_grade_guarantee TEXT NOT NULL,
    site_inspection_required BOOLEAN DEFAULT FALSE,
    technical_notes TEXT,
    status quotation_status_type NOT NULL DEFAULT 'pending',
    submitted_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_quote_rfq_vendor UNIQUE (rfq_id, vendor_id)
);

CREATE INDEX IF NOT EXISTS idx_quotations_rfq ON quotations(rfq_id);
CREATE INDEX IF NOT EXISTS idx_quotations_vendor ON quotations(vendor_id);
CREATE INDEX IF NOT EXISTS idx_quotations_status ON quotations(status);

-- 10. Orders & Milestones
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_number VARCHAR(50) UNIQUE NOT NULL,
    rfq_id UUID NOT NULL REFERENCES rfqs(id) ON DELETE RESTRICT,
    quotation_id UUID NOT NULL REFERENCES quotations(id) ON DELETE RESTRICT,
    vendor_id UUID NOT NULL REFERENCES vendor_profiles(id) ON DELETE RESTRICT,
    customer_id UUID REFERENCES users(id),
    total_amount NUMERIC(12, 2) NOT NULL,
    delivery_address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    current_step_index INT DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_orders_rfq ON orders(rfq_id);
CREATE INDEX IF NOT EXISTS idx_orders_vendor ON orders(vendor_id);

CREATE TABLE IF NOT EXISTS order_milestones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    step_index INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    state VARCHAR(30) NOT NULL DEFAULT 'pending', -- pending, in_progress, completed
    remarks TEXT,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_order_step UNIQUE (order_id, step_index)
);

CREATE INDEX IF NOT EXISTS idx_order_milestones ON order_milestones(order_id);

-- 11. Audit Logs (Immutable)
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_name VARCHAR(50) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    performed_by VARCHAR(100),
    ip_address VARCHAR(50),
    changes JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_logs(entity_name, entity_id);

COMMIT;
