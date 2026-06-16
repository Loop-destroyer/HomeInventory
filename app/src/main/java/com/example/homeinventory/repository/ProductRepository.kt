package com.example.homeinventory.repository

import com.example.homeinventory.api.OpenFoodFactsApi
import com.example.homeinventory.data.InventoryItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ProductRepository {

    private val api: OpenFoodFactsApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "HomeInventoryApp/1.0 - Android - com.example.homeinventory")
                    .build()
                chain.proceed(request)
            }
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        api = retrofit.create(OpenFoodFactsApi::class.java)
    }

    suspend fun getProductDetails(barcode: String): Result<Pair<String, String?>> {
        return try {
            val response = api.getProductByBarcode(barcode)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.status == 1 && body.product != null) {
                    val name = body.product.productName ?: "Unknown Product"
                    Result.success(Pair(name, body.product.imageUrl))
                } else {
                    Result.failure(Exception("Product not found"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
