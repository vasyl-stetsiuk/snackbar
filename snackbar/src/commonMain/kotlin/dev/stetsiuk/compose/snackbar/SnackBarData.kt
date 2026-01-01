package dev.stetsiuk.compose.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class SnackBarData @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val duration: Duration = Duration.Short,
    val content: @Composable () -> Unit
) {
    val start = Clock.System.now().toEpochMilliseconds()
    var isVisible by mutableStateOf(false)
        internal set
    var isDisposed by mutableStateOf(false)
        internal set

    sealed class Duration(
        open val valueMs: kotlin.Long
    ) {
        data object Short : Duration(2000L)
        data object Long : Duration(3500L)
        data class Custom(
            override val valueMs: kotlin.Long
        ): Duration(valueMs)
    }
}