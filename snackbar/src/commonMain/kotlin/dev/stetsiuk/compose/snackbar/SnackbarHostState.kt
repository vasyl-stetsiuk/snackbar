package dev.stetsiuk.compose.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

class SnackBarHostState {

    internal val values = mutableStateListOf<SnackBarData>()

    fun show(data: SnackBarData) {
        if (values.none { it.id == data.id }) {
            values.add(data)
        }
    }

    fun hide(id: String) {
        values.removeAll { it.id == id }
    }
}

@Composable
fun rememberSnackBarHostState() = remember { SnackBarHostState() }

val LocalSnackBarHostState = compositionLocalOf { SnackBarHostState() }