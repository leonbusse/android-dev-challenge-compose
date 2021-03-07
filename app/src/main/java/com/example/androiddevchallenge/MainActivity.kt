/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.typography


@ExperimentalAnimationApi
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<CountDownViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.onStart(10)
    }
}

@ExperimentalAnimationApi
@Composable
fun MyApp(viewModel: CountDownViewModel) {
    val state: CountDownState by viewModel.countDownState
        .observeAsState(CountDownState.Setup)
    val initialCount: Int by viewModel.initialCount
        .observeAsState(0)

    if (state == CountDownState.Setup) {
        CountDownSetup { viewModel.onStart(it) }
    } else {
        CountDown(state, initialCount, viewModel::onRestart)
    }
}


@Composable
fun CountDownButton(onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
    Button(
        colors = buttonColors(backgroundColor = Color.Transparent),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
        onClick = onClick,
        content = content
    )
}

@ExperimentalAnimationApi
@Composable
fun CountDownSetup(onDone: (Int) -> Unit) {
    val initialCount = remember { mutableStateOf(3) }
    Center {
        Box(modifier = Modifier.alpha(0f)) {
            CountDownButton({}) {
                Text(text = "Go!", fontSize = 60.sp)
            }
        }
        CountDownButton(onClick = { initialCount.value++ }) {
            Text(text = "+", fontSize = 60.sp)
        }
        CountDownElement(state = CountDownState.Running(initialCount.value))
        CountDownButton(onClick = {
            initialCount.value = kotlin.math.max(0, initialCount.value - 1)
        }) {
            Text(text = "-", fontSize = 60.sp)
        }
        CountDownButton(onClick = { onDone(initialCount.value) }) {
            Text(text = "Go!", fontSize = 60.sp)
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun CountDown(state: CountDownState, initialCount: Int, onRestart: () -> Unit = {}) {
    val backgroundColor = animateBackgroundColor(state)

    Surface(color = backgroundColor.value) {
        Center {
            Box(Modifier.size(0.dp, 200.dp))
            Box(contentAlignment = Alignment.Center) {
                for (index in -1..initialCount + 1) {
                    key(index) {
                        val st = when (index) {
                            initialCount + 1 -> CountDownState.Setup
                            -1 -> CountDownState.Finished
                            else -> CountDownState.Running(index)
                        }

                        CountDownElementAnimation(
                            visible = st == state,
                            infinite = st == CountDownState.Finished
                        ) { color: Color ->
                            CountDownElement(st, color)
                        }
                    }
                }
            }

            val restartAlpha = animateRestartAlpha(state is CountDownState.Finished)

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .graphicsLayer(alpha = restartAlpha),
                contentAlignment = Alignment.Center
            ) {
                CountDownButton(onRestart) {
                    Text(
                        "Restart",
                        fontSize = 42.sp,
                        fontStyle = FontStyle.Italic,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}

@Composable
fun Center(
    content: @Composable ColumnScope.() -> Unit
) = Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    content = content
)

@Composable
fun CountDownElement(state: CountDownState, color: Color = Color.Black) {
    when (state) {
        is CountDownState.Running -> Text(
            state.current.toString(),
            style = typography.h1,
            color = color,
        )
        else -> Text(
            "Finished!",
            style = typography.h2,
            color = color,
        )
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
fun DefaultPreview() {
    CountDown(state = CountDownState.Running(5), initialCount = 10)
}