# 首次周会分享

尝试复刻了一下 Android/iOS 桌面及一些应用的页面转场动效，并封装成库。这里只是提供其中一种实现思路。

## 一、背景

iOS 26 发布，不仅带来了 Liquid Glass，我还注意到很多系统应用都增加了展开/收起的容器共享转场动效的大肆应用。这种效果的特点是：点击一个按钮、卡片、内容，不是简单地跳转到新页面，而是从原始位置流畅地形变，展开成全屏页面，再伴随背景一些特效，收起时则为反向动作，非常有层级感和设计感。这种转场让用户对页面的层级关系有清晰的认知——知道"是从哪里来的"。

Android阵营中，各定制系统也纷纷跟进：OriginOS（vivo）、ColorOS（OPPO）、鸿蒙（华为）均已适配或正在适配这种类似效果。但 Android 官方并未提供开箱即用的方案，有也是效果不太好。所以尝试能否借助已有API封装出来。

## 二、同类产品

1. iOS 26 系统应用的效果
   <div data-block-type="resource" data-resource-type="video" data-blob-store-key="docs_enclosure_0be771ef-a167-4b0d-acd5-a31bca9208dc_drag-upload-1782268203264-0" data-file-name="录屏2026-06-24 10.29.22.mov" data-mime-type="video/quicktime" data-file-size="8737148"></div>
2. OrginOS对小红书的适配（个人猜测是在系统层面定制了 Activity 之间的转场动画以实现的）
   <div data-block-type="resource" data-resource-type="video" data-blob-store-key="docs_enclosure_61b4682f-6903-4351-963b-a06d32a2d031_drag-upload-1782315619928-0" data-file-name="28d5a787f224719ebbcc88aac5e6be28.mp4" data-mime-type="video/mp4" data-file-size="440021"></div>
3. 快手（应用内定制的）
   <div data-block-type="resource" data-resource-type="video" data-blob-store-key="docs_enclosure_bc121b2e-8794-4468-80b2-9ace9c33f88f_drag-upload-1782267937578-0" data-file-name="Screenrecorder-2026-06-24-10-24-09-433.mp4" data-mime-type="video/mp4" data-file-size="4555056"></div>
4. 本文实现的Demo（慢速处理）
   <div data-block-type="resource" data-resource-type="video" data-blob-store-key="docs_enclosure_97f132d6-4335-4a86-96dc-f0c8cd184c0e_drag-upload-1782234309992-0" data-file-name="bafecbc50a6a87248179b030028cb007.mp4" data-mime-type="video/mp4" data-file-size="3424999"></div>
## 三、设计思路

通过观察视频，可以讲任务拆解成两大部分：容器共享、背景动效，相对应两个模块：容器共享模块、页面路由模块。

### （一）容器共享

1. <h4>粗略地确定状态&amp;动作</h4>
- 状态

首先，容易考虑到基本状态为，起始态和目标态。

这里为容器成为CONTAINER，其展开的页面称为CONTENT。

- 动作

容易考虑到基础动作为展开、收起，即PUSH和POP。

当展开（PUSH）时：起始态为CONTAINER，目标态为CONTENT，中间态暂时粗略地当作一个0f->1f的插值过程。同理当收起（POP）时，起始态为CONTENT，目标态为CONTAINER。

【介绍】线性插值：

有lerp函数，计算从start到end之间progress(0f\~1f)的值，即(start+end)\*progress。

例如 fun lerp(start : Dp,end : Dp,progress : Float) : Dp

使用线性插值可以实现对两个Rect的插值（位置、尺寸连贯变化）、圆角、透明度等参数，这些统一随动画进度progress从0%到100%变化。

1. <h4>属性</h4>

接下来需要考虑收集哪些属性。

1. CONTENT和CONTAINER的画面

方案1：捕获快照

否定：速度慢，画面是冻结的，画面应该是实时的。

方案2：定义类的时候传入各自的Compose函数，在新的Layer层上绘制。

否定：二次绘制，不优雅。

方案3：通过GraphicsLayer捕获画面并绘制到新的Layer层上，同时将原画面隐藏掉

【介绍】GraphicsLayer

GraphicsLayer是Compose绘图体系中的一个离屏绘制层。可以把它理解为一个脱离屏幕的独立画布。它不在屏幕上直接显示，但可以接收绘制指令、缓存绘制结果，并支持在任意位置重新输出。

它本质上是把绘制指令从"画到屏幕上"重定向为"画到一个离屏buffer中"。这个buffer里存的不是像素截图，而是完整的绘制指令记录，因此后续可以无损地施加缩放、平移、旋转、裁剪、透明度等变换，再输出到屏幕。

同时，GraphicsLayer只涉及GPU，效率高。

需要用到的API

```kotlin
/**
* 创建一个新的[GraphicsLayer]实例，该实例将在可组合对象被销毁时自动释放。
*/
/**
 * Create a new [GraphicsLayer] instance that will automatically be released when the Composable is
 * disposed.
 *
 * @return a GraphicsLayer instance
 */
@Composable
@ComposableOpenTarget(-1)
fun rememberGraphicsLayer(): GraphicsLayer {
    val graphicsContext = LocalGraphicsContext.current
    return remember { GraphicsContextObserver(graphicsContext) }.graphicsLayer
}

/**
 * Draw the provided [GraphicsLayer] into the current [DrawScope]. The [GraphicsLayer] provided must
 * have [GraphicsLayer.record] invoked on it otherwise no visual output will be seen in the rendered
 * result.
 */
/** 
* 将提供的 [GraphicsLayer]] 插入到当前的 [DrawScope] 中。
* 所提供的 [GraphicsLayer]] 必须调用过 [GraphicsLayer.record]，否则渲染结果中将无法看到任何视觉输出。
*/
fun DrawScope.drawLayer(graphicsLayer: GraphicsLayer)

/**
 * Creates a [DrawModifier] that allows the developer to draw before or after the layout's contents.
 * It also allows the modifier to adjust the layout's canvas.
 */
 /** 
 * 创建一个[DrawModifier]，允许开发者在布局内容之前或之后进行绘制。  
 * 同时，该修饰符还可以调整布局的画布。
 */
fun Modifier.drawWithContent(onDraw: ContentDrawScope.() -> Unit): Modifier

/**
 * Receiver scope for drawing content into a layout, where the content can be drawn between other
 * canvas operations. If [drawContent] is not called, the contents of the layout will not be drawn.
 */
/** 
* 用于将内容绘制到布局中的接收器，该内容可在其他画布操作之间进行绘制。
* 如果未调用[drawContent]，则不会绘制布局的内容。
*/
@JvmDefaultWithCompatibility
interface ContentDrawScope : DrawScope {
    /** Causes child drawing operations to run during the `onPaint` lambda. */
    /** 在 `onPaint` lambda 中运行子线程绘图操作。*/
    fun drawContent()
}

/**
* Record the corresponding drawing commands for this [GraphicsLayer] instance using the
* [Density], [LayoutDirection] and [IntSize] from the provided [DrawScope] as defaults. This
* will retarget the underlying canvas of the provided DrawScope to draw within the layer itself
* and reset it to the original canvas on the conclusion of this method call.
*/
/**
* 使用提供的 DrawScope 中的 [Density]、[LayoutDirection] 和 [IntSize] 作为默认值，为该 [GraphicsLayer] 实例记录相应的绘图命令。
* 这将重定向所提供的 DrawScope 的底层画布，使其在图层内部进行绘制，并在本方法调用结束时将其重置回原始画布。
*/
fun GraphicsLayer.record(
  size: IntSize = this@DrawScope.size.toIntSize(),
  block: DrawScope.() -> Unit,
) 
```

rememberGraphicsLayer()：创建一个GraphicsLayer实例，此时内部为空，等待被录制内容

GraphicsLayer.record { ... }：将传入的绘制指令录制到这个layer中。record会临时接管DrawScope的底层Canvas，使得所有绘制操作输出到layer的离屏buffer而非屏幕。record结束后Canvas自动恢复。record可以每帧调用，实现持续更新录制内容

drawLayer(layer)：在任意DrawScope中，将之前录制的layer内容重新绘制到当前Canvas上。绘制时可以通过layer的属性（scaleX/scaleY/translationX/translationY/alpha/rotationX/rotationY/renderEffect等）施加变换，也可以在drawLayer外用withTransform包裹来做Canvas级变换

通俗类比一下：GraphicsLayer像一个相机——record实时录制，drawLayer将record实时画面画出来（当record录制不到画面时，drawlayer同时也画面为空），且同时可以对其应用效果（缩放、旋转、着色器等）。

最终选择方案3

2.CONTENT和CONTAINER的尺寸以及位置

通过Rect可以记录尺寸和位置，对两个Rect进行插值，形成连贯的路径动画。

【介绍】Rect：

Rect即Rectangle，矩形，此类构造函数可传入Rect(int start,int top,int width,int height)

,或Rect(int start,int end,int top,int bottom),通过View坐标系定位一个矩形，可以得到组件的位置、尺寸。

3.CONTENT和CONTAINER的形状（圆角）

对记录的Rect进行圆角裁切，从圆形到矩形，形成想要的形状。

> 这里也许可以扩展一下，不局限形状只能是矩形，如果是不规则图形是否也可以有办法变换呢（例如从五角星变为矩形）？

4.其余一些参数，略

```kotlin
class SharedContainerState(
   // 唯一标识
    val key : String
) {
    // 尺寸与位置
    var containerRect: Rect? = null
    var contentRect: Rect? = null

    // 录制的内容
    var containerLayer: GraphicsLayer? = null
    var contentLayer: GraphicsLayer? = null

    // 形状/圆角
    var containerCorner: CornerBasedShape = NoneRoundShape
    var contentCorner: CornerBasedShape = ScreenRoundShape
    
    val animation = Animatable(0f)
    // 当前所处状态
    var currentState: StatePause = StatePause.CONTAINER

    // 被标注为不活跃的将会在合适的时机解除注册 计数法
    var isActive : Int = 0
    fun isActive() = isActive <= 0
  
    ...
}
```

这些参数后面在浮层中进行插值、裁切、应用，接下来先设计这些条件如何收集。

1. <h4>条件收集</h4>

利用Compose中Modifier修饰符强大的功能，可以封装两个扩展函数，一个挂在CONTAINER上，一个挂在CONTENT上，双方传入相同的key，即可完成注册、绑定及收集需要的条件，及对组件生命周期的监控。

```kotlin
fun Modifier.sharedContainer(
    key : String?,
    shape : CornerBasedShape,
): Modifier = composed {
    if(key == null) {
        return@composed this
    }
    val registry = LocalSharedRegistrySafely.current ?: return@composed this
    if(!registry.enabled) {
        return@composed this
    }
    val state = remember { registry.register(key) }
    val contentStrategy = state.contentStrategy

    val graphicsLayer = rememberGraphicsLayer()
    
    LaunchedEffect(shape) {
        state.containerCorner = shape
    }

    DisposableEffect (Unit) {
        LogUtil.debug("SharedContainer ${state.key} onCreate")
        state.isActive++
        state.containerFilledStrategy = containerFilledStrategy
        state.containerLayerForPixel = graphicsLayerForPixel
        state.containerLayer = graphicsLayer
        onDispose {
            LogUtil.debug("SharedContainer ${state.key} onDestroy")
            state.isActive--
        }
    }

    return@composed this
        .let {
            when(state.currentState) {
                StatePause.CONTAINER -> {
                    it.drawWithContent {
                        drawContent()
                    }
                }
                StatePause.CONTENT -> {
                    if(contentStrategy is ContentStrategy.Layer) {
                        it.drawWithContent {}
                    } else {
                        it
                    }
                }
                StatePause.MEASURING_CONTAINER -> {
                     it.graphicsLayer(alpha = 0f)
                       .drawWithContent {
                            drawContent()
                        }
                }
                StatePause.TRANSITING -> {
                    it.drawWithContent {
                            graphicsLayer?.record {
                                this@drawWithContent.drawContent()
                            }
                        }
                }
                else -> {
                    it
                }
            }
        }
        // 记录两个组件的位置、大小
        .onGloballyPositioned { coordinates ->
            val position = coordinates.positionInRoot()
            val size = coordinates.size
            val layoutRect = Rect(
                left = position.x,
                top = position.y,
                right = position.x + size.width,
                bottom = position.y + size.height
            )
            state.containerRect = layoutRect
        }
}

fun Modifier.sharedContent(
    key : String?,
    shape: CornerBasedShape,
): Modifier = composed {
    if(key == null) {
        return@composed this
    }
    val registry = LocalSharedRegistrySafely.current ?: return@composed this
    if(!registry.enabled) {
        return@composed this
    }

    val state = remember { registry.get(key,contentStrategy) }
    if(state == null) {
        return@composed this
    }
    val graphicsLayer = rememberGraphicsLayer()

    LaunchedEffect(shape) {
        state.contentCorner = shape
    }

    DisposableEffect (Unit) {
        state.contentLayer = graphicsLayer
        onDispose {}
    }

    this
        .let {
            when(state.currentState) {
                StatePause.CONTENT -> {
                    it
                }
                StatePause.MEASURING_CONTENT -> {
                    it
                        .graphicsLayer(alpha = 0f)
                        .drawWithContent {
                            drawContent()
                        }
                }
                StatePause.TRANSITING -> {
                    it.drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                    }
                }
                else -> {
                    it
                }
            }
        }
        // 记录组件的位置、大小
        .onGloballyPositioned { coordinates ->
            val position = coordinates.positionInRoot()
            val size = coordinates.size

            state.contentRect = Rect(
                left = position.x,
                top = position.y,
                right = position.x + size.width,
                bottom = position.y + size.height
            )
        }
}
```

解注册的条件：

1. POP时等帧超时时，解除注册
2. CONTAINER出现时，state.isActive++，销毁时state.isActive--，如果state.isActive>=0则代表活跃，不需要回收，如果<0代表需要回收，SharedRegistry会调用unregister函数。
   ```kotlin
   DisposableEffect (Unit) {
       LogUtil.debug("SharedContainer ${state.key} onCreate")
       state.isActive++
       state.containerFilledStrategy = containerFilledStrategy
       state.containerLayerForPixel = graphicsLayerForPixel
       state.containerLayer = graphicsLayer
       onDispose {
           LogUtil.debug("SharedContainer ${state.key} onDestroy")
           state.isActive--
   		}
   }
   ```
此方法保证了复杂场景下状态正确性。

接下来需要设计一个”管理中枢“。

1. <h4>状态机&amp;管理</h4>
1. SharedContainerState的存取

建立一个类SharedRegistry，用于管理SharedContainerState。

由于需要频繁存取SharedContainerState，将其按key设计为哈希表。

```kotlin
class SharedRegistry( 
    private val scope: CoroutineScope,
    // 哈希表，存储SharedContainerState
    private val states : SnapshotStateMap<String, SharedContainerState>
) {
    /*
    * 注册SharedContainerState，当CONTAINER出现的时候注册
    */
    fun register(
        key: String,
    ): SharedContainerState = states.getOrPut(key) {
        LogUtil.debug("register key: $key")
        SharedContainerState(key)
    }

    /*
    * 解注册SharedContainerState，当CONTAINER不再活跃的时候会被调用
    * 如何判断不再活跃：见下面分析
    */
    fun unregister(key: String) {
        LogUtil.debug("unregister key: $key")
        states.remove(key)
    }
}
```

1. 状态机

简易状态流转：最初粗略地将状态定为这三种

![图片](/docs/DocRes/switch_simple.webp)

但实际上的状态流转：

对于 Push 动作

![图片](/docs/DocRes/switch_push.webp)

对于 Pop 动作

![图片](/docs/DocRes/state_pop.webp)

预测式返回手势

![图片](/docs/DocRes/state_back.webp)

Push：

Step1：检查CONTAINER的Rect是否为空，是则条件缺失，跳过

Step2：检查是否处于活跃状态，不是则解注册

Step3：状态复位，将state.animation（0f到1f）瞬间复位到初始值

Step4：进入WAITING\_FRAME状态，开始while循环计数10帧，超时仍未出现目标CONTENT则跳过。未超时则继续。（此时上层导航（页面路由）将切换为双页面共存的状态，为容器共享模块提供环境）

Step5：进入MEASURING状态，此时CONTENT已经渲染，将透明度置为0，在不显示的状态下测量其尺寸与位置（Rect）

Step6：进入TRANSITING状态，将真实的CONTAINER与CONTENT隐藏起来，但实时捕获他们的画面。然后开启浮层（盖在最上层），将插值的Rect绘制出来，并将画面放在Rect中。这样，就实现了伪装的组件飞出效果（组件受层级约束，是不能随意脱离的）（实际上只是将原组件隐藏起来并捕获画面，然后在新的一层绘制出来）

Pop：

类似，略。

代码：

```kotlin
class SharedRegistry( 
    private val scope: CoroutineScope,
    // 哈希表，存储SharedContainerState
    private val states : SnapshotStateMap<String, SharedContainerState>
) {
    /*
    * Push函数，驱动状态从CONTAINER到CONTENT
    */
    private suspend fun pushInternal(
        state: SharedContainerState,
        onSwap: () -> Unit
    ) {
        if(state.containerRect == null) {
            onSwap()
            state.currentState = StatePause.CONTENT
            return
        }
        // container destroy的时候不启用动画
        if(！state.isActive()) {
            onSwap()
            unregister(state)
            LogUtil.debug("push without shared ${state.key}")
            return
        }
        if(!waitContentFrame(state,onSwap)) {
            return
        }
        snap(state,true)
        // 开始标识位
        state.currentState = StatePause.TRANSITING
        state.animation.animateTo(1f,getPushAnimation())
        state.containerRect = null
        // 结束标志位
        state.currentState = StatePause.CONTENT
    }

    /*
    * Pop函数，驱动状态从CONTENT到CONTAINER
    */
    private suspend fun popInternal(
        state: SharedContainerState,
        onSwap: () -> Unit
    ) {
        if(!waitContainerFrame(state,onSwap)) {
            return
        }
        // 重置位置
        snap(state,false)
        // 开始标识位
        state.currentState = StatePause.TRANSITING
        state.animation.animateTo(0f,getPopAnimation())
        state.contentRect = null
        // 结束标志位
        state.currentState = StatePause.CONTAINER
    }

    /*
    * 状态复位
    */
    private suspend fun snap(
        state: SharedContainerState,
        isPush : Boolean
    ) {
        if(state.currentState != StatePause.TRANSITING) {
            // 状态复位
            state.animation.snapTo(
                if(isPush) {
                    0f
                } else {
                    1f
                }
            )
        }
    }

    private suspend fun waitContainerFrame(
        state: SharedContainerState,
        onSwap: () -> Unit,
    ): Boolean = waitFrame(state,true,onSwap)

    private suspend fun waitContentFrame(
        state: SharedContainerState,
        onSwap: () -> Unit,
    ): Boolean = waitFrame(state,false,onSwap)

    /**
     * 返回时，有些CONTAINER是否出现可能取决于异步数据加载；
     * 如果只等1帧，有些较慢的逻辑，CONTAINER因出现的晚，而错过了动画；
     * 如果一直轮询保持等待，如果此CONTAINER永远不出现，那么动画永远不会开始，则导致阻塞。
     * 所以等待一个合适的值（例如10帧），如果超时了则不使用容器共享动画，交给上级完成，即onSwap
     */
    private suspend fun waitFrame(
        state: SharedContainerState,
        isContainer : Boolean,
        onSwap: () -> Unit,
    ): Boolean {
        require(waitFrameMaxValue >= 1) {
            error("waitFrameMaxValue must >= 1")
        }

        var frameCount = 0
        // 状态复位
        if(state.currentState != StatePause.TRANSITING) {
            state.currentState = if(isContainer) {
                StatePause.CONTENT
            } else {
                StatePause.CONTAINER
            }
        }
        // 一定要确保页面切换(onSwap)之后马上等帧(awaitFrame)
        onSwap()
        when(state.currentState) {
            StatePause.CONTENT -> {
                state.currentState = StatePause.MEASURING_CONTAINER
            }
            StatePause.CONTAINER -> {
                state.currentState = StatePause.MEASURING_CONTENT
            }
            else -> Unit
        }
        while (true) {
            awaitFrame()
            frameCount++
            val rect = if(isContainer) {
                state.containerRect
            } else {
                state.contentRect
            }
            if (rect != null) {
                LogUtil.info("Pop : waiting for $frameCount frame")
                return true
            }
            if (frameCount >= waitFrameMaxValue) {
                LogUtil.warn("Pop : rendering timeout after $frameCount frame")
                // 复位 停止等帧
                state.currentState = if(isContainer) {
                    StatePause.CONTAINER
                } else {
                    StatePause.CONTENT
                }
                return false
            }
        }
    }
}
```

对于模块的”管理中枢“设计好了，接下来需要设计UI层，怎么把这些绘制出来。

#### UI绘制

##### 浮层

接下来需要思考，这个动画本质上是从一个组件“飞”到另一个组件，但是组件是无法随意跨层级“飞"出去的，以下是我的设计：

定义一个 SharedContainerRoot函数

```kotlin
@Composable
fun SharedContainerRoot(
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val registry = rememberSharedRegistry()

    CompositionLocalProvider(
        LocalSharedRegistrySafely provides registry,
        LocalSharedRegistry provides registry
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 正常界面
            content()
            // 动画浮层，当动画进行时覆盖在正常界面content()的上面
            SharedContainerOverlay()
        }
    }
}


@Composable
fun SharedContainerOverlay() {
    val registry = LocalSharedRegistry.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // 遍历哈希表过滤出SharedContainerState的状态正在动画的
    registry.runningStates.forEach { state ->
        key(state) {
            val container = state.containerRect!!
            val content = state.contentRect!!

            // 进度
            val progress = state.animation.value

            // 路径曲线
            val parent = registry.FullScreenRectInterpolator(progress, container, content)

            val progressOfAlpha = (progress * registry.speedUpRadioAlpha * if(useContainer) 1f else 2f).coerceIn(0f,1f)
            val contentAlpha = lerp(0f,1f,progressOfAlpha)

            val progressOfCorner = (progress * registry.speedUpRadioCorner * if(useContainer) 1f else 2f).coerceIn(0f,1f)
            val corner = lerp(state.containerCorner,state.contentCorner,progressOfCorner)

            // 倾斜计算
            val (roX, roY) = Pair(...)

            // 填充策略
            val containerFilledStrategy = state.containerFilledStrategy.getFinalStrategy(registry)
            val extensionDouble = registry.extensionDouble || containerFilledStrategy is ContainerFilledStrategy.Element

            val heightW = container.height / content.height
            val widthW = container.width / content.width
            val isHorizontal = if(heightW > widthW) {
                // 左右填充
                true
            } else if(heightW < widthW) {
                // 上下填充
                false
            } else {
                isLandscape
            }

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = parent.left
                        translationY = parent.top

                        rotationX = roX
                        rotationY = roY
                    }
                    .size(
                        with(density) { parent.width.toDp() },
                        with(density) { parent.height.toDp() }
                    )
                    .clip(corner)
                    .let {
                        if(containerFilledStrategy is ContainerFilledStrategy.Color && useContainer) {
                            it.background(containerFilledStrategy.color)
                        } else {
                            it
                        }
                    }
            ) {
              ...//对内容进行处理，略
            }
        }
    }
}

```

当动画进行时，动画浮层出现，层级在正常界面之上，此时隐藏真实的CONTAINER与CONTENT（通过上面定义的Modifier扩展函数），然后使用捕获的画面、尺寸、位置、形状等参数，根据插值结果在浮层中绘制新的组件，待动画结束时浮层消失，真实的CONTAINER或CONTENT取消隐藏，在这个瞬间完成切换的过程中不会有任何卡顿（看不出来这其实是两个层级之间互相切换完成的结果）。

接下来就是对这个新的Rect进行”装饰“：

1. <h5>填充处理</h5>

这里讲一下填充的处理，通过观察手机系统桌面动画，可以看出目前分为三种方式：

（这里只讨论一种方向）

1. 直接Y轴拉伸CONTAINER的画面，用于填满Layer（早期华为EMUI使用方式，已不流行）
2. 将CONTAINER的画面放大填至满Layer的宽度，并放在Layer的顶部，下面空缺部分，采用填充

（1）填充1: 取底部1像素点颜色（Flyme9使用，已不流行）

（2）填充2:取底部1像素直线做拉伸（iOS方案，目前主流方案）

1. 将CONTAINER的画面放大填至满Layer的高度，宽度溢出的画面裁切掉

OriginOS1使用，目前仍流行，适合作为取底部1像素的替代方案。成本低

![图片](/docs/DocRes/fill_1.webp)![图片](/docs/DocRes/fill_2.webp)![图片](/docs/DocRes/fill_originos.webp)

1. Shader 着色器

Shader是一段运行在GPU上的小程序，它的输入是像素坐标，输出是该像素的颜色。可以把它理解为一个函数：给你一个坐标(x, y)，你告诉我这个点应该画什么颜色。因为GPU天然并行，屏幕上数百万个像素可以同时各自跑一遍这个函数，所以Shader处理图形的效率极高。

GLSL（OpenGL Shading Language）是OpenGL的着色器语言，广泛用于游戏、3D渲染、WebGL等场景。Android的OpenGL也使用GLSL编写顶点着色器和片元着色器。但GLSL运行在OpenGL上下文中，需要配置VBO、FBO、纹理等繁琐的GPU管线，对2D图形操作来说过于重量级。

AGSL（Android Graphics Shader Language）是Android 13引入的着色器语言，语法与GLSL几乎一致，但运行在Android的Canvas/GraphicsLayer绘制管线中，而非OpenGL上下文。

AGSL不需要搭建OpenGL管线。你只需要写一段Shader代码字符串，传给RuntimeShader编译，然后用RenderEffect挂到任意View/GraphicsLayer上即可生效。写法简洁，就像写一段纯函数——输入坐标，输出颜色。

关键api

1. RuntimeShader：Android 13+提供的API，接收一段AGSL代码字符串，在运行时编译为GPU可执行的着色器程序。编译后可以通过\`setFloatUniform()\`传入参数（如尺寸、缩放比等），着色器内部通过\`content.eval(coord)\`采样原始画面。
2. RenderEffect：将RuntimeShader包装为RenderEffect对象，然后通过\`graphicsLayer.renderEffect = ...\`应用到任意GraphicsLayer上。应用后，该GraphicsLayer的每个像素都会经过其Shader代码计算后输出。（Shader代码写错了直接Crash）

三者的关系：AGSL是语言，RuntimeShader是编译器，RenderEffect是挂载器。  

借助如下代码可复刻iOS桌面的效果：

```glsl
uniform shader content;
uniform float2 size;          

half4 main(float2 fragCoord) {
  // 注释
	float y = fragCoord.y < size.y * 0.5 
    ? 1.0 
    : size.y - 1.0;

	float2 coord = float2(fragCoord.x, y);
	return content.eval(coord);
}
```

<div data-block-type="resource" data-resource-type="video" data-blob-store-key="docs_enclosure_06e5145f-0e2b-49de-b74b-7b99ee532013_drag-upload-1782235567183-0" data-file-name="d326268fb564ed5d07687364b468c007.mp4" data-mime-type="video/mp4" data-file-size="349981"></div>

完整实现：

```kotlin
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import org.intellij.lang.annotations.Language

enum class ExtensionDirection {
    BOTTOM,
    END,
    START,
    TOP,
    HORIZONTAL,
    VERTICAL
}

fun Modifier.pixelExtension(
    parentGraphicsLayer: GraphicsLayer,
    parentRect: Rect?,
    direction : ExtensionDirection
): Modifier {
    if(parentRect == null) {
        return this
    }
    return composed {
        if (Build.VERSION.SDK_INT < 33) {
            this
        } else {
            val customRenderEffect = remember(parentRect) {
                val runtimeShader = RuntimeShader(
                    when(direction) {
                        ExtensionDirection.END -> END_SHADER_CODE
                        ExtensionDirection.TOP -> TOP_SHADER_CODE
                        ExtensionDirection.START -> START_SHADER_CODE
                        ExtensionDirection.BOTTOM -> BOTTOM_SHADER_CODE
                        ExtensionDirection.VERTICAL -> VERTICAL_SHADER_CODE
                        ExtensionDirection.HORIZONTAL -> HORIZONTAL_SHADER_CODE
                    }.trimIndent()
                )
                runtimeShader.setFloatUniform("size", parentRect.width, parentRect.height)

                RenderEffect.createRuntimeShaderEffect(runtimeShader, "content").asComposeRenderEffect()
            }

            this.drawWithCache {
                onDrawWithContent {
                    parentGraphicsLayer.renderEffect = customRenderEffect
                    drawLayer(parentGraphicsLayer)
                }
            }
        }
    }
}

/**
 * @param isLandscape 是否是横屏，为true则取右侧1像素，否则取底部1像素
 * @param isDouble 是否取双边延展
 */
fun Modifier.pixelExtension(
    parentGraphicsLayer: GraphicsLayer,
    parentRect: Rect?,
    isLandscape : Boolean,
    isDouble : Boolean = false,
): Modifier = pixelExtension(
    parentGraphicsLayer,
    parentRect,
    if(isLandscape) {
        if(!isDouble) {
            ExtensionDirection.END
        } else {
            ExtensionDirection.HORIZONTAL
        }
    } else {
        if(!isDouble) {
            ExtensionDirection.BOTTOM
        } else {
            ExtensionDirection.VERTICAL
        }
    }
)

@Language("AGSL")
private const val BOTTOM_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 采样底部1像素行
        float2 bottomCoord = float2(fragCoord.x, size.y - 1.0);
        return content.eval(bottomCoord);
    }
"""

@Language("AGSL")
private const val END_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 采样右侧1像素列
        float2 endCoord = float2(size.x - 1.0, fragCoord.y);
        return content.eval(endCoord);
    }
"""

@Language("AGSL")
private const val START_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 采样左侧1像素行
        float2 leftCoord = float2(1.0, fragCoord.y);
        return content.eval(leftCoord);
    }
"""

@Language("AGSL")
private const val TOP_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 采样顶部1像素行
        float2 topCoord = float2(fragCoord.x, 1.0);
        return content.eval(topCoord);
    }
"""

@Language("AGSL")
private const val HORIZONTAL_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {

        float x = fragCoord.x < size.x * 0.5
            ? 1.0
            : size.x - 1.0;

        float2 coord = float2(x, fragCoord.y);
        return content.eval(coord);
    }
"""

@Language("AGSL")
private const val VERTICAL_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {

        float y = fragCoord.y < size.y * 0.5
            ? 1.0
            : size.y - 1.0;

        float2 coord = float2(fragCoord.x, y);
        return content.eval(coord);
    }
"""
```

除了1像素提取，对于剩余空缺位置的填充方式，还有别的可选方案，从左至右分别为：取边缘1像素拉伸、指定颜色、裁切填满

![图片](/docs/DocRes/fill_gap.webp)

裁切方案参考了 OriginOS（vivo）。

1. <h5>倾斜效果</h5>

再讲一下倾斜效果的处理：

倾斜效果的计算分为三个维度：方向、强度、时间曲线。

1. 方向：

观察iOS26的桌面动画，可以很容易地发现倾斜/扭曲方向的规律

![图片](/docs/DocRes/tilt_dir_1.webp)![图片](/docs/DocRes/tilt_dir_2.webp)![图片](/docs/DocRes/tilt_dir_3.webp)![图片](/docs/DocRes/tilt_dir_4.webp)

CONTAINER与CONTENT取中心点A和B，作向量AB，如图一：A点在B点的左下方，则在动画进行时按向量方向进行扭曲。（图2实际路径受二次贝塞尔曲线影响，不是严格按照直线AB走的，后面讲）

![图片](/docs/DocRes/tilt_vec_1.webp)![图片](/docs/DocRes/tilt_vec_2.webp)

1. 强度

**由尺寸差异和偏移共同决定**

尺寸差异越大，展开幅度越大，倾斜越明显。但不是线性关系，而是二次缓出：

```kotlin
r = widthDelta / content.width   // 尺寸差异比 0~1
f = 1 - (1 - r)²                 // 缓出曲线：差异小时增长慢，差异大时增长快
```

同时计算归一化偏移量dxNorm/dyNorm（-1\~1），中心点越不对齐，倾斜越明显。

最终强度取两组方案的较小值（保守策略，避免极端方向倾斜过度）

```kotlin
tiltStrengthX = min(方案1: heightFactor * dyNorm, 方案2: widthFactor * sign(dx))
```

完整代码：

```kotlin
val (roX, roY) = if (enableTilt) {
    // 进度 插值用
    val progressOfTilt = (progress * speedUpRadioTilt).coerceIn(0f,1f)

    // CONTAINER 中心点
    val cCenterX = container.left + container.width / 2f
    val cCenterY = container.top + container.height / 2f
		// CONTENT 中心点
    val tCenterX = content.left + content.width / 2f
    val tCenterY = content.top + content.height / 2f

  	// 两中心点相对方向
    val dx = cCenterX - tCenterX
    val dy = cCenterY - tCenterY
  
  	// X 与 Y 的方向（正负号）
    val dirY = when {
        dx > 0 -> 1f
        dx < 0 -> -1f
        else -> 0f
    }
    val dirX = when {
        dy > 0 -> -1f
        dy < 0 -> 1f
        else -> 0f
    }

    val dxNorm = (dx / content.width).coerceIn(-1f, 1f)
    val dyNorm = (dy / content.height).coerceIn(-1f, 1f)

    val widthDelta = abs(content.width - container.width)
    val heightDelta = abs(content.height - container.height)

    val widthFactor = 1f - (1f - (widthDelta / content.width).coerceIn(0f, 1f)).let { it * it }
    val heightFactor = 1f - (1f - (heightDelta / content.height).coerceIn(0f, 1f)).let { it * it }

    val tiltStrengthX1 = abs(tiltMaxValue * heightFactor * dyNorm)
    val tiltStrengthY1 = abs(tiltMaxValue * widthFactor * dxNorm)

    val tiltStrengthX2 = abs(tiltMaxValue * widthFactor * sign(dx))
    val tiltStrengthY2 = abs(tiltMaxValue * heightFactor * sign(dy))

    val tiltStrengthX = minOf(tiltStrengthX1, tiltStrengthX2)
    val tiltStrengthY = minOf(tiltStrengthY1, tiltStrengthY2)

    // 二次函数
    val currentTilt = 1f - abs(2f * progressOfTilt - 1f)

    Pair(
       // 方向（正负号） * 当前倾斜度（插值得到） *  倾斜强度
        dirX * currentTilt * tiltStrengthX,
        dirY * currentTilt * tiltStrengthY
    )
} else {
    Pair(0f, 0f)
}
```

> 需注意：iOS26采用的不仅仅是通过改变角度，而是非线性的扭曲，应该是用了Shader，理论上安卓也可以做到，但是我觉得目前已有的简单倾斜也足够像了（其实是没来得及写）

华为鸿蒙最近跟进的效果。

<div data-block-type="resource" data-resource-type="video" data-blob-store-key="docs_enclosure_a1e9bfe3-e201-414d-809f-8e4d395b3879_drag-upload-1782313039336-0" data-file-name="422365ea2d1b54670a8c5311ae038ad1.mp4" data-mime-type="video/mp4" data-file-size="358721"></div>

1. <h5>路径曲线</h5>

定义

```kotlin
typealias RectInterpolator = (progress: Float, from: Rect, to: Rect) -> Rect
```

那么，线性的路径曲线就是：

```kotlin
val LinearRectInterpolator: RectInterpolator = { t, from, to ->
    Rect(
        left = lerp(from.left, to.left, t),
        top = lerp(from.top, to.top, t),
        right = lerp(from.right, to.right, t),
        bottom = lerp(from.bottom, to.bottom, t)
    )
}
```

对比图：（左线性，右非线性）

<div data-block-type="resource" data-resource-type="video" data-blob-store-key="docs_enclosure_d40fd9ca-5c1e-4819-b513-d862d22efcab_drag-upload-1782316624367-0" data-file-name="59aefea7a350d932171474f777e79ef1.mp4" data-mime-type="video/mp4" data-file-size="872172"></div>

<div data-block-type="resource" data-resource-type="video" data-blob-store-key="docs_enclosure_9e70d5ab-8e85-456e-8a56-46d9a4e72b70_drag-upload-1782316629171-0" data-file-name="1de247c8eb67b396faf8befe57c0541e.mp4" data-mime-type="video/mp4" data-file-size="945324"></div>

直接对Rect线性插值的问题是：容器从A点移动到B点，走的是直线。但真实世界里的运动，往往不是严格直线，观察系统桌面的动画，路径是带弧度的——容器不是直来直去，而是走了一条曲线，这让运动更有"弹性"和"自然感"。

使用二次贝塞尔曲线（Quadratic Bezier）来替代线性路径：

核心思路：不再对left/top/right/bottom分别做lerp，而是对两个Rect的**中心点**做贝塞尔插值，宽度/高度仍然做lerp。

1. 取起始中心点startCenter和结束中心点endCenter

2. 计算控制点control：取两点中点，然后根据中点在屏幕中的位置加一个弧度偏移（verticalArc/horizontalArc）

3. 弧度偏移的方向：如果中心点在屏幕上半部，弧度向上；下半部则向下。这样不同位置的容器会有不同方向的弧线

4. 用二次贝塞尔公式计算中间中心点： center = (1-t)² × start + 2(1-t)t × control + t² × end

5. 根据中心点+当前宽高重建Rect

![图片](/docs/DocRes/bezier_path.webp)

二次贝塞尔曲线完整实现：

```kotlin
fun QuadraticBezierRectInterpolator(
    contentHeight: Float,
    contentWidth: Float,
    maxVerticalArc: Float = contentHeight / 2f,
    maxHorizontalArc: Float = contentWidth / 3f
): RectInterpolator = { t, from, to ->

    val startCenter = Offset(
        from.left + from.width / 2f,
        from.top + from.height / 2f
    )

    val endCenter = Offset(
        to.left + to.width / 2f,
        to.top + to.height / 2f
    )

    val avgY = (startCenter.y + endCenter.y) / 2f
    val normalizedY = ((avgY / contentHeight) - 0.5f) * 2f
    val verticalArc = -normalizedY * maxVerticalArc

    val avgX = (startCenter.x + endCenter.x) / 2f
    val normalizedX = ((avgX / contentWidth) - 0.5f) * 2f
    val horizontalArc = -normalizedX * maxHorizontalArc

    val control = Offset(
        x = (startCenter.x + endCenter.x) / 2f + horizontalArc,
        y = (startCenter.y + endCenter.y) / 2f + verticalArc
    )

    val oneMinusT = 1f - t

    val center = Offset(
        x = oneMinusT * oneMinusT * startCenter.x + 2 * oneMinusT * t * control.x + t * t * endCenter.x,
        y = oneMinusT * oneMinusT * startCenter.y + 2 * oneMinusT * t * control.y + t * t * endCenter.y
    )

    val width = lerp(from.width, to.width, t)
    val height = lerp(from.height, to.height, t)

    Rect(
        left = center.x - width / 2f,
        top = center.y - height / 2f,
        right = center.x + width / 2f,
        bottom = center.y + height / 2f
    )
}
```

至此，共享容器动画的核心已经完成设计了，但还有其他若干细节需要处理。

Modifier.sharedContainer & Modifier.sharedContent ：绑定CONTAINER和CONTENT组件

SharedContainerOverlay ： 覆盖在真实界面之上的浮层，真正的动画绘制层

SharedContainerRoot：将SharedContainerOverlay放在真实界面之上

SharedRegistry：管理SharedContainerState

SharedViewModel：用于保持Activity重建时数据不丢失

SharedContainerState：管理各对CONTAINER和CONTENT的参数&状态

StatePause:状态机

ContainerFilledStrategy：填充方案

RectInterpolator：路径曲线插值器（线性/贝塞尔非线性）

之前有提到容器共享要满足一个外部条件，也就是需要提供CONTAINER和CONTENT同时存在的机会。否则GraphicsLayer的record函数捕获不到画面，且不同时存在也影响正确测量Rect。

也就是说上层的页面路由库不能这么写：

```kotlin
when(route) {
	A -> ScreenA()
	B -> ScreenB()
	...
}
```

在任何时刻，都只有一个页面存活，会导致动画异常。

所以对于页面路由（以下统称导航模块），我自己设计了一套。而且目前只实现了容器共享动画，背景特效还没实现，这部分需要交给导航模块。

### （二）页面路由

页面路由模块在设计时不应与容器共享模块发生耦合，导航模块只负责页面导航，提供动画效果，如有共享容器的时候优先使用容器共享，没有时则降级，走导航默认的动画。

对于此模块的详细设计，略，和本文关系不大。

#### 页面生命周期

1. 在动画过程中，两个页面共存：旧页面继续存活，新页面创建，直到动画结束时，旧页面销毁。
2. 其余情况下只存活一个栈顶页面。

why：

两页面共存（listOf(transitionEntry.from, transitionEntry.to)）一是为了做背景动效，如果马上就切换到另一个页面，无法实现动效；二是为共享容器提供环境。

平时只存活一个页面（listOf(navController.stack.last())）是为了流畅度考虑的，如果其他已经不不显示的页面还活着，空浪费资源，且实测页面过多时主线程会卡顿。

```kotlin
@Composable
private fun NavHost(
    navController: NavigationController,
    modifier: Modifier = Modifier,
    dependencies: Dependencies = Dependencies(),
    backHandler: (@Composable () -> Unit) = { DefaultBackHandler() },
) {
    val saveableStateHolder = rememberSaveableStateHolder()

    CompositionLocalProvider(
        LocalNavControllerSafely provides navController,
        LocalNavController provides navController,
        LocalNavDependencies provides dependencies,
    ) {
        backHandler()

        val transitionEntry = navController.transitionEntry
        val progress = navController.transitionProgress

        // 当 transition 变化时启动动画
        LaunchedEffect(
            transitionEntry,
            registry.isRunning,
            registry.isWaitingFrame
        ) {
            navController.animate()
        }

        val visibleEntries = remember(transitionEntry) {
                when (transitionEntry?.type) {
                    ActionType.POP -> listOf(transitionEntry.to, transitionEntry.from)
                    ActionType.PUSH -> listOf(transitionEntry.from, transitionEntry.to)
                    else -> listOf(navController.stack.last())
                }
            }


        Box(
            modifier = modifier.fillMaxSize()
        ) {
            visibleEntries.forEachIndexed { _, entry ->
                key(entry.id) {
                    saveableStateHolder.SaveableStateProvider(entry.id) {
                        val isFrom = transitionEntry?.from == entry
                        val isTo = transitionEntry?.to == entry

                        val animatedProgress = progress.value

                        val effect = finalTransitionMode.pageEffect
                        // 背景与前景效果，通过animatedProgress插值实现
                        val backgroundEffect = remember(animatedProgress,level) { effect.background(animatedProgress,level) }
                        val foregroundEffect = remember(animatedProgress,level) { effect.foreground(animatedProgress, level) }
                        
                        // 为保证界面创建的时候，isTransitioning马上为true，完成后置为false，供开发者监听
                        LaunchedEffect(Unit) {
                            navController.setTransiting()
                        }

                        Box(
                            Modifier
                                .fillMaxSize()
                                ...应用backgroundEffect和foregroundEffect
                                .touchEvent(
                                    enableTouch,interceptTouch
                                )
                        ) {
                            SharedContent(
                                key = entry.destination.key,
                            ) {
                                entry.destination.Content()
                            }
                        }
                    }
                }
            }
        }
    }
}

```

#### 背景&前景动效

接下来看一下背景动效&前景动效的设计

定义参数有：

```kotlin
@Immutable
data class PageEffect(
    // 形状/圆角
    val corner: EffectValue<CornerBasedShape>,
    // 缩放
    val scale: EffectValue<Float> = EffectValue.const(1f),
    // 模糊
    val blur: EffectValue<Dp> = EffectValue.const(0.dp),
    // 遮罩
    val mask: EffectValue<Color> = EffectValue.const(Color.Transparent),
    // 透明度
    val alpha: EffectValue<Float> = EffectValue.const(1f),
    // 位置（配合scale）
    val position: EffectValue<TransformOrigin> = EffectValue.const(TransformOrigin.Center),
    // 位置
    val translationPercent: EffectValue<Offset> = EffectValue.const(Offset.Zero),
    // 角度
    val rotate: EffectValue<Rotation> = EffectValue.const(Rotation()),
)
```

借助这些参数，允许开发者自定义PageEffect，组合成多种动效。

对于背景的处理，参考开头iOS快捷指令的视频，背景从1f逐渐变小到一定阈值，且模糊从0f逐渐增加到一定阈值，并伴随压暗（黑色蒙层从0透明度逐渐增加到一定阈值）

借助上面的参数可以轻易实现。

##### 镜面缩放

特殊讲一下背景缩放scale，在app中使用传统的scale会导致在复杂页面时观感差（如图左，像是画面被"掏空"了）

左：直接缩放scale，边缘观感差，空虚

右：缩放的同时，用镜像画面填充边缘，使观感饱满

![图片](/docs/DocRes/mirror_scale.webp)

代码：

```glsl
uniform shader content;
uniform float2 size;   // 原始画面宽高
uniform float scale;   // 缩放比例，
        
half4 main(float2 fragCoord) {
  float2 center = size * 0.5;
  float2 offset = fragCoord - center;
  
  // 缩放
  float2 scaled = offset / scale;
  float2 sampleCoord = center + scaled;
        
  // 镜面反射逻辑
  if(sampleCoord.x < 0.0) {
  	sampleCoord.x = -sampleCoord.x;
  }
  if(sampleCoord.x > size.x) {
  	sampleCoord.x = 2.0*size.x - sampleCoord.x;      
  }
  
  if(sampleCoord.y < 0.0) {
    sampleCoord.y = -sampleCoord.y;
  }
  if(sampleCoord.y > size.y) {
  	sampleCoord.y = 2.0*size.y - sampleCoord.y;
  }

  return content.eval(sampleCoord);
}
```

> 这个AGSL是基础版，实际跑起来在镜像和实际画面中间又一个非常细的透明线，只是为了方便看核心实现而删减了。下面的是优化版

AGSL完整实现：

```kotlin
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import org.intellij.lang.annotations.Language

fun Modifier.scaleMirror(
    scale: Float,
    enabled : Boolean,
): Modifier =
    if(scale == 1f) {
        this
    } else if(!enabled || Build.VERSION.SDK_INT < 33) {
        this.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    } else {
        composed {
            // 绘制面
            var rect by remember { mutableStateOf<Rect?>(null) }
            this
                .graphicsLayer {
                    rect?.let { r ->
                        val runtimeShader = RuntimeShader(SHADER_CODE.trimIndent())
                        runtimeShader.setFloatUniform("size", r.width, r.height)
                        runtimeShader.setFloatUniform("scale", scale)

                        renderEffect = RenderEffect.createRuntimeShaderEffect(runtimeShader, "content").asComposeRenderEffect()
                    }
                }
                .onGloballyPositioned { layoutCoordinates ->
                    val pos = layoutCoordinates.positionInWindow()
                    val size = layoutCoordinates.size
                    rect = Rect(
                        pos.x,
                        pos.y,
                        pos.x + size.width,
                        pos.y + size.height
                    )
                }
        }
    }


@Language("AGSL")
private const val SHADER_CODE = """
    uniform shader content;
    uniform float2 size;
    uniform float scale;

    // 无缝镜像折叠：将任意坐标映射到 [0, maxVal] 的三角波
    float mirrorFold(float v, float maxVal) {
        float period = 2.0 * maxVal;
        // 先把负数折到正数范围
        v = abs(v);
        // 取模得到 [0, period) 内的值
        v = mod(v, period);
        // 超过 maxVal 的部分再折回来
        if (v > maxVal) v = period - v;
        return v;
    }

    half4 main(float2 fragCoord) {
        float2 center = size * 0.5;
        float2 offset = fragCoord - center;

        // 缩放
        float2 sampleCoord = center + offset / scale;

        // 镜面折叠（支持多次反射）
        sampleCoord.x = mirrorFold(sampleCoord.x, size.x);
        sampleCoord.y = mirrorFold(sampleCoord.y, size.y);

        // 收缩半像素，防止浮点误差导致边缘双线性采样混入透明像素
        sampleCoord = clamp(sampleCoord, float2(0.5), size - float2(0.5));

        return content.eval(sampleCoord);
    }
"""


```

##### 自定义动效示例

<div data-block-type="resource" data-resource-type="video" data-blob-store-key="docs_enclosure_0e3b79b0-45b2-405a-b43b-d714803a9dc6_drag-upload-1782234150955-0" data-file-name="0080a3081557dd745a5c2739a85fdba9.mp4" data-mime-type="video/mp4" data-file-size="1041152"></div>

<div data-block-type="resource" data-resource-type="video" data-blob-store-key="docs_enclosure_00547b8a-6c9a-4f26-8086-7946af4592a0_drag-upload-1782234153879-0" data-file-name="aa9ea8f3064b6e7baf932b764729668e.mp4" data-mime-type="video/mp4" data-file-size="544520"></div>

<div data-block-type="resource" data-resource-type="video" data-blob-store-key="docs_enclosure_17bc59b9-9ffc-4b3f-a2f7-303918ba3afa_drag-upload-1782234155930-0" data-file-name="5a5179e9a027a27beb9ac250456c77ad.mp4" data-mime-type="video/mp4" data-file-size="651750"></div>

<div data-block-type="resource" data-resource-type="video" data-blob-store-key="docs_enclosure_b2888e01-7b11-4ecf-b1a4-eb34b09d2b84_drag-upload-1782234158989-0" data-file-name="6c0175b99efedf290e8112fd162e3fba.mp4" data-mime-type="video/mp4" data-file-size="785639"></div>

## 四、性能瓶颈

### 谁在竞争？

整个动画的生命周期中，性能开销主要来自两部分：**界面初始化**和**动画绘制**，两者都在UI主线程执行。

因为导航的栈顶存活机制，只有栈顶页面是存活的，无论是PUSH还是POP，此时都需要重新走初始化流程，如果页面太重、CPU性能差，会造成竞争，导致动画掉帧。（所以手机厂商想实现桌面动画流畅，应该是魔改了系统的调度，优先保证动画流畅）

模糊也不容小觑，尤其是对整个背景进行实时地模糊。RenderEffect的blur滤镜是GPU密集型操作，模糊半径越大、画面分辨率越高，GPU压力越大。实测在较差设备上关闭模糊确实掉帧率更低，单纯的容器共享（drawLayer + 缩放 + 平移 + 圆角裁切）的开销很低。

AGSL Shader的性能开销也很低：1像素延展和镜面缩放的Shader逻辑极简（每像素只做一次坐标映射 + 一次采样），全部在GPU上完成，CPU零参与。

### HWUI图

背景满动效时（骁龙8gen2）

![图片](/docs/DocRes/hwui_full_8g2.webp)

削弱背景动效后（骁龙8gen2）

![图片](/docs/DocRes/hwui_weak_8g2.webp)

背景满动效时（骁龙855）

![图片](/docs/DocRes/hwui_full_855.webp)

削弱背景动效后（骁龙855）

![图片](/docs/DocRes/hwui_weak_855.webp)

能力不足，Profiler、Perfetto 之类的分析就不放了。

## 五、结语

### 局限

1. 与Compose强绑定，所以只能为其提供思路。
2. 可以自建页面路由框架不使用自带的NavHost，但这会失去背景特效能力，因为背景动效是由导航库在动画期间同时渲染两个页面来实现的。最佳效果需要配合自带的导航库使用。
3. 动画和界面初始化之间地竞态（底层限制，作为应用层无法克服）

### 意义

1. 提供思路和细节实现：许多App已有类似展开/收起效果，但细节不到位（例如填充策略、路径曲线、倾斜效果）。本文拆解了iOS方案的每个细节，可作为后续应用优化的参考。

2. 行业趋势：iOS 26 又带起了这波潮流（以前也有，只是不流行，例如MIUI12），各手机厂商纷纷在适配这个动画，很多国民级应用也早就有这个效果了。

### Apk

Demo：

<div data-block-type="resource" data-resource-type="attachment" data-blob-store-key="docs_enclosure_6260e35c-88d2-41ef-8dda-0aa893879920_drag-upload-1782234791033-0" data-file-name="app-release.apk" data-mime-type="application/vnd.android.package-archive" data-file-size="2037456"></div>

时间有限，就没仔细排版，部分地方还是比较乱的，有时间改进。

## 六、续集

### 性能实测

![图片](/docs/DocRes/perf_1.webp)

![图片](/docs/DocRes/perf_2.webp)

结论：

性能优势+架构优势+视觉优势

相⽐第一代原⽅案（官⽅推荐的 Navigation + Modifier.sharedElements），本框架与本应⽤的适配度更⾼，从⽽简化了开发流程，并带来更精致流畅的⽤⼾体验。

性能优势：GPU 渲染耗时降低约 30% \~ 43%，默认动效下严重卡顿帧占⽐从约 2% 降⾄ 0%。 

架构优势：每个页面都是一个Destination，他们共同继承于一个基类Destination，使得增加页面、跨项目复用页面非常方便（形成了内聚，直接复制就可以了），已大量在个人项目中使用，目前已有100+页面如图

![图片](/docs/DocRes/pages_100.webp)

视觉优势：略，上文有很多截图展示。

### Launcher3原理分析

【待写】