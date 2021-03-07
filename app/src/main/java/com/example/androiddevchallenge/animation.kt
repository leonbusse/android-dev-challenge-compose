package com.example.androiddevchallenge

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
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
    val targetBackgroundColor = remember { mutableStateOf(Color.White) }
    val skippedFirst = remember { mutableStateOf(false) }
    LaunchedEffect(state) {
        if (skippedFirst.value) {
            var nextColor = targetBackgroundColor.value
            while (nextColor == targetBackgroundColor.value) {
                nextColor = backgroundColors[(Math.random() * backgroundColors.size).toInt()]
            }
            targetBackgroundColor.value = nextColor
        } else {
            skippedFirst.value = true
        }
    }
    val backgroundColorRed by animateFloatAsState(targetValue = targetBackgroundColor.value.red)
    val backgroundColorGreen by animateFloatAsState(targetValue = targetBackgroundColor.value.green)
    val backgroundColorBlue by animateFloatAsState(targetValue = targetBackgroundColor.value.blue)
    return derivedStateOf {
        Color(backgroundColorRed, backgroundColorGreen, backgroundColorBlue)
    }
}

@Composable
fun animateRestartAlpha(visible: Boolean) =
    animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(delayMillis = 1500)
    ).value

@ExperimentalAnimationApi
@Composable
fun CountDownElementAnimation(
    visible: Boolean,
    infinite: Boolean,
    content: @Composable (color: Color) -> Unit
) {

    val animationTweenProgress: Float by animateFloatAsState(
        if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Box {
        CountDownElementForegroundAnimation(visible, animationTweenProgress, content)
        CountDownElementBackgroundAnimation(visible, animationTweenProgress, infinite, content)
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
    infinite: Boolean,
    content: @Composable (color: Color) -> Unit
) {
    val rotationZ = if (infinite) {
        animateFloatAsState(
            targetValue = 0f,
            animationSpec = repeatable(
                100,
                keyframes {
                    delayMillis = 0
                    durationMillis = 4_000
                    0f at 0 with FastOutSlowInEasing
                    360f at 4_000 with FastOutSlowInEasing
                },
                repeatMode = RepeatMode.Restart
            ),
        ).value
    } else {
        if (entering) (animationTweenProgress - 1f) * 90f
        else (1f - animationTweenProgress) * 90f
    }

    val scale = if (infinite) {
        animateFloatAsState(
            targetValue = 0f,
            animationSpec = repeatable(
                100,
                keyframes {
                    delayMillis = 0
                    durationMillis = 4_000
                    2f at 0 with FastOutSlowInEasing
                    4f at 2_000 with FastOutSlowInEasing
                    2f at 4_000 with FastOutSlowInEasing
                },
                repeatMode = RepeatMode.Restart,
            ),
        ).value
    } else {
        animationTweenProgress * 4f
    }

    Box(
        Modifier
            .graphicsLayer(
                alpha = animationTweenProgress,
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotationZ
            )
    ) { content(Color(0f, 0f, 0f, .1f)) }
}