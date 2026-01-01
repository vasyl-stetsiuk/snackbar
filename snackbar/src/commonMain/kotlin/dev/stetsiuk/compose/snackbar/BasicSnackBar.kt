package dev.stetsiuk.compose.snackbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

@Composable
fun BasicSnackBar(
    color: Color,
    modifier: Modifier = Modifier,
    shape: Shape = BasicSnackBarDefaults.shape,
    contentColor: Color = BasicSnackBarDefaults.contentColor,
    border: BorderStroke? = null,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        border = border,
        shape = shape,
    ) {
        content.invoke()
    }
}

@Composable
fun BasicDraggableSnackBar(
    color: Color,
    modifier: Modifier = Modifier,
    shape: Shape = BasicSnackBarDefaults.shape,
    contentColor: Color = BasicSnackBarDefaults.contentColor,
    border: BorderStroke? = null,
    onDismissed: () -> Unit,
    positionalThreshold: (Float) -> Float = { distance -> distance * 0.5f },
    velocityThreshold: (Density) -> Float = { density -> with(density) { 80.dp.toPx() } },
    snapAnimationSpec: AnimationSpec<Float> = tween(300),
    decayAnimationSpec: DecayAnimationSpec<Float> = exponentialDecay(),
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        val density = LocalDensity.current
        val anchors = remember {
            DraggableAnchors {
                SnackBarVisibilityState.InvisibleStart at with(density) { -maxWidth.toPx() * 2f }
                SnackBarVisibilityState.Visible at 0f
                SnackBarVisibilityState.InvisibleEnd at with(density) { maxWidth.toPx() * 2f }
            }
        }
        val anchorState = remember {
            AnchoredDraggableState(
                initialValue = SnackBarVisibilityState.Visible,
                anchors = anchors,
                positionalThreshold = positionalThreshold,
                velocityThreshold = { velocityThreshold(density) },
                snapAnimationSpec = snapAnimationSpec,
                decayAnimationSpec = decayAnimationSpec
            )
        }.also {
            LaunchedEffect(it.currentValue) {
                if (it.currentValue != SnackBarVisibilityState.Visible) {
                    onDismissed()
                }
            }
        }

        BasicSnackBar(
            modifier = Modifier
                .graphicsLayer {
                    translationX = anchorState.requireOffset()
                }
                .fillMaxWidth()
                .anchoredDraggable(anchorState, Orientation.Horizontal),
            shape = shape,
            contentColor = contentColor,
            border = border,
            color = color,
            content = content
        )
    }
}

object BasicSnackBarDefaults {
    val shape = RoundedCornerShape(16.dp)
    val contentColor = Color.White
}