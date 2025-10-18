package dev.johnoreilly.starwarscmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform