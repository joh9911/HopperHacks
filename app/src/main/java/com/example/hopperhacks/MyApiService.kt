package com.example.hopperhacks

import ProductResponse
import android.telecom.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MyApiService {
    @GET("products")
    suspend fun getInfoFromBarcode(
        @Query("barcode") barcode: String,
        @Query("formatted") formatted: String = "y",
        @Query("key") apiKey: String
    ): Response<ProductResponse>
}