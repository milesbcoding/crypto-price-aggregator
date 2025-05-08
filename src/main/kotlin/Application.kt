package com.example

import com.example.utilities.Config
import com.example.utilities.configureMonitoring
import com.example.utilities.configureRouting
import com.example.utilities.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    Config.load(environment)
    configureMonitoring()
    configureSerialization()
    configureRouting()
}
