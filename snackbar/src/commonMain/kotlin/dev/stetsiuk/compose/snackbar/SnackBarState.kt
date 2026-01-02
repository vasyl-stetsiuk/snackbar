package dev.stetsiuk.compose.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.time.Clock

class SnackBarState {
    val start = Clock.System.now().toEpochMilliseconds()

    var isVisibleTarget by mutableStateOf(false)
        private set

    fun show() {
        isVisibleTarget = true
    }
    fun hide() {
        isVisibleTarget = false
    }
}

@Composable
fun rememberSnackBarState() = remember { SnackBarState() }