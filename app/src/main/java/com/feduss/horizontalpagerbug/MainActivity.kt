package com.feduss.horizontalpagerbug

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.RevealActionType
import androidx.wear.compose.foundation.RevealState
import androidx.wear.compose.foundation.RevealValue
import androidx.wear.compose.foundation.rememberRevealState
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.SwipeToRevealCard
import androidx.wear.compose.material.SwipeToRevealDefaults
import androidx.wear.compose.material.SwipeToRevealPrimaryAction
import androidx.wear.compose.material.SwipeToRevealSecondaryAction
import androidx.wear.compose.material.SwipeToRevealUndoAction
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp(mainActivity = this)
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
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

                SwipeToClosePagerScreen(
                    modifier = Modifier,
                    state = pagerState
                ){ selectedPage ->

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
                                ViewWithSwipeToRevealCards(
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

@OptIn(ExperimentalHorologistApi::class, ExperimentalWearFoundationApi::class,
    ExperimentalWearMaterialApi::class
)
@Composable
fun ViewWithSwipeToRevealCards(pageNumber: Int, columnState: ScalingLazyColumnState, mainActivity: MainActivity) {

    val revealState = rememberRevealState()
    val coroutineScope = rememberCoroutineScope()

    val isWorkaroundEnabled = true

    // MARK: - Logging
    val revealStateValue: String = when(revealState.currentValue) {
        RevealValue.Revealed -> "Revealed"
        RevealValue.Revealing -> "Revealing"
        RevealValue.Covered -> "Covered"
        else -> "Error"
    }

    val lastActionType: String = when(revealState.lastActionType) {
        RevealActionType.PrimaryAction -> "PrimaryAction"
        RevealActionType.SecondaryAction -> "SecondaryAction"
        RevealActionType.UndoAction -> "UndoAction"
        RevealActionType.None -> "None"
        else -> "Error"
    }

    val revealStateHashCode = revealState.hashCode()

    Log.e("Test --> ", "hashCode: $revealStateHashCode, stateValue: $revealStateValue, lastActionType: $lastActionType")
    //end

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        columnState = columnState,
    ) {
        items(8) {
            SwipeToRevealCard(
                primaryAction = {
                    SwipeToRevealPrimaryAction(
                        revealState = revealState,
                        icon = { Icon(SwipeToRevealDefaults.Delete, "Delete") },
                        label = { Text("Delete") },
                        onClick = {
                            Toast.makeText(
                                mainActivity,
                                "Primary action!",
                                Toast.LENGTH_SHORT
                            )
                            .show()

                            resetRevealState(
                                coroutineScope = coroutineScope,
                                revealState = revealState,
                                isWorkaroundEnabled = isWorkaroundEnabled,
                                isUndoAction = false
                            )
                        }
                    )
                },
                secondaryAction = {
                    SwipeToRevealSecondaryAction(
                        revealState = revealState,
                        onClick = {
                            Toast.makeText(
                                mainActivity,
                                "Secondary action!",
                                Toast.LENGTH_SHORT
                            )
                            .show()

                            resetRevealState(
                                coroutineScope = coroutineScope,
                                revealState = revealState,
                                isWorkaroundEnabled = isWorkaroundEnabled,
                                isUndoAction = false
                            )
                        }
                    ) {
                        Icon(SwipeToRevealDefaults.MoreOptions, "More Options")
                    }
                },
                undoPrimaryAction = {
                    SwipeToRevealUndoAction(
                        revealState = revealState,
                        label = { Text("Primary Undo") },
                        onClick = {
                            Toast.makeText(
                                mainActivity,
                                "Primary Undo action!",
                                Toast.LENGTH_SHORT
                            )
                            .show()

                            resetRevealState(
                                coroutineScope = coroutineScope,
                                revealState = revealState,
                                isWorkaroundEnabled = isWorkaroundEnabled,
                                isUndoAction = true
                            )
                        }
                    )
                },
                undoSecondaryAction = {
                    SwipeToRevealUndoAction(
                        revealState = revealState,
                        label = { Text("Secondary Undo") },
                        onClick = {
                            Toast.makeText(
                                mainActivity,
                                "Secondary undo action!",
                                Toast.LENGTH_SHORT
                            )
                            .show()

                            resetRevealState(
                                coroutineScope = coroutineScope,
                                revealState = revealState,
                                isWorkaroundEnabled = isWorkaroundEnabled,
                                isUndoAction = true
                            )
                        }
                    )
                },
                onFullSwipe = {
                    Toast.makeText(
                        mainActivity,
                        "Full swipe action!",
                        Toast.LENGTH_SHORT
                    )
                    .show()

                    Log.e("Test --> ", "onFullSwipe scope")

                    //TODO: this is a workaround, check slack --> https://kotlinlang.slack.com/archives/C02GBABJUAF/p1711923876665509
                    if (isWorkaroundEnabled) {
                        Log.e("Test --> ", "reveal lastActionType manually to None")
                        revealState.lastActionType = RevealActionType.None
                    }

                    resetRevealState(
                        coroutineScope = coroutineScope,
                        revealState = revealState,
                        isWorkaroundEnabled = isWorkaroundEnabled,
                        isUndoAction = false
                    )
                },
                revealState = revealState,
            ){
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
}

@OptIn(ExperimentalWearFoundationApi::class)
private fun resetRevealState(
    coroutineScope: CoroutineScope,
    revealState: RevealState,
    isWorkaroundEnabled: Boolean,
    isUndoAction: Boolean
) {
    coroutineScope.launch {
        if (isWorkaroundEnabled) {
            revealState.snapTo(RevealValue.Covered)
            Log.e("Test --> ", "reveal state manually snapped to Covered")
        }
    }

    coroutineScope.launch {
        if(isUndoAction) {
            delay(3000)
        }

        if (isWorkaroundEnabled) {
            revealState.snapTo(RevealValue.Covered)
            Log.e("Test --> ", "reveal state manually snapped to Covered")
        }
    }
}