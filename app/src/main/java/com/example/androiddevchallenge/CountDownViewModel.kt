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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    fun onRestart() {
        _countDownState.value = CountDownState.Setup
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
