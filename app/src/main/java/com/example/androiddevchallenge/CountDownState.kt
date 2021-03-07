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

sealed class CountDownState {
    abstract val key: String

    object Setup : CountDownState() {
        override val key = "Setup"
    }

    data class Running(val current: Int) : CountDownState() {
        override val key = "Running{$current}"
    }

    object Finished : CountDownState() {
        override val key = "Finished"
    }
}

fun CountDownState.next(initialCount: Int): CountDownState? =
    when {
        this is CountDownState.Setup -> CountDownState.Running(initialCount)
        this is CountDownState.Running
            && this.current != 0 -> CountDownState.Running(this.current - 1)
        this is CountDownState.Running
            && this.current == 0 -> CountDownState.Finished
        else -> null
    }

fun CountDownState.previous(initialCount: Int): CountDownState? =
    when {
        this is CountDownState.Finished -> CountDownState.Running(0)
        this is CountDownState.Running
            && this.current != initialCount -> CountDownState.Running(this.current + 1)
        this is CountDownState.Running
            && this.current == initialCount -> CountDownState.Setup
        else -> null
    }
