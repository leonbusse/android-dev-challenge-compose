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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class CountDownState {
    object Setup : CountDownState()
    class Running(val current: Int) : CountDownState()
    object Finished : CountDownState()
}

class CountDownViewModel : ViewModel() {
    private var _countDownState = MutableLiveData<CountDownState>(CountDownState.Setup)
    val countDownState: LiveData<CountDownState> = _countDownState

    fun onStart(targetCount: Int) {
        _countDownState.value = CountDownState.Running(targetCount)
        viewModelScope.launch(Dispatchers.Main) {
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
                is CountDownState.Finished,
                is CountDownState.Setup, null -> CountDownState.Finished
            }
        }
    }
}


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

@Composable
fun MyApp(viewModel: CountDownViewModel) {
    val state: CountDownState by viewModel.countDownState
        .observeAsState(CountDownState.Setup)

    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.let {
                when (it) {
                    is CountDownState.Running -> CountDown(it.current)
                    else -> Text("Finished!", style = typography.body1)
                }
            }

        }
    }
}

@Composable
fun CountDown(count: Int) {
    Text(count.toString(), style = typography.h1)
}
