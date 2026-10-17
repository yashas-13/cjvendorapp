package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DesignItem
import com.example.data.model.ProjectListing
import com.example.data.repository.DataRepository
import com.example.data.repository.SyncSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DataRepositoryTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun `project listing converts correctly to and from design item`() {
        val design = DesignItem(
            id = "test_des_01",
            categoryId = "cat_main_gates",
            code = "CJ-TST-01",
            title = "Modern Sliding Gate",
            description = "High strength sliding gate",
            materialsAvailable = listOf("MS IS 2062", "SS 304"),
            finishOptions = listOf("Matte Black", "Anthracite Grey"),
            indicativeRateMin = 450,
            indicativeRateMax = 600,
            rateUnit = "sq. ft.",
            defaultDimensions = "14 ft x 7 ft",
            estimatedWeightKg = 250.0,
            isPopular = true,
            leadTimeDays = 14,
            imageUrl = "https://example.com/gate.jpg"
        )

        val project = ProjectListing.fromDesignItem(design)
        assertEquals("test_des_01", project.id)
        assertEquals("cat_main_gates", project.categoryId)
        assertEquals("CJ-TST-01", project.code)
        assertEquals(450, project.indicativeRateMin)
        assertTrue(project.isPopular)

        val convertedBack = project.toDesignItem()
        assertEquals(design.id, convertedBack.id)
        assertEquals(design.title, convertedBack.title)
        assertEquals(design.indicativeRateMax, convertedBack.indicativeRateMax)
    }

    @Test
    fun `project listing fires correct firestore map serialization and deserialization`() {
        val original = ProjectListing(
            id = "proj_001",
            categoryId = "cat_railings",
            code = "CJ-RL-101",
            title = "Glass Balustrade Railing",
            description = "Toughened glass with SS 304 studs",
            materialsAvailable = listOf("SS 304", "Toughened Glass"),
            finishOptions = listOf("Brushed Satin"),
            indicativeRateMin = 950,
            indicativeRateMax = 1400,
            rateUnit = "running ft.",
            defaultDimensions = "10 running ft.",
            estimatedWeightKg = 85.0,
            isPopular = true,
            leadTimeDays = 10,
            imageUrl = "https://example.com/railing.jpg",
            isActive = true
        )

        val map = original.toFirestoreMap()
        assertEquals("proj_001", map["id"])
        assertEquals("cat_railings", map["categoryId"])
        assertEquals(950, map["indicativeRateMin"])

        val deserialized = ProjectListing.fromFirestoreMap(map)
        assertEquals(original.id, deserialized.id)
        assertEquals(original.title, deserialized.title)
        assertEquals(original.materialsAvailable, deserialized.materialsAvailable)
        assertEquals(original.indicativeRateMin, deserialized.indicativeRateMin)
        assertTrue(deserialized.isActive)
    }

    @Test
    fun `data repository initializes with baseline listings and sync status`() = runBlocking {
        val repo = DataRepository(context)
        val listings = repo.projectListings.value
        assertTrue("Repository should initialize with baseline listings", listings.isNotEmpty())

        val status = repo.syncStatus.value
        assertNotNull(status)
        assertEquals(SyncSource.INITIAL_SEED, status.lastSource)
        assertTrue(status.itemsCount > 0)
        assertFalse(status.isSyncing)

        val designItems = repo.getDesignItems()
        assertEquals(listings.size, designItems.size)

        repo.cleanup()
    }
}
