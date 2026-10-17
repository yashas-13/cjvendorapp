# CJ FABRICATIONS — APPLICATION SECURITY CHECKLIST & OWASP COMPLIANCE

This document outlines the security controls implemented across the CJ Fabrications platform (Backend, PostgreSQL Database, and Android/PWA clients).

---

## 1. Authentication & Role-Based Access Control (RBAC)
- [x] **Zero-Trust Role Enforcement:** Server-side validation on every administrative and vendor endpoint (`req.user.role`). Roles supplied in client payloads are strictly ignored.
- [x] **Vendor-to-RFQ Isolation:** Vendors can only access RFQs explicitly assigned to their `vendor_id` via `rfq_vendor_assignments`. Direct Object Reference (IDOR) attempts return `403 Forbidden`.
- [x] **Unguessable Public RFQ Access Tokens:** Public customers can track requirements without account creation using a high-entropy 16-character cryptographic token (`X-RFQ-Token` or query parameter). Guessable sequential integer IDs are completely decoupled from external URLs.

---

## 2. PostgreSQL Database & Injection Defenses
- [x] **100% Parameterized Queries:** All SQL queries use positional parameters (`$1, $2, ...`) via `pg.query`. No dynamic string concatenation or unsafe interpolation is permitted.
- [x] **Transactional Integrity:** Multi-entity operations (e.g., RFQ + line items creation, quote acceptance + order initiation + milestone seeding) are wrapped in atomic PostgreSQL `BEGIN ... COMMIT / ROLLBACK` transactions.
- [x] **Immutable Audit Logging:** Key state transitions trigger automatic audit logging in `audit_logs` tracking entity, action, timestamp, and changes.

---

## 3. WhatsApp Integration Security
- [x] **No Sensitive Data Exposure in URLs:** WhatsApp links contain only product summaries, dimensions, and the authorized tracking link. No passwords, user tokens, internal server file paths, or private credentials are included in URLs.
- [x] **Verified Fallback:** Direct click-to-chat fallback (`https://api.whatsapp.com/send?phone=...`) is transparently handled without false claims of background API delivery.

---

## 4. File Upload & Drawing Security
- [x] **Strict Extension & MIME Whitelisting:** Permitted file formats are restricted to `application/pdf`, `image/jpeg`, `image/png`, and architectural `application/dxf`. Executable binaries (`.exe`, `.sh`, `.apk`, `.php`) are rejected at the edge.
- [x] **Storage Segregation:** Uploaded files are stored in a private directory outside web root (`/var/www/cjfabrication/uploads`), accessible only via authorized download handlers with authentication.
- [x] **File Size Capping:** Strict 10MB limit enforced per upload.

---

## 5. Network & HTTP Controls
- [x] **HTTPS Enforcement:** Production communication strictly requires TLS/SSL (Nginx reverse proxy terminated).
- [x] **CORS Configuration:** Specific domain whitelisting (`https://parkzone.in`).
- [x] **Health and Readiness Probes:** Decoupled `/health` (app liveness) and `/ready` (PostgreSQL connectivity check) probes for operational monitoring.
