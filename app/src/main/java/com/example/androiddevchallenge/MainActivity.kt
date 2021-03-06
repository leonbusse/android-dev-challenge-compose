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
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class CountDownState {
    object Setup : CountDownState()
    class Running(val current: Int) : CountDownState()
    object Finished : CountDownState()
}

class CountDownViewModel : ViewModel() {
    private var countDownJob: Job? = null

    private var _countDownState = MutableLiveData<CountDownState>(CountDownState.Setup)
    val countDownState: LiveData<CountDownState> = _countDownState

    private var _initialCount = MutableLiveData(0)
    val initialCount: LiveData<Int> = _initialCount

    fun onStart(initial: Int) {
        countDownJob?.cancel()
        _initialCount.value = initial
        _countDownState.value = CountDownState.Running(initial + 1)
        countDownJob = viewModelScope.launch(Dispatchers.Main) {
            while (_countDownState.value is CountDownState.Running) {
                delay(1000)
                updateCounter()
            }
        }
    }

    private fun updateCounter() {
        _countDownState.value.let { previous ->
            _countDownState.value = when (previous) {
                is CountDownState.Running -> when (previous.current) {
                    0 -> CountDownState.Finished
                    else -> CountDownState.Running(previous.current - 1)
                }
                else -> CountDownState.Finished
            }
        }
    }
}


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

    Surface(color = MaterialTheme.colors.background) {
        Box {
            CountDownElementAnimation(visible = state is CountDownState.Finished) {
                CountDown(CountDownState.Finished)
            }
            for (i in 0..initialCount) {
                CountDownElementAnimation(visible = (state as? CountDownState.Running)?.current == i) {
                    CountDown(CountDownState.Running(i))
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun CountDownElementAnimation(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        initiallyVisible = false,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
    ) {
        Center {
            content()
        }
    }
}

@Composable
fun Center(content: @Composable ColumnScope.() -> Unit) = Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    content = content
)

@Composable
fun CenterHorizontally(content: @Composable ColumnScope.() -> Unit) = Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    content = content
)

@Composable
fun CountDown(state: CountDownState) {
    when (state) {
        is CountDownState.Running -> Text(state.current.toString(), style = typography.h1)
        else -> Text("Finished!", style = typography.h1)
    }
}
