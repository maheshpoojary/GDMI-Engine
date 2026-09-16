package com.gdmie.network

import com.gdmie.GDMEngine
import com.gdmie.GDMInput
import com.gdmie.GDMResult
import com.gdmie.network.model.GDMIERequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GDMIEEngineGateway {

    private val repository = GDMIERepository()

    suspend fun calculate(input: GDMInput): GDMResult {
        if (!NetworkConfig.NETWORK_ENABLED) {
            return GDMEngine.calculate(input)
        }

        return withContext(Dispatchers.IO) {
            try {
                val request = GDMIERequest(
                    presentValue = input.presentValue,
                    expectedValue = input.expectedValue,
                    targetValue = input.targetValue,
                    recentMomentum = input.recentMomentum,
                    immediateMomentum = input.immediateMomentum,
                    twoMinMarketAdvantage = input.twoMinMarketAdvantage,
                    exactMarketLine = input.exactMarketLine,
                    odds = input.odds,
                    oddsMovement = input.oddsMovement,
                    timingFactor = input.timingFactor,
                    riskFactor = input.riskFactor
                )

                val response = repository.calculate(request)

                GDMResult(
                    presentValue = response.presentValue,
                    expectedValue = response.expectedValue,
                    marketValue = response.marketValue,
                    gap = response.gap,
                    momentum = response.momentum,
                    risk = response.risk,
                    edge = response.edge,
                    confidence = response.confidence,
                    decision = response.decision,
                    explanation = response.explanation
                )
            } catch (e: Exception) {
                // Network unavailable → local GDMIE engine remains available.
                GDMEngine.calculate(input)
            }
        }
    }
}
