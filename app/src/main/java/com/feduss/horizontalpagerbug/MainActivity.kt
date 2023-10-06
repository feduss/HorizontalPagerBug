package com.feduss.horizontalpagerbug

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.edgeSwipeToDismiss
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeSource
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.navscaffold.NavScaffoldViewModel
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.composable
import com.google.android.horologist.compose.pager.PagerScreen
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp(context = this)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalHorologistApi::class)
@Composable
fun WearApp(context: Context) {

    val testRoute = "testRoute"

    val timeSource = TimeTextDefaults.timeSource(
        DateFormat.getBestDateTimePattern(Locale.getDefault(), "HH:mm"))

    var currentPage by remember {
        mutableIntStateOf(0)
    }

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    val navHostState =
        rememberSwipeDismissableNavHostState(swipeToDismissBoxState = swipeToDismissBoxState)
    val navController = rememberSwipeDismissableNavController()

    val pagerState = rememberPagerState(pageCount =  {
        4
    })

    val firstColumnState = ScalingLazyColumnDefaults.belowTimeText().create()
    val secondColumnState = ScalingLazyColumnDefaults.belowTimeText().create()
    val thirdColumnState = ScalingLazyColumnDefaults.belowTimeText().create()
    val fourthColumnState = ScalingLazyColumnDefaults.belowTimeText().create()

    WearNavScaffold(
        modifier = Modifier.background(Color.Black),
        startDestination = testRoute,
        navController = navController,
        state = navHostState
    ) {

        composable(route = testRoute) {
            it.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off
            it.positionIndicatorMode = NavScaffoldViewModel.PositionIndicatorMode.On
            val modifier = if (currentPage == 0) {
                Modifier.edgeSwipeToDismiss(swipeToDismissBoxState)
            } else {
                Modifier.unswipeable()
            }

            PagerScreen(
                modifier = modifier,
                state = pagerState
            ) { selectedPage ->
                currentPage = pagerState.currentPage

                when (selectedPage) {
                    0 -> {
                        PageScaffold(
                            timeSource = timeSource,
                            columnState = firstColumnState,
                            content = { columnState ->
                                ViewWithList(
                                    pageNumber = selectedPage,
                                    columnState = columnState,
                                    context = context
                                )
                            }
                        )
                    }

                    1 -> {
                        PageScaffold(
                            timeSource = timeSource,
                            columnState = secondColumnState,
                            content = { columnState ->
                                ViewWithList(
                                    pageNumber = selectedPage,
                                    columnState = columnState,
                                    context = context
                                )
                            }
                        )
                    }

                    2 -> {
                        PageScaffold(
                            timeSource = timeSource,
                            columnState = thirdColumnState,
                            content = { columnState ->
                                ViewWithList(
                                    pageNumber = selectedPage,
                                    columnState = columnState,
                                    context = context
                                )
                            }
                        )
                    }

                    3 ->
                        PageScaffold(
                            timeSource = timeSource,
                            columnState = fourthColumnState,
                            content = { columnState ->
                                ViewWithList(
                                    pageNumber = selectedPage,
                                    columnState = columnState,
                                    context = context
                                )
                            }
                        )
                }
            }
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
private fun PageScaffold(
    timeSource: TimeSource,
    columnState: ScalingLazyColumnState,
    content: @Composable (ScalingLazyColumnState) -> Unit
) {

    Scaffold(
        timeText = {
            TimeText(
                timeSource = timeSource,
            )
        },
        positionIndicator = {
            PositionIndicator(columnState.state)
        }) {
        content(columnState)
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ViewWithList(pageNumber: Int, columnState: ScalingLazyColumnState, context: Context,) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        columnState = columnState,
    ) {
        items(8) {
            Card(
                modifier = Modifier.fillMaxWidth(0.95f),
                onClick = {
                    Toast.makeText(
                        context,
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

fun Modifier.unswipeable() =
    this.then(
        Modifier.draggable(
            orientation = Orientation.Horizontal,
            enabled = true,
            state = DraggableState {}
        )
    )
