package com.artem.wikiapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.HttpTimeout

fun wikiHttpClient(
    baseUrl: String,
    userAgent: String,
): HttpClient = HttpClient {
    expectSuccess = true
    defaultRequest {
        url(baseUrl)
        headers.append(HttpHeaders.UserAgent, userAgent)
        headers.append("Api-User-Agent", userAgent)
    }
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.INFO
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 10_000
        socketTimeoutMillis = 20_000
    }
}
