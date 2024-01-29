package com.feduss.horizontalpagerbug

import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.layout.scrollAway
import com.google.android.horologist.compose.pager.PagerScreen
import java.util.Locale

typealias ComposableFun = @Composable () -> Unit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp(mainActivity = this)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalHorologistApi::class)
@Composable
fun WearApp(mainActivity: MainActivity) {

    val testRoute = "testRoute"

    val firstPageColumnState = rememberColumnState()
    val secondPageColumnState = rememberColumnState()
    val thirdPageColumnState = rememberColumnState()
    val fourthPageColumnState = rememberColumnState()

    val columnStates = remember {
        listOf(
            firstPageColumnState,
            secondPageColumnState,
            thirdPageColumnState,
            fourthPageColumnState,
        )
    }

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    val navHostState =
        rememberSwipeDismissableNavHostState(swipeToDismissBoxState = swipeToDismissBoxState)
    val navController = rememberSwipeDismissableNavController()

    val pagerState = rememberPagerState(pageCount = { 4 })

    AppScaffold(
        timeText = { CustomTimeText(columnState = columnStates[pagerState.currentPage]) }
    ) {

        SwipeDismissableNavHost(
            modifier = Modifier.background(Color.Black),
            startDestination = testRoute,
            navController = navController,
            state = navHostState
        ) {

            composable(route = testRoute) {

                PagerScreen(
                    //modifier = Modifier.edgeSwipeToDismiss(swipeToDismissBoxState),
                    state = pagerState
                ) { selectedPage ->

                    when (selectedPage) {
                        0 -> {
                            PageView(columnState = columnStates[selectedPage]) {
                                ViewWithList(
                                    pageNumber = selectedPage,
                                    columnState = it,
                                    mainActivity = mainActivity
                                )
                            }
                        }

                        1 -> {
                            PageView(columnState = columnStates[selectedPage]) {
                                ViewWithList(
                                    pageNumber = selectedPage,
                                    columnState = it,
                                    mainActivity = mainActivity
                                )
                            }
                        }

                        2 -> {
                            PageView(columnState = columnStates[selectedPage]) {
                                ViewWithList(
                                    pageNumber = selectedPage,
                                    columnState = it,
                                    mainActivity = mainActivity
                                )
                            }
                        }

                        3 ->
                            PageView(columnState = columnStates[selectedPage]) {
                                ViewWithList(
                                    pageNumber = selectedPage,
                                    columnState = it,
                                    mainActivity = mainActivity
                                )
                            }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun PageView(columnState: ScalingLazyColumnState, content: @Composable BoxScope.(ScalingLazyColumnState) -> Unit) {

    ScreenScaffold(
        positionIndicator = { PositionIndicator(scalingLazyListState = columnState.state) },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)) {

        val originalTouchSlop = LocalViewConfiguration.current.touchSlop
        CustomTouchSlopProvider(newTouchSlop = originalTouchSlop * 2) {
            content(columnState)
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun CustomTimeText(columnState: ScalingLazyColumnState) {
    val timeSource = TimeTextDefaults.timeSource(
        DateFormat.getBestDateTimePattern(Locale.getDefault(), "HH:mm")
    )

    TimeText(
        modifier = Modifier.scrollAway(columnState),
        timeSource = timeSource,
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ViewWithList(pageNumber: Int, columnState: ScalingLazyColumnState, mainActivity: MainActivity) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        columnState = columnState,
    ) {
        items(8) {
            Card(
                modifier = Modifier.fillMaxWidth(0.95f),
                onClick = {
                    Toast.makeText(
                        mainActivity,
                        "Item $it, page $pageNumber tapped!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        maxLines = 1,
                        text = "Test $pageNumber"
                    )
                }
            }
        }
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