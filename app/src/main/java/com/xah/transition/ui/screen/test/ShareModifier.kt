package com.xah.transition.ui.screen.test

import android.graphics.Canvas
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.scaleToBounds
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.xah.common.util.LogUtil
import com.xah.navigation.state.LocalAnimatedContentScope
import com.xah.navigation.state.LocalSharedTransitionScope

private val spring = spring(
//    dampingRatio = 0.825f,
    stiffness = 100f,
    visibilityThreshold = Rect.VisibilityThreshold
)

/**
 * 实时测量组件宽高
 * @param onSize CallBack -> (width, height)
 */
@Composable
fun Modifier.measureDpSize(
    onSize: (Dp, Dp) -> Unit
): Modifier {
    val density = LocalDensity.current
    val currentCallback = rememberUpdatedState(onSize)

    return this.then(
        Modifier.onSizeChanged { size ->
            with(density) {
                currentCallback.value(size.width.toDp(), size.height.toDp())
            }
        }
    )
}


// 容器共享元素
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.containerShare(
    route : String,
    color : Color?,
    shape: Shape?,
) : Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedContentScope.current
    with(sharedTransitionScope) {

        val state = rememberSharedContentState(key = "container_$route")

        val boundsTransform = BoundsTransform { _,_ ->
            spring
        }


        var rect by remember { mutableStateOf<Rect?>(null) }
        return this@containerShare
        .sharedBounds(
            enter = fadeIn(
                animationSpec = spring(
                    stiffness = 50f,
                )
            ),
            exit = fadeOut(
                animationSpec = spring(
                    stiffness = 50f,
                )
            ),
            resizeMode = scaleToBounds(ContentScale.FillWidth, Alignment.TopCenter),
            boundsTransform = boundsTransform,
            sharedContentState = state,
            animatedVisibilityScope = animatedContentScope,
        )

            .onGloballyPositioned { coordinates ->
                rect = coordinates.boundsInRoot()  // 获取更新后的布局信息
                LogUtil.info("color==null:${color == null } rect:${rect?.size}")
            }

            .drawBehind {
                if(rect == null) {
                    return@drawBehind
                }
                // 绘制白色背景遮罩，大小和共享元素同步
                drawRect(
                    color = color ?: Color.Yellow,  // 白色背景遮罩
                    topLeft = Offset(rect!!.left, rect!!.top),
                    size = Size(rect!!.width, rect!!.height)
                )
            }
    }
}




@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.singleElementShare(
    title : String,
    route : String,
) : Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedContentScope.current
    return with(sharedTransitionScope) {
        this@singleElementShare.sharedElement(
            boundsTransform = BoundsTransform { _,_ ->
                spring
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