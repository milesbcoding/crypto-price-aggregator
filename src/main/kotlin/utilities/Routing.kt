package com.example.utilities

import com.example.routes.PriceRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val priceRoutes = PriceRoutes()

    routing {
        priceRoutes.register(this)
    }
}
