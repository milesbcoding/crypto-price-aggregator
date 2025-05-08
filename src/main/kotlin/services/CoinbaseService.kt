package com.example.services

import com.example.models.CoinbaseTickerResponse
import com.example.models.ExchangePriceService
import com.example.utilities.Config
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.*
import kotlinx.io.IOException
import mu.KotlinLogging

import java.util.concurrent.ConcurrentHashMap

class CoinbaseService(
    override val trackedPairs:List<String>,
    val client: HttpClient
)  : ExchangePriceService {

    private val logger = KotlinLogging.logger {}

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val prices = ConcurrentHashMap<String,Double>()

    init {
        load()
    }

    fun load() {
        val url:String? = Config.coinbaseURL
        val pollingInterval:Long = Config.pollingInterval ?: 10000
        val pairs:List<String> = trackedPairs

        if (pairs.isEmpty()) {
            logger.error{"Error: Fetching could not begin due to missing pairs. Aborting fetching." }
            return
        }

        if(url != null && url.isNotEmpty()){
            logger.info{ "Fetching coinbase tickers for $url" }
            startPeriodicFetching(pairs=pairs, interval = pollingInterval,url=url)
        } else {
            logger.error{ "Error: Fetching could not begin due to null url. Aborting fetching." }
            return
        }
    }

    fun startPeriodicFetching(pairs: List<String>, interval: Long, url:String) {
        scope.launch {
            while(isActive) {
                logger.info{ "Incoming Price Updates" }
                val jobs = pairs.map { pair ->
                    async{
                        try {
                            val formattedURL = url.replace("<PAIR>",pair)
                            val response: HttpResponse = client.get(formattedURL)
                            val tickerResponse: CoinbaseTickerResponse = response.body()

                            if (tickerResponse.price != null) {
                                logger.info{ "Price updated for $pair" }
                                prices.put(pair,tickerResponse.price)
                            } else {
                                logger.error{ "Error: ticker response did not contain price" }
                            }
                        }
                        catch (e: IOException) {
                            logger.error (e){ "Network Error while fetching pair $pair ${e.message}" }
                        }
                        catch (e: Exception) {
                            logger.error(e){ "Error fetching pair $pair ${e.message}" }
                        }
                    }
                }

                jobs.awaitAll()
                delay(interval)
            }
        }
    }

    override fun getPrice(symbol:String): Double? = prices[symbol]
}