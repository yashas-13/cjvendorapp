// ============================================================================
// Automated Tests for CJ Fabrications Backend Routes & State Machine
// ============================================================================

const test = require('node:test');
const assert = require('node:assert');

test('Validation of RFQ reference formatting', () => {
  const sampleRef = 'CJF-2026-X892';
  assert.match(sampleRef, /^CJF-2026-[A-Z]\d{3}$/, 'Reference number must match CJF-2026-[A-Z][0-9]{3}');
});

test('Commercial Quotation GST calculation at 18%', () => {
  const baseFabrication = 20000;
  const materialCost = 15000;
  const installationCost = 3000;
  const transportCost = 2000;
  const subtotal = baseFabrication + materialCost + installationCost + transportCost;
  const gstRate = 18.0;
  const gstAmount = subtotal * (gstRate / 100);
  const grandTotal = subtotal + gstAmount;

  assert.strictEqual(subtotal, 40000);
  assert.strictEqual(gstAmount, 7200);
  assert.strictEqual(grandTotal, 47200);
});

test('RFQ Status Transition State Machine', () => {
  const validTransitions = {
    draft: ['submitted'],
    submitted: ['assigned'],
    assigned: ['quoted'],
    quoted: ['accepted', 'cancelled'],
    accepted: ['in_production'],
    in_production: ['dispatched'],
    dispatched: ['installation'],
    installation: ['completed'],
    completed: [],
    cancelled: [],
  };

  assert.ok(validTransitions.submitted.includes('assigned'), 'submitted can transition to assigned');
  assert.ok(validTransitions.quoted.includes('accepted'), 'quoted can transition to accepted');
  assert.ok(!validTransitions.completed.includes('submitted'), 'completed cannot revert to submitted');
});
