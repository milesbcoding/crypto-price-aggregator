package com.example.utilities

import io.ktor.server.application.ApplicationEnvironment

object Config {

    var coinbaseURL:String? = null
    var pollingInterval:Long? = null

    fun load(env: ApplicationEnvironment) {
        coinbaseURL = env.config.property("coinbase.url").getString()
        pollingInterval = env.config.property("coinbase.poll").getString().toLong()
    }

}