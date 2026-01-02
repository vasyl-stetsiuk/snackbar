package dev.stetsiuk.compose.snackbar

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.TransformOrigin

internal fun Alignment.toTransformOrigin(): TransformOrigin = when (this) {
    Alignment.TopStart -> TransformOrigin(0f, 0f)
    Alignment.TopCenter -> TransformOrigin(0.5f, 0f)
    Alignment.TopEnd -> TransformOrigin(1f, 0f)
    Alignment.CenterStart -> TransformOrigin(0f, 0.5f)
    Alignment.Center -> TransformOrigin(0.5f, 0.5f)
    Alignment.CenterEnd -> TransformOrigin(1f, 0.5f)
    Alignment.BottomStart -> TransformOrigin(0f, 1f)
    Alignment.BottomCenter -> TransformOrigin(0.5f, 1f)
    Alignment.BottomEnd -> TransformOrigin(1f, 1f)
    else -> TransformOrigin(0.5f, 1f) // Default to bottom center
}