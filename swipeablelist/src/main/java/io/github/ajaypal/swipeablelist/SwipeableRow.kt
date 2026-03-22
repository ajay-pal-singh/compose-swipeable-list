package io.github.ajaypal.swipeablelist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

private const val CLOSE_DRAG_EPSILON_PX = 0.5f

/**
 * A swipeable row with optional left and right action content.
 *
 * @param modifier modifier applied to the row container.
 * @param isVisible controls row visibility. When false, the row exits with a vertical shrink animation.
 * @param isClosed when true, any currently open row animates back to its resting position.
 * @param onSwiped called when the row settles open on either side.
 * @param swipeThreshold drag distance required for a closed row to settle open. Narrow action panels use an
 * smaller effective threshold so they can still be opened.
 * @param animationDurationMillis duration used for open and close animations.
 * @param mainContent primary content that slides horizontally.
 * @param leftContent content revealed when swiping right.
 * @param rightContent content revealed when swiping left.
 */
@Composable
fun SwipeableRow(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    isClosed: Boolean = false,
    onSwiped: () -> Unit = {},
    swipeThreshold: Dp = 100.dp,
    animationDurationMillis: Int = 200,
    mainContent: @Composable BoxScope.() -> Unit,
    leftContent: @Composable (closeRow: () -> Unit) -> Unit = {},
    rightContent: @Composable (closeRow: () -> Unit) -> Unit = {},
) {
    var currentOffsetX by remember { mutableFloatStateOf(0f) }
    var previousOffsetX by remember { mutableFloatStateOf(0f) }
    var contentHeight by remember { mutableFloatStateOf(0f) }
    var leftPanelWidth by remember { mutableFloatStateOf(0f) }
    var rightPanelWidth by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val swipeThresholdPx = with(LocalDensity.current) { swipeThreshold.toPx() }

    fun closeRow() {
        coroutineScope.launch {
            animatedOffsetX.animateTo(0f, animationSpec = tween(animationDurationMillis))
            currentOffsetX = 0f
            previousOffsetX = 0f
        }
    }

    LaunchedEffect(isClosed) {
        if (isClosed && animatedOffsetX.value != 0f) {
            closeRow()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        label = "SwipeableRowVisibility",
        exit = shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(animationDurationMillis),
        ),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            val newOffsetX =
                                (currentOffsetX + dragAmount).coerceIn(-rightPanelWidth, leftPanelWidth)
                            currentOffsetX = newOffsetX
                            coroutineScope.launch {
                                animatedOffsetX.snapTo(newOffsetX)
                            }
                            change.consume()
                        },
                        onDragEnd = {
                            val targetOffset = settleTargetOffset(
                                currentOffsetX = currentOffsetX,
                                previousOffsetX = previousOffsetX,
                                leftPanelWidth = leftPanelWidth,
                                rightPanelWidth = rightPanelWidth,
                                swipeThresholdPx = swipeThresholdPx,
                            )

                            if (targetOffset != 0f) {
                                onSwiped()
                            }

                            coroutineScope.launch {
                                animatedOffsetX.animateTo(
                                    targetOffset,
                                    animationSpec = tween(animationDurationMillis),
                                )
                            }
                            currentOffsetX = targetOffset
                            previousOffsetX = targetOffset
                        },
                    )
                },
        ) {
            if (currentOffsetX >= 0) {
                ContentPanel(
                    content = leftContent,
                    onWidthChanged = { leftPanelWidth = it },
                    contentHeight = contentHeight,
                    isLeft = true,
                    closeRow = ::closeRow,
                )
            }

            if (currentOffsetX <= 0) {
                ContentPanel(
                    content = rightContent,
                    onWidthChanged = { rightPanelWidth = it },
                    contentHeight = contentHeight,
                    isLeft = false,
                    closeRow = ::closeRow,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged {
                        contentHeight = it.height.toFloat()
                    }
                    .offset { IntOffset(animatedOffsetX.value.roundToInt(), 0) },
            ) {
                mainContent()
            }
        }
    }
}

@Composable
private fun ContentPanel(
    content: @Composable (closeRow: () -> Unit) -> Unit,
    onWidthChanged: (Float) -> Unit,
    contentHeight: Float,
    isLeft: Boolean,
    closeRow: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(with(LocalDensity.current) { contentHeight.toDp() }),
        contentAlignment = if (isLeft) Alignment.CenterStart else Alignment.CenterEnd,
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .onSizeChanged { onWidthChanged(it.width.toFloat()) },
            horizontalArrangement = if (isLeft) Arrangement.Start else Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content(closeRow)
        }
    }
}

internal fun settleTargetOffset(
    currentOffsetX: Float,
    previousOffsetX: Float,
    leftPanelWidth: Float,
    rightPanelWidth: Float,
    swipeThresholdPx: Float,
): Float {
    val isRevealing = abs(currentOffsetX) - abs(previousOffsetX) > 0
    val effectiveLeftThreshold = revealThresholdPx(swipeThresholdPx, leftPanelWidth)
    val effectiveRightThreshold = revealThresholdPx(swipeThresholdPx, rightPanelWidth)

    return if (isRevealing) {
        when {
            currentOffsetX >= effectiveLeftThreshold -> leftPanelWidth
            currentOffsetX <= -effectiveRightThreshold -> -rightPanelWidth
            else -> 0f
        }
    } else {
        if (abs(previousOffsetX - currentOffsetX) > CLOSE_DRAG_EPSILON_PX) 0f else previousOffsetX
    }
}

internal fun revealThresholdPx(
    configuredThresholdPx: Float,
    panelWidthPx: Float,
): Float = min(configuredThresholdPx, panelWidthPx / 2f)
