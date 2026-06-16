package com.example.homeinventory.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response

@JsonClass(generateAdapter = true)
data class OpenFoodFactsResponse(
    val status: Int,
    val product: ProductDto?
)

@JsonClass(generateAdapter = true)
data class ProductDto(
    @Json(name = "product_name") val productName: String?,
    @Json(name = "image_url") val imageUrl: String?
)

interface OpenFoodFactsApi {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): Response<OpenFoodFactsResponse>
}
