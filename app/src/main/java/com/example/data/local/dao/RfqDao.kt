package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.RfqEntity
import com.example.data.local.entity.RfqItemEntity
import com.example.data.local.entity.VendorQuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RfqDao {
    @Query("SELECT * FROM rfqs ORDER BY createdAt DESC")
    fun getAllRfqsFlow(): Flow<List<RfqEntity>>

    @Query("SELECT * FROM rfqs WHERE id = :id LIMIT 1")
    suspend fun getRfqById(id: String): RfqEntity?

    @Query("SELECT * FROM rfqs WHERE referenceNumber = :ref LIMIT 1")
    suspend fun getRfqByReference(ref: String): RfqEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRfq(rfq: RfqEntity)

    @Update
    suspend fun updateRfq(rfq: RfqEntity)

    @Query("SELECT * FROM rfq_items WHERE rfqId = :rfqId")
    suspend fun getItemsForRfq(rfqId: String): List<RfqItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRfqItems(items: List<RfqItemEntity>)

    @Query("SELECT * FROM vendor_quotes WHERE rfqId = :rfqId")
    fun getQuotesForRfqFlow(rfqId: String): Flow<List<VendorQuoteEntity>>

    @Query("SELECT * FROM vendor_quotes WHERE rfqId = :rfqId")
    suspend fun getQuotesForRfq(rfqId: String): List<VendorQuoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<VendorQuoteEntity>)

    @Update
    suspend fun updateQuote(quote: VendorQuoteEntity)
}
