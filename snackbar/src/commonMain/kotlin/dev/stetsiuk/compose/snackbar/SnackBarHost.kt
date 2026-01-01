package dev.stetsiuk.compose.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Clock

enum class SnackBarVisibilityState {
    InvisibleStart,
    Visible,
    InvisibleEnd
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProvideSnackBarHost(
    modifier: Modifier = Modifier,
    state: SnackBarHostState = rememberSnackBarHostState(),
    contentPadding: PaddingValues = SnackBarHostDefaults.contentPadding,
    contentAlignment: Alignment = SnackBarHostDefaults.alignment,
    content: @Composable () -> Unit,
) {
    val items = state.values
    val isAnyVisible = items.any { !it.isDisposed }
    val isAllDisposed = items.all { it.isDisposed }

    CompositionLocalProvider(
        LocalSnackBarHostState provides state
    ) {
        Box(modifier) {
            content()

            if (isAnyVisible) {
                Box(
                    modifier = Modifier
                        .align(contentAlignment)
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        .fillMaxWidth()
                        .padding(contentPadding)
                ) {
                    items.forEachIndexed { _, item ->
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            val density = LocalDensity.current
                            val anchors = remember {
                                DraggableAnchors {
                                    SnackBarVisibilityState.InvisibleStart at with(density) { -maxWidth.toPx() * 2f }
                                    SnackBarVisibilityState.Visible at 0f
                                    SnackBarVisibilityState.InvisibleEnd at with(density) { maxWidth.toPx() * 2 }
                                }
                            }
                            val anchorState = remember {
                                AnchoredDraggableState(
                                    initialValue = SnackBarVisibilityState.Visible,
                                    anchors = anchors,
                                )
                            }.also {
                                LaunchedEffect(it.currentValue) {
                                    if (it.currentValue != SnackBarVisibilityState.Visible) {
                                        item.isVisible = false
                                    }
                                }
                            }

                            AnimatedVisibility(
                                modifier = Modifier
                                    .graphicsLayer {
                                        translationX = anchorState.requireOffset()
                                    }
                                    .fillMaxWidth()
                                    .anchoredDraggable(
                                        state = anchorState,
                                        orientation = Orientation.Horizontal,
                                    ),
                                visible = item.isVisible,
                                enter = fadeIn() + scaleIn(
                                    initialScale = 0.9f
                                ) + expandVertically(
                                    clip = false,
                                    expandFrom = Alignment.Top
                                ),
                                exit = fadeOut() + scaleOut(
                                    targetScale = 0.9f,
                                ) + shrinkVertically(
                                    clip = false,
                                    shrinkTowards = Alignment.Top,
                                ),
                            ) {
                                item.content()

                                DisposableEffect(Unit) {
                                    onDispose { item.isDisposed = true }
                                }
                            }
                        }

                        LaunchedEffect(item.duration, item.start) {
                            delay(
                                ((item.start + item.duration.valueMs) - Clock.System.now()
                                    .toEpochMilliseconds())
                                    .coerceAtLeast(0)
                            )
                            item.isVisible = false
                        }

                        LaunchedEffect(Unit) {
                            item.isVisible = true
                        }
                    }
                }
            }

            LaunchedEffect(isAllDisposed) {
                if (isAllDisposed) {
                    state.values.clear()
                }
            }
        }
    }
}

object SnackBarHostDefaults {
    val contentPadding
        @Composable get() = PaddingValues(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            bottom = WindowInsets.navigationBars.union(WindowInsets.ime).asPaddingValues()
                .calculateBottomPadding() + 16.dp
        )

    val alignment = Alignment.BottomCenter
}