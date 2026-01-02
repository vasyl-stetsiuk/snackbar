package dev.stetsiuk.compose.snackbar

import androidx.compose.runtime.Composable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class SnackBarData @OptIn(ExperimentalUuidApi::class) constructor(
    val state: SnackBarState = SnackBarState(),
    val id: String = Uuid.random().toString(),
    val duration: Duration = Duration.Short,
    val content: @Composable () -> Unit,
) {
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