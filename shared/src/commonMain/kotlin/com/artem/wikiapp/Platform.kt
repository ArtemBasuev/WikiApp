package com.artem.wikiapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform