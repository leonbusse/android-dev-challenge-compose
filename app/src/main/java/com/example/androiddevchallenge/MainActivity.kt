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
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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
        viewModel.onStart(10)

    }
}

@ExperimentalAnimationApi
@Composable
fun MyApp(viewModel: CountDownViewModel) {
    val state: CountDownState by viewModel.countDownState
        .observeAsState(CountDownState.Setup)
    val initialCount: Int by viewModel.initialCount
        .observeAsState(0)

    if (state != CountDownState.Setup) {
        CountDown(state, initialCount)
    }
}

@ExperimentalAnimationApi
@Composable
fun CountDown(state: CountDownState, initialCount: Int) {
    val backgroundColor = animateBackgroundColor(state)

    Surface(color = backgroundColor.value) {
        Box(Modifier.fillMaxSize()) {
            for (index in -1..initialCount + 1) {
                key(index) {
                    val st = when (index) {
                        initialCount + 1 -> CountDownState.Setup
                        -1 -> CountDownState.Finished
                        else -> CountDownState.Running(index)
                    }

                    CountDownElementAnimation(visible = st == state) { color: Color ->
                        Center { CountDownElement(st, color) }
                    }
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
            color = color
        )
        else -> Text(
            "Finished!",
            style = typography.h2,
            color = color
        )
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
fun DefaultPreview() {
    CountDown(state = CountDownState.Running(5), initialCount = 10)
}