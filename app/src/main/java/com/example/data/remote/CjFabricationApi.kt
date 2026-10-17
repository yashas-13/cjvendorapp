package com.example.data.remote

import com.example.data.model.FabricationCategory
import com.example.data.model.FabricationOrder
import com.example.data.model.ProjectListing
import com.example.data.model.Quotation
import com.example.data.model.RfqRequest
import com.example.data.model.VendorProfile
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null
)

data class CreateRfqResponse(
    val id: String,
    val referenceNumber: String,
    val secureAccessToken: String,
    val whatsappMessage: String
)

data class AssignVendorRequest(
    val vendorIds: List<String>,
    val adminNotes: String = ""
)

data class AcceptQuoteRequest(
    val customerNotes: String = ""
)

data class UpdateMilestoneRequest(
    val stepIndex: Int,
    val status: String,
    val remarks: String
)

interface CjFabricationApi {
    @GET("api/health")
    suspend fun getHealth(): Response<ApiResponse<Map<String, Any>>>

    @GET("api/categories")
    suspend fun getCategories(): Response<ApiResponse<List<FabricationCategory>>>

    @GET("api/rfqs/{referenceNumber}")
    suspend fun getRfqByReference(
        @Path("referenceNumber") referenceNumber: String,
        @Header("X-RFQ-Token") token: String? = null
    ): Response<ApiResponse<RfqRequest>>

    @POST("api/rfqs")
    suspend fun createRfq(
        @Body rfq: RfqRequest
    ): Response<ApiResponse<CreateRfqResponse>>

    @GET("api/vendors/{vendorId}/rfqs")
    suspend fun getVendorAssignedRfqs(
        @Path("vendorId") vendorId: String,
        @Header("Authorization") authHeader: String? = null
    ): Response<ApiResponse<List<RfqRequest>>>

    @POST("api/rfqs/{id}/assign")
    suspend fun assignRfqToVendors(
        @Path("id") rfqId: String,
        @Body body: AssignVendorRequest,
        @Header("Authorization") authHeader: String? = null
    ): Response<ApiResponse<RfqRequest>>

    @GET("api/rfqs/{id}/quotes")
    suspend fun getQuotesForRfq(
        @Path("id") rfqId: String,
        @Header("X-RFQ-Token") token: String? = null
    ): Response<ApiResponse<List<Quotation>>>

    @POST("api/quotes")
    suspend fun submitQuotation(
        @Body quote: Quotation,
        @Header("Authorization") authHeader: String? = null
    ): Response<ApiResponse<Quotation>>

    @POST("api/quotes/{id}/accept")
    suspend fun acceptQuotation(
        @Path("id") quoteId: String,
        @Body body: AcceptQuoteRequest,
        @Header("X-RFQ-Token") token: String? = null
    ): Response<ApiResponse<FabricationOrder>>

    @GET("api/orders/{id}")
    suspend fun getOrderDetails(
        @Path("id") orderId: String
    ): Response<ApiResponse<FabricationOrder>>

    @POST("api/orders/{id}/milestones")
    suspend fun updateOrderMilestone(
        @Path("id") orderId: String,
        @Body body: UpdateMilestoneRequest,
        @Header("Authorization") authHeader: String? = null
    ): Response<ApiResponse<FabricationOrder>>

    @GET("api/projects")
    suspend fun getProjectListings(
        @Query("categoryId") categoryId: String? = null,
        @Query("search") search: String? = null
    ): Response<ApiResponse<List<ProjectListing>>>

    @POST("api/projects")
    suspend fun createOrUpdateProject(
        @Body project: ProjectListing,
        @Header("Authorization") authHeader: String? = null
    ): Response<ApiResponse<ProjectListing>>

    companion object {
        const val DEFAULT_BASE_URL = "https://parkzone.in/cjfabrication/"

        fun create(baseUrl: String = DEFAULT_BASE_URL): CjFabricationApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(CjFabricationApi::class.java)
        }
    }
}

