package dev.stetsiuk.compose.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Clock

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProvideSnackBarHost(
    modifier: Modifier = Modifier,
    state: SnackBarHostState = rememberSnackBarHostState(),
    contentPadding: PaddingValues = SnackBarHostDefaults.contentPadding,
    contentAlignment: Alignment = SnackBarHostDefaults.alignment,
    enter: EnterTransition = SnackBarHostDefaults.enterTransition,
    exit: ExitTransition = SnackBarHostDefaults.exitTransition,
    content: @Composable () -> Unit,
) {
    val items = state.values
    val isAnyVisible = items.isNotEmpty()

    CompositionLocalProvider(
        LocalSnackBarHostState provides state
    ) {
        Box(modifier) {
            content()

            if (isAnyVisible) {
                items.forEachIndexed { _, item ->
                    key(item.id) {
                        BoxWithConstraints(
                            modifier = Modifier
                                .align(contentAlignment)
                                .graphicsLayer {
                                    compositingStrategy = CompositingStrategy.Offscreen
                                }
                                .padding(contentPadding)
                        ) {
                            val itemState = item.state
                            AnimatedVisibility(
                                visible = itemState.isVisibleTarget,
                                enter = enter,
                                exit = exit,
                            ) {
                                item.content()

                                DisposableEffect(Unit) {
                                    onDispose { state.hide(item.id) }
                                }
                            }

                            LaunchedEffect(item.duration, itemState.start) {
                                delay(
                                    ((itemState.start + item.duration.valueMs) - Clock.System.now()
                                        .toEpochMilliseconds())
                                        .coerceAtLeast(0)
                                )
                                itemState.hide()
                            }

                            LaunchedEffect(Unit) {
                                itemState.show()
                            }
                        }
                    }
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
    val enterTransition = fadeIn() + scaleIn(
        initialScale = 0.9f
    ) + expandVertically(
        clip = false,
        expandFrom = Alignment.Top
    )
    val exitTransition = fadeOut()
}