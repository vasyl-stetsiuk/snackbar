package dev.stetsiuk.compose.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.stetsiuk.compose.snackbar.model.SnackBarStackParams
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.time.Clock

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProvideSnackBarHost(
    modifier: Modifier = Modifier,
    state: SnackBarHostState = rememberSnackBarHostState(),
    contentPadding: PaddingValues = SnackBarHostDefaults.contentPadding(),
    contentAlignment: Alignment = SnackBarHostDefaults.alignment,
    enter: EnterTransition = SnackBarHostDefaults.enterTransition,
    exit: ExitTransition = SnackBarHostDefaults.exitTransition,
    stackParams: SnackBarStackParams = SnackBarHostDefaults.stackParams,
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
                val transformOrigin = contentAlignment.toTransformOrigin()
                val visibleItems = items.takeLast(stackParams.maxVisibleItems)

                visibleItems.forEachIndexed { index, item ->
                    key(item.id) {
                        val stackIndex = visibleItems.size - 1 - index
                        val scale by animateFloatAsState(stackParams.scaleRatio.pow(stackIndex))
                        val alpha by animateFloatAsState(stackParams.alphaRatio.pow(stackIndex))
                        val offsetY by animateDpAsState(stackParams.offsetStep * stackIndex)

                        BoxWithConstraints(
                            modifier = Modifier
                                .align(contentAlignment)
                                .graphicsLayer {
                                    translationY = offsetY.toPx()
                                    this.transformOrigin = transformOrigin
                                    compositingStrategy = CompositingStrategy.Offscreen
                                }
                                .padding(contentPadding)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    this.alpha = alpha
                                }
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
    private const val DEFAULT_DAMPING_RATIO = Spring.DampingRatioMediumBouncy
    private const val DEFAULT_STIFFNESS = Spring.StiffnessLow
    private val floatSpring = spring<Float>(DEFAULT_DAMPING_RATIO, DEFAULT_STIFFNESS)
    private val intSizeSpring = spring<IntSize>(DEFAULT_DAMPING_RATIO, DEFAULT_STIFFNESS)

    @Composable
    fun contentPadding(bottomPlus: Dp = 16.dp) = PaddingValues(
        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
        bottom = WindowInsets.navigationBars.union(WindowInsets.ime).asPaddingValues()
            .calculateBottomPadding() + bottomPlus
    )

    val alignment = Alignment.BottomCenter
    val enterTransition = fadeIn(floatSpring) + scaleIn(
        initialScale = 0.9f,
    ) + expandVertically(
        clip = false,
        expandFrom = Alignment.Top,
        animationSpec = intSizeSpring
    )
    val exitTransition = fadeOut(floatSpring)
    val stackParams = SnackBarStackParams(
        scaleRatio = 0.95f,
        alphaRatio = 0.6f,
        offsetStep = (-8).dp,
        maxVisibleItems = 6
    )
}