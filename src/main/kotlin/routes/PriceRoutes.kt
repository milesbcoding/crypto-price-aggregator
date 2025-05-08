package com.example.routes

import com.example.services.CoinbaseService
import com.example.services.PriceService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.json.Json
import mu.KotlinLogging

class PriceRoutes {

    private val logger = KotlinLogging.logger {}
    private var priceService : PriceService? = null

    init {
        configureExchangePriceService()
    }

    fun configureExchangePriceService(){
        logger.info{"loading tracked pairs"}
        val trackedPairs = listOf("BTC-USD","ETH-USD","ETH-BTC")
        val exchangePriceService = CoinbaseService(trackedPairs = trackedPairs, client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        })
        logger.info{"exchange price service initialised for pairs: ${trackedPairs.joinToString(",")}"}
        priceService = PriceService(exchangePriceService)
        logger.info{"price service initialised"}
    }

    fun register(route: Route) {
        route.get("/prices/{symbol}") {
            val symbol = call.parameters["symbol"]

            if (priceService == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    "API not fully initialised. Please contact the server administrator."
                )
                return@get
            }

            if (symbol == null) {
                call.respondText("Missing or incorrect symbol", status = HttpStatusCode.BadRequest)
                return@get
            }

            val priceResponse = priceService?.getPrice(symbol)

            priceResponse?.let { x ->
                call.respond(priceResponse)
            } ?: run {
                call.respond(
                    HttpStatusCode.NotFound,
                    "Price for $symbol was not found, check configuration to ensure this symbol is tracked"
                )
            }
        }
    }
}