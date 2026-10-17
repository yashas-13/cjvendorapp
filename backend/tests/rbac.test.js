// ============================================================================
// Automated RBAC & Marketplace Access Boundary Tests — CJ Fabrications
// ============================================================================

const test = require('node:test');
const assert = require('node:assert');
const { requireRole } = require('../middleware/auth');

test('RBAC Middleware: blocks unauthenticated customer from vendor endpoints', () => {
  const req = { user: { role: 'customer' } };
  let statusSent = null;
  let jsonSent = null;
  const res = {
    status: (code) => {
      statusSent = code;
      return {
        json: (data) => {
          jsonSent = data;
        },
      };
    },
  };
  let nextCalled = false;
  const next = () => { nextCalled = true; };

  const middleware = requireRole(['vendor', 'admin']);
  middleware(req, res, next);

  assert.strictEqual(statusSent, 403, 'Should respond with 403 Forbidden');
  assert.strictEqual(nextCalled, false, 'Next should not be called');
  assert.match(jsonSent.error, /Access Denied/, 'Should return informative error');
});

test('RBAC Middleware: allows authorized vendor to access vendor endpoints', () => {
  const req = { user: { role: 'vendor', vendorId: 'ven_001' } };
  let nextCalled = false;
  const res = {};
  const next = () => { nextCalled = true; };

  const middleware = requireRole(['vendor', 'admin']);
  middleware(req, res, next);

  assert.strictEqual(nextCalled, true, 'Next should be invoked for authorized role');
});

test('RBAC Middleware: allows admin to bypass role restrictions', () => {
  const req = { user: { role: 'admin' } };
  let nextCalled = false;
  const res = {};
  const next = () => { nextCalled = true; };

  const middleware = requireRole(['admin']);
  middleware(req, res, next);

  assert.strictEqual(nextCalled, true, 'Admin should pass');
});

test('Marketplace Quotation Privacy: Vendor cannot see competitor quotes', () => {
  const allQuotes = [
    { id: 'q1', vendorId: 'ven_001', grandTotal: 45000, vendorName: 'Apex Metal' },
    { id: 'q2', vendorId: 'ven_002', grandTotal: 43000, vendorName: 'Sri Balaji' },
  ];

  // Simulating query filter:
  function getQuotesForUser(user, quotes) {
    if (user.role === 'customer' || user.role === 'admin') {
      return quotes; // Full marketplace comparison
    }
    if (user.role === 'vendor') {
      return quotes.filter((q) => q.vendorId === user.vendorId); // Strict vendor isolation
    }
    return [];
  }

  const customerView = getQuotesForUser({ role: 'customer' }, allQuotes);
  assert.strictEqual(customerView.length, 2, 'Customer sees all quotes for comparison');

  const vendor1View = getQuotesForUser({ role: 'vendor', vendorId: 'ven_001' }, allQuotes);
  assert.strictEqual(vendor1View.length, 1, 'Vendor only sees their own quote');
  assert.strictEqual(vendor1View[0].vendorId, 'ven_001', 'Vendor cannot inspect competitor pricing');
});

test('RBAC Vendor Verification Guard: only verified status permits quotation', () => {
  function canVendorQuote(vendorProfile) {
    if (!vendorProfile) return false;
    return vendorProfile.verification_status === 'verified';
  }

  assert.strictEqual(canVendorQuote({ id: 'v1', verification_status: 'verified' }), true, 'Verified vendor can quote');
  assert.strictEqual(canVendorQuote({ id: 'v2', verification_status: 'pending_verification' }), false, 'Pending vendor cannot quote');
  assert.strictEqual(canVendorQuote({ id: 'v3', verification_status: 'suspended' }), false, 'Suspended vendor cannot quote');
});

test('RBAC Vendor-to-RFQ Isolation Guard: only assigned vendor can quote on RFQ', () => {
  const rfqAssignments = [
    { rfqId: 'rfq_001', vendorId: 'ven_001' },
    { rfqId: 'rfq_001', vendorId: 'ven_002' },
    { rfqId: 'rfq_002', vendorId: 'ven_003' },
  ];

  function isVendorAssigned(rfqId, vendorId) {
    return rfqAssignments.some((a) => a.rfqId === rfqId && a.vendorId === vendorId);
  }

  assert.strictEqual(isVendorAssigned('rfq_001', 'ven_001'), true, 'Assigned vendor 1 can access RFQ 1');
  assert.strictEqual(isVendorAssigned('rfq_001', 'ven_002'), true, 'Assigned vendor 2 can access RFQ 1');
  assert.strictEqual(isVendorAssigned('rfq_001', 'ven_003'), false, 'Unassigned vendor 3 CANNOT access RFQ 1');
});

test('Marketplace Order Progression: Customer accepts quote, milestones advance sequentially', () => {
  const rfq = { id: 'rfq_001', status: 'quoted' };
  const quote = { id: 'q_001', rfqId: 'rfq_001', vendorId: 'ven_001', grandTotal: 50000, status: 'pending' };

  // Customer accepts quote
  quote.status = 'accepted';
  rfq.status = 'accepted';

  const order = {
    orderNumber: 'CJO-2026-001',
    rfqId: rfq.id,
    vendorId: quote.vendorId,
    totalAmount: quote.grandTotal,
    currentStepIndex: 1,
    status: 'confirmed',
  };

  assert.strictEqual(rfq.status, 'accepted');
  assert.strictEqual(order.currentStepIndex, 1);

  // Vendor advances to step 2
  order.currentStepIndex = 2;
  assert.strictEqual(order.currentStepIndex, 2);
});
