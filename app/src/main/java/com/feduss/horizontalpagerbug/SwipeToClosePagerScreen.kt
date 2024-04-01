package com.feduss.horizontalpagerbug

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.horizontalScrollAxisRange
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.HierarchicalFocusCoordinator
import com.google.android.horologist.compose.layout.PagerScaffold
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun SwipeToClosePagerScreen(modifier: Modifier, state: PagerState, content: @Composable (Int) -> Unit) {

    val screenWidth = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    var allowPaging by remember { mutableStateOf(true) }

    val originalTouchSlop = LocalViewConfiguration.current.touchSlop
    CustomTouchSlopProvider(newTouchSlop = originalTouchSlop * 2) {
        PagerScaffold(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(screenWidth) {
                    coroutineScope {
                        awaitEachGesture {
                            allowPaging = true
                            val firstDown =
                                awaitFirstDown(false, PointerEventPass.Initial)
                            val xPosition = firstDown.position.x
                            // Define edge zone of 15%
                            allowPaging = xPosition > screenWidth * 0.15f
                        }
                    }
                }
                .semantics {
                    horizontalScrollAxisRange = if (allowPaging) {
                        ScrollAxisRange(value = { state.currentPage.toFloat() },
                            maxValue = { 3f })
                    } else {
                        // signals system swipe to dismiss that they can take over
                        ScrollAxisRange(value = { 0f },
                            maxValue = { 0f })
                    }
                },
            pagerState = state
        ) {
            HorizontalPager(
                modifier = modifier,
                state = state,
                flingBehavior =
                PagerDefaults.flingBehavior(
                    state,
                    snapAnimationSpec = tween(10, 0),
                    pagerSnapDistance = PagerSnapDistance.atMost(1)
                ),
                userScrollEnabled = allowPaging
            ) { page ->
                ClippedBox(state) {
                    HierarchicalFocusCoordinator(requiresFocus = { page == state.currentPage }) {
                        content(page)
                    }
                }
            }
        }
    }
}

//MARK: - Horologist code

@Composable
fun ClippedBox(pagerState: PagerState, content: @Composable () -> Unit) {
    val shape = rememberClipWhenScrolling(pagerState)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .optionalClip(shape),
    ) {
        content()
    }
}

@Composable
private fun rememberClipWhenScrolling(state: PagerState): State<RoundedCornerShape?> {
    val shape = if (LocalConfiguration.current.isScreenRound) CircleShape else null
    return remember(state) {
        derivedStateOf {
            if (shape != null && state.currentPageOffsetFraction != 0f) {
                shape
            } else {
                null
            }
        }
    }
}

private fun Modifier.optionalClip(shapeState: State<RoundedCornerShape?>): Modifier {
    val shape = shapeState.value

    return if (shape != null) {
        clip(shape)
    } else {
        this
    }
}

// MARK: - Utility code by Steve Bower

@Composable
internal fun CustomTouchSlopProvider(
    newTouchSlop: Float,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalViewConfiguration provides CustomTouchSlop(
            newTouchSlop,
            LocalViewConfiguration.current
        )
    ) {
        content()
    }
}

class CustomTouchSlop(
    private val customTouchSlop: Float,
    currentConfiguration: ViewConfiguration
) : ViewConfiguration by currentConfiguration {
    override val touchSlop: Float
        get() = customTouchSlop
}