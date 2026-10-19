package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Quotation
import com.example.data.model.QuotationStatus
import com.example.data.model.RfqItem
import com.example.data.model.RfqRequest
import com.example.data.model.RfqStatus
import com.example.data.repository.FabricationRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var repository: FabricationRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        repository = FabricationRepository(context)
    }

    @Test
    fun `read string from context validates app name`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("CJ Fabrications", appName)
    }

    @Test
    fun `rfq item calculates total area and running length accurately`() {
        val item = RfqItem(
            productType = "Main Gate",
            title = "Laser Cut Gate",
            lengthFt = 12.0,
            heightFt = 6.5,
            material = "Mild Steel",
            materialGrade = "IS 2062",
            finish = "Powder Coated",
            quantity = 2
        )

        // 12.0 * 6.5 * 2 = 156.0 sq. ft.
        assertEquals(156.0, item.totalAreaSqFt, 0.01)
        assertEquals(24.0, item.runningLengthFt, 0.01)
    }

    @Test
    fun `submit rfq generates unique reference and assigns status`() {
        val item = RfqItem(
            productType = "SS Balcony Railing",
            title = "SS 304 Glass Railing",
            lengthFt = 30.0,
            heightFt = 3.5,
            material = "SS 304",
            materialGrade = "Jindal Architectural",
            finish = "Hairline Satin",
            quantity = 1
        )

        val submittedRfq = repository.submitRfq(
            customerName = "Vikram Reddy",
            customerPhone = "+91 98450 12345",
            customerEmail = "vikram@example.com",
            projectType = "Homeowner",
            siteAddress = "Koramangala 4th Block",
            city = "Bengaluru",
            pincode = "560034",
            targetCompletionDate = "30-Nov-2026",
            budgetRange = "₹60,000",
            customItems = listOf(item)
        )

        assertNotNull(submittedRfq)
        assertTrue(submittedRfq.referenceNumber.startsWith("CJF-2026-"))
        assertEquals("Vikram Reddy", submittedRfq.customerName)
        assertEquals(1, submittedRfq.items.size)
        // Check that quotes were generated
        val quotes = repository.quotationsMap.value[submittedRfq.id]
        assertNotNull(quotes)
        assertTrue(quotes!!.isNotEmpty())
    }

    @Test
    fun `format whatsapp message includes rfq reference and itemized specifications`() {
        val item = RfqItem(
            productType = "Main Gate",
            title = "Motorized Sliding Gate",
            lengthFt = 16.0,
            heightFt = 7.0,
            material = "Mild Steel",
            materialGrade = "IS 2062",
            finish = "Jet Black RAL 9005",
            quantity = 1,
            installationRequired = true
        )

        val rfq = RfqRequest(
            referenceNumber = "CJF-2026-T881",
            customerName = "Anita Desai",
            customerPhone = "9988776655",
            siteAddress = "Indiranagar",
            city = "Bengaluru",
            pincode = "560038",
            targetCompletionDate = "10-Dec-2026",
            items = listOf(item)
        )

        val msg = repository.formatWhatsAppMessage(rfq)
        assertTrue(msg.contains("CJF-2026-T881"))
        assertTrue(msg.contains("Anita Desai"))
        assertTrue(msg.contains("Motorized Sliding Gate"))
        assertTrue(msg.contains("16.0 ft (W) x 7.0 ft (H)"))
        assertTrue(msg.contains("Jet Black RAL 9005"))
        assertTrue(msg.contains("https://parkzone.in/cjfabrication/track?ref=CJF-2026-T881"))
    }

    @Test
    fun `quotation calculates grand total and gst properly`() {
        val quote = Quotation(
            rfqId = "rfq_test",
            rfqReference = "CJF-2026-X111",
            vendorId = "ven_001",
            vendorName = "Apex Metal",
            vendorCity = "Bengaluru",
            vendorRating = 4.8,
            isGstVerified = true,
            baseFabricationCost = 20000.0,
            materialCost = 15000.0,
            installationCost = 3000.0,
            transportCost = 2000.0,
            gstRatePercent = 18.0,
            leadTimeDays = 14,
            validityDate = "15 Days",
            materialGradeGuarantee = "SAIL IS 2062",
            technicalNotes = "Ready"
        )

        // Subtotal = 20k + 15k + 3k + 2k = 40,000
        assertEquals(40000.0, quote.subtotal, 0.01)
        // GST (18%) = 7,200
        assertEquals(7200.0, quote.gstAmount, 0.01)
        // Grand Total = 47,200
        assertEquals(47200.0, quote.grandTotal, 0.01)
    }

    @Test
    fun `accepting quotation transitions rfq to accepted and creates fabrication order`() {
        val initialRfq = repository.rfqList.value.first()
        val quotes = repository.quotationsMap.value[initialRfq.id]
        assertNotNull(quotes)
        val targetQuote = quotes!!.first()

        val order = repository.acceptQuotation(initialRfq.id, targetQuote.id)
        assertNotNull(order)
        assertEquals(initialRfq.referenceNumber, order!!.rfqReference)
        assertEquals(targetQuote.vendorName, order.vendorName)
        assertEquals(targetQuote.grandTotal, order.totalAmount, 0.01)
        assertEquals(6, order.milestones.size)

        // Verify RFQ status updated
        val updatedRfq = repository.getRfqById(initialRfq.id)
        assertEquals(RfqStatus.ACCEPTED, updatedRfq!!.status)
    }

    @Test(expected = IllegalStateException::class)
    fun `rbac enforcement unverified vendor cannot submit quotation`() {
        val initialRfq = repository.rfqList.value.first()
        // ven_004 is PENDING_VERIFICATION
        repository.submitVendorQuotation(
            rfqId = initialRfq.id,
            vendorId = "ven_004",
            baseFabrication = 20000.0,
            materialCost = 10000.0,
            installationCost = 3000.0,
            transportCost = 1000.0,
            leadTimeDays = 14,
            validity = "15 Days",
            materialGuarantee = "Standard",
            siteVisitNeeded = false,
            notes = "Test"
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `rbac enforcement unassigned vendor cannot submit quotation on restricted rfq`() {
        val initialRfq = repository.rfqList.value.first()
        // ven_003 is verified but not assigned to demo rfq
        repository.submitVendorQuotation(
            rfqId = initialRfq.id,
            vendorId = "ven_003",
            baseFabrication = 20000.0,
            materialCost = 10000.0,
            installationCost = 3000.0,
            transportCost = 1000.0,
            leadTimeDays = 14,
            validity = "15 Days",
            materialGuarantee = "Standard",
            siteVisitNeeded = false,
            notes = "Test"
        )
    }

    @Test
    fun `admin can verify vendor and permit quotation submission`() {
        val initialRfq = repository.rfqList.value.first()
        // Assign ven_004 to RFQ
        repository.assignRfqToVendor(initialRfq.id, "ven_004")
        // Admin verifies ven_004
        repository.updateVendorVerificationStatus("ven_004", com.example.data.model.VendorVerificationStatus.VERIFIED)

        // Now submission succeeds
        val quote = repository.submitVendorQuotation(
            rfqId = initialRfq.id,
            vendorId = "ven_004",
            baseFabrication = 22000.0,
            materialCost = 11000.0,
            installationCost = 2500.0,
            transportCost = 1200.0,
            leadTimeDays = 10,
            validity = "15 Days",
            materialGuarantee = "Audited Grade",
            siteVisitNeeded = true,
            notes = "Now verified"
        )
        assertNotNull(quote)
        assertEquals("Omkar Steel Fabrications (New Applicant)", quote!!.vendorName)
    }

    @Test
    fun `vendor to customer direct whatsapp url formatting`() {
        val initialRfq = repository.rfqList.value.first()
        val quote = repository.quotationsMap.value[initialRfq.id]!!.first()

        val directUrl = repository.generateDirectVendorWhatsAppUrl(initialRfq, quote)
        assertTrue(directUrl.contains("api.whatsapp.com/send?phone="))
        assertTrue(directUrl.contains(quote.vendorPhone.replace("[^0-9]".toRegex(), "")))
    }

    @Test
    fun `vendor advances order milestone successfully in marketplace workflow`() {
        val initialRfq = repository.rfqList.value.first()
        val quote = repository.quotationsMap.value[initialRfq.id]!!.first()
        val order = repository.acceptQuotation(initialRfq.id, quote.id)
        assertNotNull(order)

        // Advance to step 2 (CNC Cutting & Machining)
        repository.advanceOrderMilestone(order!!.id, 2, "CNC Plasma cutting completed for IS 2062 plates.")
        val updatedOrder = repository.orders.value.find { it.id == order.id }
        assertNotNull(updatedOrder)
        assertEquals(3, updatedOrder!!.currentStepIndex)
        val step2 = updatedOrder.milestones.find { it.stepIndex == 2 }
        assertEquals(com.example.data.model.MilestoneState.COMPLETED, step2!!.state)
        assertEquals("CNC Plasma cutting completed for IS 2062 plates.", step2.remarks)
        val step3 = updatedOrder.milestones.find { it.stepIndex == 3 }
        assertEquals(com.example.data.model.MilestoneState.IN_PROGRESS, step3!!.state)
    }

    @Test(expected = IllegalStateException::class)
    fun `admin suspension blocks vendor from submitting quotations`() {
        val initialRfq = repository.rfqList.value.first()
        // ven_001 is initially verified. Admin suspends ven_001.
        repository.updateVendorVerificationStatus("ven_001", com.example.data.model.VendorVerificationStatus.SUSPENDED)

        // Now ven_001 cannot submit quotes
        repository.submitVendorQuotation(
            rfqId = initialRfq.id,
            vendorId = "ven_001",
            baseFabrication = 18000.0,
            materialCost = 12000.0,
            installationCost = 2000.0,
            transportCost = 1000.0,
            leadTimeDays = 12,
            validity = "15 Days",
            materialGuarantee = "Standard",
            siteVisitNeeded = false,
            notes = "Attempt after suspension"
        )
    }

    @Test
    fun `customer can track multiple rfqs and view quotation status`() {
        val rfqList = repository.rfqList.value
        assertTrue(rfqList.isNotEmpty())
        val firstRef = rfqList.first().referenceNumber
        val found = repository.getRfqByReference(firstRef)
        assertNotNull(found)
        assertEquals(firstRef, found!!.referenceNumber)
    }

    @Test
    fun `createServiceBooking generates unique reference and creates notification`() {
        val booking = repository.createServiceBooking(
            serviceType = com.example.data.model.ServiceType.SITE_MEASUREMENT,
            customerName = "Anita Desai",
            customerPhone = "+91 98450 99887",
            customerEmail = "anita@example.com",
            siteAddress = "Plot 88, Indiranagar 100ft Road",
            landmark = "Near Metro Station",
            city = "Bengaluru",
            pincode = "560038",
            preferredDate = "28-Oct-2026",
            preferredTimeSlot = "Morning (09:00 AM - 12:00 PM)",
            projectDetails = "Balcony railing and window safety grills",
            attachedPhotoCount = 4
        )

        assertNotNull(booking)
        assertTrue(booking.referenceNumber.startsWith("CJF-BK-2026-"))
        assertEquals("Anita Desai", booking.customerName)
        assertEquals(com.example.data.model.BookingStatus.CONFIRMED, booking.status)

        // Verify it was added to serviceBookings StateFlow
        val bookings = repository.serviceBookings.value
        assertTrue(bookings.any { it.id == booking.id })

        // Verify a notification was triggered for Screen 8
        val notifs = repository.notifications.value
        assertTrue(notifs.any { it.referenceId == booking.id })
    }

    @Test
    fun `order tracking contains 9 defined milestones from booking to handover`() {
        val orders = repository.orders.value
        assertTrue("Seed order should be present", orders.isNotEmpty())
        val order = orders.first()
        assertEquals(9, order.milestones.size)
        assertEquals("Booking Confirmed", order.milestones[0].title)
        assertEquals("Completed & Handover", order.milestones[8].title)
        assertTrue("Step index should be within 1..9", order.currentStepIndex in 1..9)
    }

    @Test
    fun `quotation itemized BOQ computes exact subtotal 18 percent GST and grand total`() {
        val quote = Quotation(
            rfqId = "test_rfq",
            rfqReference = "CJF-2026-T101",
            vendorId = "ven_001",
            vendorName = "Apex Metal",
            vendorCity = "Bengaluru",
            vendorRating = 4.8,
            isGstVerified = true,
            baseFabricationCost = 20000.0,
            materialCost = 15000.0,
            installationCost = 3000.0,
            transportCost = 2000.0,
            gstRatePercent = 18.0,
            leadTimeDays = 14,
            validityDate = "15 Days",
            materialGradeGuarantee = "IS 2062 Grade E250",
            technicalNotes = "Standard notes"
        )

        // Subtotal = 20000 + 15000 + 3000 + 2000 = 40000.0
        assertEquals(40000.0, quote.subtotal, 0.01)
        // GST (18%) = 40000 * 0.18 = 7200.0
        assertEquals(7200.0, quote.gstAmount, 0.01)
        // Grand Total = 40000 + 7200 = 47200.0
        assertEquals(47200.0, quote.grandTotal, 0.01)
    }

    @Test
    fun `notifications centre supports marking single and all alerts as read`() {
        repository.addNotification(
            title = "Test Alert",
            message = "Test message for fabricator assign",
            category = com.example.data.model.NotificationCategory.BOOKING
        )
        val initialNotifs = repository.notifications.value
        val unreadItem = initialNotifs.first { !it.isRead }

        repository.markNotificationAsRead(unreadItem.id)
        val afterSingle = repository.notifications.value
        assertTrue(afterSingle.first { it.id == unreadItem.id }.isRead)

        repository.markAllNotificationsAsRead()
        val afterAll = repository.notifications.value
        assertTrue(afterAll.all { it.isRead })
    }

    @Test
    fun `customer profile updates preferences and manages saved addresses`() {
        val initialProfile = repository.customerProfile.value
        assertEquals("Feet & Inches (ft/in)", initialProfile.measurementUnit)

        val newAddress = com.example.data.model.SavedAddress(
            id = "test_addr_99",
            label = "Commercial Warehouse Site",
            fullAddress = "Plot 24, Peenya Industrial Area 2nd Stage",
            city = "Bengaluru",
            pincode = "560058"
        )
        repository.addSavedAddress(newAddress)
        assertTrue(repository.customerProfile.value.savedAddresses.any { it.id == "test_addr_99" })

        repository.deleteSavedAddress("test_addr_99")
        assertTrue(repository.customerProfile.value.savedAddresses.none { it.id == "test_addr_99" })
    }
}

