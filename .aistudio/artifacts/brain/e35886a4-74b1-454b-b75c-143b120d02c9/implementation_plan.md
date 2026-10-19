# Implementation Plan: CJ Fabrications 10-Screen Architecture

Build a complete, production-grade 10-screen Android marketplace experience for **CJ Fabrications (Chethan Jackson Fabrication)**, connecting customers, architects, builders, and administrators with verified metalwork fabrication workflows.

---

## 1. User Journey & Navigation Flow

```
[Screen 1: Welcome & Auth]
   ├── Login (Email/Password or Phone OTP + Google One-Tap)
   ├── Register (Name, Phone, Email, Password)
   └── Forgot Password / OTP Verification
          │
          ▼
[Main Bottom Navigation]
   ├── Tab 1: [Screen 2: Home Dashboard]
   │      ├── Search & Category shortcuts (Gates, Grills, Railings, Stairs, Custom)
   │      ├── Featured & Recent Designs Carousel
   │      └── Quick Actions:
   │             ├── Book a Service ───────────► [Screen 5: Service Booking]
   │             ├── Upload Photos ────────────► [Screen 4: Photo & Design Upload]
   │             ├── Request a Quote ──────────► [Screen 4 / Screen 5]
   │             └── Track Orders ─────────────► [Screen 7: Order Tracking]
   │
   ├── Tab 2: [Screen 3: Product Catalogue]
   │      ├── Image-based Grid & Benchmark Rates
   │      ├── Sector & Category Filter (Residential 5 Divisions vs Industrial 10 Lines)
   │      └── Product Details Modal ───────────► Add to Requirement / Direct Enquiry
   │
   ├── Tab 3: [Screen 5: Service Booking]
   │      ├── Measurement, Date/Time, Address, Photo Attachments
   │      └── Booking Confirmation with Unique Reference (CJF-BK-XXXX)
   │
   ├── Tab 4: [Screen 7: Order Tracking & Screen 6: Quotation]
   │      ├── 9-Stage Visual Milestones (Booking -> Measurement -> Fabrication -> Install)
   │      └── [Screen 6: Quotation View & Actions] (Itemized rates, Accept/Reject/PDF/Share)
   │
   └── Tab 5: [Screen 9: Customer Profile & Screen 8: Notifications]
          ├── Profile details, Saved Site Addresses, Booking & Quote History
          ├── [Screen 8: Notifications Centre] (Date-grouped updates, deep links)
          └── [Screen 10: Admin Dashboard] (Role-protected staff dashboard)
```

---

## 2. Screen Specifications & Features

### Screen 1: Customer Registration, Login & Auth
- **Welcome Screen**: CJ Fabrications industrial logo, company value proposition ("Precision Metalwork Direct From Certified Workshops").
- **Registration**: Fields for Full Name, Mobile Number (+91 format), Email, Password, and Role selection.
- **Login**: Email/Password and Mobile Phone OTP options, plus integrated Google One-Tap via Jetpack Credential Manager.
- **Forgot Password & OTP Modal**: Verification code input with countdown resend timer.
- **Compliance Links**: Terms of Service and Privacy Policy dialog links.
- **Session State**: Secure state in `AuthManager` with reactive `AuthState`.

### Screen 2: Home Dashboard
- **Welcome Header**: Personalized customer greeting with active notification bell badge.
- **Global Search Bar**: Instant search across all products, materials, and categories.
- **Primary Category Cards**: Interactive visual cards for **Main Gates**, **Safety Grills**, **Balcony Railings**, **Staircases**, and **Custom Fabrication**.
- **Quick-Action Shortcuts Grid**:
  1. *Book a Service* (Site measurement, installation, repair)
  2. *Upload Photos* (Reference designs, CAD drawings, site pictures)
  3. *Request a Quote* (Detailed requirement builder)
  4. *Track Orders* (Live status with reference number lookup)
- **Featured Products & Recent Designs**: Dynamic carousel populated from live catalogue repository.

### Screen 3: Product Catalogue & Product Details
- **Visual Grid**: High-resolution fabrication photography with dual-column adaptive layout.
- **Product Card Metadata**: Product Name, Category, Material Grade, Finish Options, Default Dimensions, and Indicative Rate (`₹/sq.ft`, `₹/running ft`, `₹/kg`).
- **Filter & Sort Bar**: Search query, Sector toggle (Residential vs Industrial), Material chips, and Sorting (Popularity, Price Low-to-High, Lead Time).
- **Product Details Bottom Sheet / Screen**:
  - Full-screen image preview, technical description, material grade specs (MS IS 2062, SS 304/316, GI).
  - Customizable dimension calculators (Length × Height in feet/meters).
  - Primary CTA: **"Add to My Requirement"** or **"Enquire Now"**.

### Screen 4: Photo & Design Upload
- **Media Ingestion**: Camera capture option and Gallery file picker for CAD drawings (PDF) and reference photographs.
- **Multiple Image Preview Gallery**: Horizontal scrollable previews with individual *Replace* and *Remove* action buttons.
- **Requirement Attributes Form**:
  - Project Title & Detailed Description.
  - Approximate Dimensions (Width, Height, Depth, Units).
  - Preferred Material (MS, SS 304, SS 316, GI, Aluminium) & Finish (Powder Coat, Enamel, Galvanized).
  - Special site conditions / structural notes.
- **Submission Action**: Primary button generating draft requirement with immediate feedback.

### Screen 5: Service Booking
- **Service Type Selector**: Site Measurement & Inspection, Fabrication & Erection, Repair & Maintenance, Automation Retrofit.
- **Project Specifics**: Linked reference to uploaded designs or custom scope notes.
- **Scheduling**: Preferred Date picker and Preferred Time Window (Morning 9-12, Afternoon 12-4, Evening 4-7).
- **Site Address Form**: Street address, Landmark, City, Pincode with "Use Saved Address" selector.
- **Photo Attachments**: Directly attach site photos or existing gate/grill locations.
- **Booking Summary & Confirmation**: Complete review dialog with generated unique reference: `CJF-BK-2026-XXXX`.

### Screen 6: Quotation Screen
- **Header**: Quotation Reference (`CJF-QT-XXXX`), Date of Issue, Validity Period (e.g. 15 days), and Status Badge (*Draft* vs *Officially Issued* vs *Accepted*).
- **Project & Customer Information**: Customer name, site address, and assigned verified vendor workshop.
- **Itemized Bill of Quantities (BOQ)**:
  - Base Materials (Grade, thickness, weight/area, unit rate, material total).
  - Labour & Machining (Laser cutting, welding, bending).
  - Surface Finishing (Powder coating, hot-dip galvanizing).
  - Transportation & On-site Erection charges.
  - Applicable GST (18%) and Grand Total in INR (`₹`).
- **Action Toolbar**:
  - *View Digital Invoice* / *Export Summary* / *Share via WhatsApp*.
  - *Accept Quotation* (Advances status to Order).
  - *Reject Quotation* (With optional reason).
  - *Request Revision* (Send message to fabricator).

### Screen 7: Order Tracking & Milestones
- **Order Overview**: Order ID (`CJF-ORD-XXXX`), Associated RFQ/Quote, Target Delivery Date, Total Value.
- **9-Stage Visual Progress Timeline**:
  1. *Booking Confirmed*
  2. *Site Measurement*
  3. *Quotation Approved*
  4. *Advance Paid*
  5. *Fabrication in Progress*
  6. *Quality Check (QC)*
  7. *Ready for Delivery*
  8. *Installation at Site*
  9. *Completed & Handover*
- **Progress Photographs**: Photos taken during shop fabrication and pre-dispatch inspection.
- **Payment Milestones**: Advance (40%), Pre-dispatch (40%), Post-installation balance (20%).
- **Support Contact**: One-tap direct WhatsApp / phone call to project coordinator and workshop supervisor.

### Screen 8: Notifications Centre
- **Date-Grouped Feed**: Categorized into *Today*, *Yesterday*, and *Earlier*.
- **Notification Cards**:
  - Booking confirmed, Fabricator assigned.
  - Quotation ready for review.
  - Advance payment acknowledged.
  - Milestone updates (e.g., "Laser cutting completed", "Out for galvanizing").
  - Delivery & Installation reminders.
- **Interactive Affordances**: Read/Unread badge indicators, "Mark All as Read", notification preference toggles, and direct tap-to-navigate to corresponding Order, Quotation, or Booking.

### Screen 9: Customer Profile & Settings
- **Customer Identity**: Full Name, Verified Mobile Number, Email, Profile Avatar placeholder.
- **Saved Site Addresses**: Multi-address management (Home, Site Project, Office) with Add/Edit/Delete actions.
- **Activity Hub Tabs**:
  - *My Bookings* (List of past and active site measurement bookings).
  - *My Quotations* (Historical and pending quotation records).
  - *My Orders* (Active and completed orders).
- **Preferences & Security**: Notification alerts, preferred measurement units (feet/inches vs mm), Change Password, and Logout.

### Screen 10: Admin Dashboard & Portal
- **Role-Protected Surface**: Accessible via role switcher or administrative credentials.
- **KPI Metrics Cards**: Total Active RFQs, Pending Bookings, Quotations Awaiting Approval, Work-in-Progress Orders, Total GMV.
- **Management Panes**:
  - *Bookings & Enquiries*: Review customer bookings, assign field measurement engineers.
  - *RFQ & Vendor Assignment*: Route custom requests to verified fabricator workshops.
  - *Quotation Generation*: Review or adjust fabricator quotations before official issuance.
  - *Order Milestone Progression*: Advance order through the 9 lifecycle steps with notes and photo uploads.
  - *Catalogue Management*: Enable/disable products, update benchmark rates.

---

## 3. UI/UX Design System & Theme Alignment

- **Palette**:
  - Primary Background: Industrial Charcoal Black (`#121316`).
  - Card & Surface: Industrial Surface (`#1A1D24`), Container (`#232834`), Border (`#343B4B`).
  - Accent Colours: Forge Amber / Industrial Orange (`#F59E0B`, `#FF8A00`), Laser Cyan (`#06B6D4`), Emerald Green (`#10B981`).
  - Typography: Clean M3 typography with bold industrial headers and accessible high-contrast text.
- **Responsive Layout**: Designed for Android handheld with fluid container limits (`Modifier.widthIn(max = 640.dp)`) for tablets/foldables.
- **Accessibility**: Minimum 48.dp touch targets, semantic content descriptions, and clear active states.

---

## 4. Technical Architecture & Data Models

- **State Management**: `FabricationViewModel` managing `StateFlow` for Bookings, Quotations, Orders, Notifications, User Profiles, and Catalogue.
- **Data Persistence & Sync**:
  - `DataRepository` handling real-time synchronization with Firebase Firestore (`projects`, `rfq_sync`, `quotes_sync`, `bookings_sync`, `orders_sync`).
  - Authoritative PostgreSQL backend via `CjFabricationApi` for transactional consistency.
- **Local Cache**: Room Database (`AppDatabase`) for offline resilience.

---

## 5. Verification Plan

1. **Compilation**: Run `compile_applet` to verify all new Composables, models, and navigation paths compile cleanly.
2. **Automated Testing**: Run JVM Robolectric tests via `gradle :app:testDebugUnitTest` to validate booking calculations, quotation totals, order milestone progressions, and auth state flows.
3. **End-to-End Walkthrough**:
   - Customer authentication & registration.
   - Browsing products and adding to requirement.
   - Uploading custom photo design.
   - Booking a site measurement.
   - Viewing and accepting quotation.
   - Tracking order milestones through to completion.
   - Admin portal status updates and notification dispatch.
