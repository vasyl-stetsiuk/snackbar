package dev.stetsiuk.compose.snackbar.model

import androidx.compose.ui.unit.Dp

data class SnackBarStackParams(
    val scaleRatio: Float,
    val alphaRatio: Float,
    val offsetStep: Dp,
    val maxVisibleItems: Int,
)