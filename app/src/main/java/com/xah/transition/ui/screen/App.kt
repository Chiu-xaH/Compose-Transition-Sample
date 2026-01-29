package com.xah.transition.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.xah.transition.ui.NavStackState
import com.xah.transition.ui.component.TransitionNavHost
import com.xah.transition.ui.model.NavCommand
import com.xah.transition.ui.state.LocalNavStackState

@Composable
fun App() {
    val nav = remember { NavStackState(startDestination = HomeDestination) }
    TransitionNavHost(state = nav)
}

@Composable
fun HomeScreen() {
    val navStackState = LocalNavStackState.current
    Box(modifier = Modifier.fillMaxSize().background(Color.Blue)) {
        LazyColumn {
            items(30) {
                ListItem(
                    headlineContent = { Text(it.toString()) },
                    trailingContent = {
                        Button(
                            onClick = {
                                navStackState.navigate(NavCommand.Push(SecondDestination(userId = "42")))
                            },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text("Go to SecondScreen")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SecondScreen(userId : String) {
    val navStackState = LocalNavStackState.current
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.inversePrimary)) {
        Button(
            onClick = {
                navStackState.navigate(NavCommand.Pop)
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("$userId Back to HomeScreen")
        }
    }
}
