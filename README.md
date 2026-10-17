# CJ FABRICATIONS — FULL-STACK FABRICATION MARKETPLACE

India's quotation-led B2B marketplace connecting customers (homeowners, architects, interior firms, and civil builders) with verified metalwork and structural fabrication workshops.

---

## 1. System Architecture & Deployment

- **Live URL:** https://parkzone.in/cjfabrication/
- **Repository:** https://github.com/yashas-13/cjfabrication
- **Deployment Path:** `/var/www/cjfabrication`
- **Reverse Proxy:** Nginx terminating TLS/HTTPS → Node.js / Express on `127.0.0.1:3010`
- **Authoritative Database:** PostgreSQL 14+ with connection pooling and transactions
- **Mobile Client:** Native Android application (Kotlin, Jetpack Compose, Room persistence, Retrofit) + Installable PWA

---

## 2. Environment Configuration

1. Copy `.env.example` to `.env` in the deployment directory:
   ```bash
   cp .env.example .env
   ```
2. Configure your database credentials, WhatsApp business helpline, and storage paths. Never commit `.env` to source control.

---

## 3. Database Migrations & Rollback Procedures

### Applying Migrations
Execute migrations in numeric order using `psql`:
```bash
# Apply initial schema (Users, Vendors, Categories, Designs, RFQs, Quotes, Orders)
psql -h 127.0.0.1 -U cjfab_app -d cjfabrication -f migrations/001_initial_schema.sql

# Apply notification queue & audit triggers
psql -h 127.0.0.1 -U cjfab_app -d cjfabrication -f migrations/002_rfq_workflow_and_milestones.sql
```

### Rollback (If needed)
Rollback scripts are located in `migrations/rollback/`:
```bash
psql -h 127.0.0.1 -U cjfab_app -d cjfabrication -f migrations/rollback/002_rollback.sql
psql -h 127.0.0.1 -U cjfab_app -d cjfabrication -f migrations/rollback/001_rollback.sql
```

### PostgreSQL Backup & Restore
- **Backup:**
  ```bash
  pg_dump -h 127.0.0.1 -U cjfab_app -F c -b -v -f /var/backups/cjfab_$(date +%Y%m%d_%H%M%S).dump cjfabrication
  ```
- **Restore:**
  ```bash
  pg_restore -h 127.0.0.1 -U cjfab_app -d cjfabrication -v -c /var/backups/cjfab_TARGET.dump
  ```

---

## 4. Backend Deployment (`/var/www/cjfabrication`)

```bash
cd /var/www/cjfabrication/backend
npm install --production

# Run with PM2 or systemd service
pm2 start server.js --name "cjfab-api"
pm2 save

# Verify health probes
curl -s http://127.0.0.1:3010/health
curl -s http://127.0.0.1:3010/ready
```

### Nginx Reverse Proxy Snippet
```nginx
location /cjfabrication/api/ {
    proxy_pass http://127.0.0.1:3010/api/;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection 'upgrade';
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    client_max_body_size 10M;
}
```

---

## 5. Automated Tests Execution

### Backend Integration Tests
```bash
node --test backend/tests/api.test.js
```

### Android Robolectric & Unit Tests
```bash
gradle :app:testDebugUnitTest
```

---

## 6. End-to-End Quotation Journey

1. **Customer Browse & Configure:** Customer browses designs or enters custom dimensions (e.g. 14 ft x 7 ft Main Gate in MS IS 2062).
2. **Add to My Requirement:** Item added to requirement list with material and finish selections.
3. **Submit RFQ (Zero-Login):** Submits requirement with site location and target date. Backend creates unique reference `CJF-2026-X892` and token.
4. **WhatsApp Handoff:** Formatted structured summary generated for instant chat dispatch.
5. **Vendor Quoting:** Verified workshops submit itemized quotes (base fabrication, material, installation, 18% GST).
6. **Quote Acceptance:** Customer reviews and accepts quote; backend transitions status to `accepted` and generates a tracked Work Order (`ORD-2026-XXXX`) with 6 manufacturing milestones.
