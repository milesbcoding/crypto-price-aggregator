package com.example.models

interface ExchangePriceService {

    val trackedPairs:List<String>

    fun getPrice(symbol: String): Double?

}