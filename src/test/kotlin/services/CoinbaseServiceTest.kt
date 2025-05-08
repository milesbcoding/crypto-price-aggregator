package com.example.services

import com.example.models.CoinbaseTickerResponse
import com.example.utilities.Config
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import kotlin.test.Test

class CoinbaseServiceTest {

    private val json = Json { ignoreUnknownKeys = true }

    @BeforeEach
    fun before() {
        Config.coinbaseURL = "https://api.exchange.coinbase.com/products/<PAIR>/ticker"
        Config.pollingInterval = 10000
    }

    @Test
    fun `prices map should return correct price for calls on tracked pairs`() = runTest {
        val testSymbol = "BTC-USD"
        val expectedPrice:Double = 97328.73
        val mockResponse = CoinbaseTickerResponse(price = expectedPrice)

        val engine = MockEngine { request ->
            respond(
                content = json.encodeToString(CoinbaseTickerResponse.serializer(), mockResponse),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val service = CoinbaseService(trackedPairs = listOf(testSymbol), client = client)

        val resultingPrice = waitForPrice(service,testSymbol)
        assertNotNull(resultingPrice, "Price was not fetched in time")
        assertEquals(expectedPrice, resultingPrice)
    }

    @Test
    fun `prices map should return null for calls on untracked pairs`() = runTest {
        val testSymbol = "BTC-USD"
        val expectedPrice:Double = 97328.73
        val mockResponse = CoinbaseTickerResponse(price = expectedPrice)

        val engine = MockEngine { request ->
            respond(
                content = json.encodeToString(CoinbaseTickerResponse.serializer(), mockResponse),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val service = CoinbaseService(trackedPairs = listOf(testSymbol), client = client)

        val resultingPrice = waitForPrice(service,"FKE-SYM")
        assertNull(resultingPrice)
    }

    @Test
    fun `prices map should be empty due to failed fetch cycle requests`()= runTest {
        val testSymbol = "BTC-USD"
        val expectedPrice:Double = 97328.73
        val mockResponse = CoinbaseTickerResponse(price = expectedPrice)

        val engine = MockEngine { request ->
            respond(
                content = "Not Found",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val service = CoinbaseService(trackedPairs = listOf(testSymbol), client = client)

        val resultingPrice = waitForPrice(service,testSymbol)
        assertNull(resultingPrice)
    }

    suspend fun waitForPrice(
        service: CoinbaseService,
        symbol: String,
        timeoutMs: Long = 1000,
        pollIntervalMs: Long = 50
    ): Double? {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMs) {
            val price = service.getPrice(symbol)
            if (price != null) return price
            delay(pollIntervalMs)
        }
        return null
    }

    //TODO: Write tests that encapsulate the logic of parallel coroutines fetching prices

}