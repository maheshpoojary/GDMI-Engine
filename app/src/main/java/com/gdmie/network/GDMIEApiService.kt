package com.gdmie.network

import com.gdmie.network.model.GDMIERequest
import com.gdmie.network.model.GDMIEResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GDMIEApiService {

    @POST("api/v1/calculate")
    suspend fun calculate(
        @Body request: GDMIERequest
    ): GDMIEResponse
}
