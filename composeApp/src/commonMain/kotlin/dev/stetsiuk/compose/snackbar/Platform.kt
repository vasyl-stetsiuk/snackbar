package dev.stetsiuk.compose.snackbar

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform