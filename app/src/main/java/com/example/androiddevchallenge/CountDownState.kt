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