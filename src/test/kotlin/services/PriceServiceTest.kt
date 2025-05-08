package com.example.services

import com.example.models.ExchangePriceService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import kotlin.test.assertEquals

class PriceServiceTest {

    private lateinit var service: PriceService
    private lateinit var exchangePriceService: ExchangePriceService

    @BeforeEach
    fun before() {
        exchangePriceService = mockk()
        service = PriceService(exchangePriceService = exchangePriceService)
    }

    @Test
    fun `should return correct PriceResponse when ticker is tracked and price is available`() {
        val ticker = "BTC-USD"
        val price = 97328.73

        every { exchangePriceService.getPrice(ticker)} returns price

        val result = service.getPrice(ticker)

        assertEquals(price, result?.price)
        assertEquals(ticker, result?.symbol)
        assertNotNull(result?.timestamp)
    }

    @Test
    fun `should return null PriceResponse when ticker is not tracked or price is unavailable`() {
        val ticker = "FKE-SYM"

        every { exchangePriceService.getPrice(ticker)} returns null

        val result = service.getPrice(ticker)

        assertNull(result)
    }

}