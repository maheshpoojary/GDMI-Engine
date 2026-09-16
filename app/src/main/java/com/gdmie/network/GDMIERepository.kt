package com.gdmie.network

import com.gdmie.network.model.GDMIERequest
import com.gdmie.network.model.GDMIEResponse

class GDMIERepository(
    private val api: GDMIEApiService =
        NetworkClient.retrofit.create(GDMIEApiService::class.java)
) {

    suspend fun calculate(request: GDMIERequest): GDMIEResponse {
        return api.calculate(request)
    }
}
