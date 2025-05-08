package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class PriceResponse(val symbol:String, val price:Double, val timestamp:String)