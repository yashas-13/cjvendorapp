package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represents a fabrication project listing / catalog item in the marketplace.
 * Synchronized in real-time between PostgreSQL backend database and Firebase Firestore.
 */
@JsonClass(generateAdapter = true)
data class ProjectListing(
    @Json(name = "id") val id: String = "",
    @Json(name = "category_id") val categoryId: String = "",
    @Json(name = "code") val code: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "materials_available") val materialsAvailable: List<String> = emptyList(),
    @Json(name = "finish_options") val finishOptions: List<String> = emptyList(),
    @Json(name = "indicative_rate_min") val indicativeRateMin: Int = 0,
    @Json(name = "indicative_rate_max") val indicativeRateMax: Int = 0,
    @Json(name = "rate_unit") val rateUnit: String = "sq. ft.",
    @Json(name = "default_dimensions") val defaultDimensions: String = "",
    @Json(name = "estimated_weight_kg") val estimatedWeightKg: Double = 0.0,
    @Json(name = "is_popular") val isPopular: Boolean = false,
    @Json(name = "lead_time_days") val leadTimeDays: Int = 14,
    @Json(name = "image_url") val imageUrl: String = "",
    @Json(name = "is_active") val isActive: Boolean = true,
    @Json(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDesignItem(): DesignItem {
        return DesignItem(
            id = id,
            categoryId = categoryId,
            code = code,
            title = title,
            description = description,
            materialsAvailable = materialsAvailable,
            finishOptions = finishOptions,
            indicativeRateMin = indicativeRateMin,
            indicativeRateMax = indicativeRateMax,
            rateUnit = rateUnit,
            defaultDimensions = defaultDimensions,
            estimatedWeightKg = estimatedWeightKg,
            isPopular = isPopular,
            leadTimeDays = leadTimeDays,
            imageUrl = imageUrl
        )
    }

    fun toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "categoryId" to categoryId,
            "code" to code,
            "title" to title,
            "description" to description,
            "materialsAvailable" to materialsAvailable,
            "finishOptions" to finishOptions,
            "indicativeRateMin" to indicativeRateMin,
            "indicativeRateMax" to indicativeRateMax,
            "rateUnit" to rateUnit,
            "defaultDimensions" to defaultDimensions,
            "estimatedWeightKg" to estimatedWeightKg,
            "isPopular" to isPopular,
            "leadTimeDays" to leadTimeDays,
            "imageUrl" to imageUrl,
            "isActive" to isActive,
            "updatedAt" to updatedAt
        )
    }

    companion object {
        fun fromDesignItem(design: DesignItem): ProjectListing {
            return ProjectListing(
                id = design.id,
                categoryId = design.categoryId,
                code = design.code,
                title = design.title,
                description = design.description,
                materialsAvailable = design.materialsAvailable,
                finishOptions = design.finishOptions,
                indicativeRateMin = design.indicativeRateMin,
                indicativeRateMax = design.indicativeRateMax,
                rateUnit = design.rateUnit,
                defaultDimensions = design.defaultDimensions,
                estimatedWeightKg = design.estimatedWeightKg,
                isPopular = design.isPopular,
                leadTimeDays = design.leadTimeDays,
                imageUrl = design.imageUrl,
                isActive = true,
                updatedAt = System.currentTimeMillis()
            )
        }

        @Suppress("UNCHECKED_CAST")
        fun fromFirestoreMap(data: Map<String, Any>): ProjectListing {
            return ProjectListing(
                id = data["id"] as? String ?: "",
                categoryId = data["categoryId"] as? String ?: "",
                code = data["code"] as? String ?: "",
                title = data["title"] as? String ?: "",
                description = data["description"] as? String ?: "",
                materialsAvailable = (data["materialsAvailable"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList(),
                finishOptions = (data["finishOptions"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList(),
                indicativeRateMin = (data["indicativeRateMin"] as? Number)?.toInt() ?: 0,
                indicativeRateMax = (data["indicativeRateMax"] as? Number)?.toInt() ?: 0,
                rateUnit = data["rateUnit"] as? String ?: "sq. ft.",
                defaultDimensions = data["defaultDimensions"] as? String ?: "",
                estimatedWeightKg = (data["estimatedWeightKg"] as? Number)?.toDouble() ?: 0.0,
                isPopular = data["isPopular"] as? Boolean ?: false,
                leadTimeDays = (data["leadTimeDays"] as? Number)?.toInt() ?: 14,
                imageUrl = data["imageUrl"] as? String ?: "",
                isActive = data["isActive"] as? Boolean ?: true,
                updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
}
