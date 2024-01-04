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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.SwipeToDismissValue
import androidx.wear.compose.foundation.edgeSwipeToDismiss
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.layout.scrollAway
import com.google.android.horologist.compose.navscaffold.composable
import com.google.android.horologist.compose.pager.PagerScreen
import java.util.Locale
import kotlin.system.exitProcess

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

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    val navHostState =
        rememberSwipeDismissableNavHostState(swipeToDismissBoxState = swipeToDismissBoxState)
    val navController = rememberSwipeDismissableNavController()

    val pagerState = rememberPagerState(pageCount = { 4 })

    val workaround = true

    if (workaround) {
        LaunchedEffect(swipeToDismissBoxState.currentValue) {
            if (swipeToDismissBoxState.currentValue == SwipeToDismissValue.Dismissed) {
                closeApp(mainActivity)
            }
        }
    }

    SwipeDismissableNavHost(
        modifier = Modifier.background(Color.Black),
        startDestination = testRoute,
        navController = navController,
        state = navHostState
    ) {

        composable(route = testRoute) {

            PagerScreen(
                modifier = Modifier.edgeSwipeToDismiss(swipeToDismissBoxState),
                state = pagerState
            ) { selectedPage ->

                when (selectedPage) {
                    0 -> {
                        PageView {
                            ViewWithList(
                                pageNumber = selectedPage,
                                columnState = it,
                                mainActivity = mainActivity
                            )
                        }
                    }

                    1 -> {
                        PageView {
                            ViewWithList(
                                pageNumber = selectedPage,
                                columnState = it,
                                mainActivity = mainActivity
                            )
                        }
                    }

                    2 -> {
                        PageView {
                            ViewWithList(
                                pageNumber = selectedPage,
                                columnState = it,
                                mainActivity = mainActivity
                            )
                        }
                    }

                    3 ->
                        PageView {
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

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun PageView(content: @Composable BoxScope.(ScalingLazyColumnState) -> Unit) {

    val columnState = rememberColumnState(ScalingLazyColumnDefaults.responsive())

    ScreenScaffold(
        positionIndicator = { PositionIndicator(scalingLazyListState = columnState.state) },
        timeText = { CustomTimeText(columnState = columnState) },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        content(columnState)
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

private fun closeApp(activity: MainActivity) {
    activity.finishAndRemoveTask()
    exitProcess(0)
}