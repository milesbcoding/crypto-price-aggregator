package com.example.services

import com.example.models.ExchangePriceService
import com.example.models.PriceResponse
import java.time.Instant

class PriceService (val exchangePriceService: ExchangePriceService) {

    fun getPrice(symbol:String): PriceResponse? {
        val price:Double? = exchangePriceService.getPrice(symbol)
        return price?.let {
            PriceResponse(
                symbol = symbol,
                price = it,
                timestamp = Instant.now().toString()
            )
        }
    }

}