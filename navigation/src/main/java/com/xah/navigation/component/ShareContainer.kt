package com.xah.navigation.component

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import com.xah.navigation.state.LocalAnimatedContentScope
import com.xah.navigation.state.LocalSharedTransitionScope

private fun <T> transition() :  SpringSpec<T> = spring(
    dampingRatio = 0.875f,
    stiffness = 200f,
)

// 容器共享元素
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.containerShare(route : String, ) : Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedContentScope.current
    with(sharedTransitionScope) {
        val state = rememberSharedContentState(key = "container_$route")

        val boundsTransform = BoundsTransform { _,_ ->
            transition()
        }

        return this@containerShare
            .sharedElement(
                renderInOverlayDuringTransition = true,
                zIndexInOverlay = 0f,
                boundsTransform = boundsTransform,
                sharedContentState = state,
                animatedVisibilityScope = animatedContentScope,
            )
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Modifier.singleElementShare(
    title : String,
    route : String,
) : Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedContentScope.current
    return with(sharedTransitionScope) {
        this@singleElementShare.sharedElement(
            boundsTransform = BoundsTransform { _,_ ->
                transition()
            },
            sharedContentState = rememberSharedContentState(key = "${title}_$route"),
            animatedVisibilityScope = animatedContentScope,
        )
    }
}

// 标题共享元素
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.iconElementShare(route : String) : Modifier = singleElementShare("icon",route)
// 图标共享元素
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.titleElementShare(route : String) : Modifier = singleElementShare("title",route)