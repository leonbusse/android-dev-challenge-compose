package com.example.androiddevchallenge

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer


val backgroundColors = listOf(
    Color.Red, Color.Green,
    Color.Cyan, Color.Magenta,
    Color.Yellow, Color.Blue
)

@Composable
fun animateBackgroundColor(state: Any): State<Color> {
    val targetBackgroundColor = remember { mutableStateOf(backgroundColors.first()) }
    LaunchedEffect(state) {
        targetBackgroundColor.value =
            backgroundColors[(Math.random() * backgroundColors.size).toInt()]
    }
    val backgroundColorRed by animateFloatAsState(targetValue = targetBackgroundColor.value.red)
    val backgroundColorGreen by animateFloatAsState(targetValue = targetBackgroundColor.value.green)
    val backgroundColorBlue by animateFloatAsState(targetValue = targetBackgroundColor.value.blue)
    return derivedStateOf {
        Color(backgroundColorRed, backgroundColorGreen, backgroundColorBlue)
    }
}

@ExperimentalAnimationApi
@Composable
fun CountDownElementAnimation(
    visible: Boolean,
    content: @Composable (color: Color) -> Unit
) {

    val animationTweenProgress: Float by animateFloatAsState(
        if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Box {
        CountDownElementForegroundAnimation(visible, animationTweenProgress, content)
        CountDownElementBackgroundAnimation(visible, animationTweenProgress, content)
    }
}

@ExperimentalAnimationApi
@Composable
fun CountDownElementForegroundAnimation(
    entering: Boolean,
    animationTweenProgress: Float,
    content: @Composable (color: Color) -> Unit
) {
    val enterOffsetY: Float by animateFloatAsState(
        if (entering) 0f else -1200f,
        animationSpec = spring(0.4f, 300f)
    )

    val exitOffsetY: Float by animateFloatAsState(
        if (entering) 0f else 2000f,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    val offsetY = if (entering) enterOffsetY else exitOffsetY

    val alpha = if (entering) 1f
    else animationTweenProgress

    val scale = if (entering) animationTweenProgress / 2 + .5f
    else animationTweenProgress

    Box(
        Modifier
            .graphicsLayer(
                alpha = alpha,
                scaleX = scale,
                scaleY = scale,
                translationY = offsetY
            )
    ) { content(Color.Black) }
}

@ExperimentalAnimationApi
@Composable
fun CountDownElementBackgroundAnimation(
    entering: Boolean,
    animationTweenProgress: Float,
    content: @Composable (color: Color) -> Unit
) {
    Box(
        Modifier
            .graphicsLayer(
                alpha = animationTweenProgress,
                scaleX = animationTweenProgress * 4f,
                scaleY = animationTweenProgress * 4f,
                rotationZ = if (entering) (animationTweenProgress - 1f) * 90f
                else (1f - animationTweenProgress) * 90f
            )
    ) { content(Color(0f, 0f, 0f, .1f)) }
}