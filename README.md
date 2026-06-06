# SharedNav  [![](https://jitpack.io/v/Chiu-xaH/SharedNav.svg)](https://jitpack.io/#Chiu-xaH/SharedNav)
基于 Compose 的容器共享（SharedContainer）、页面导航（Navigation）以及全局浮窗（FloatingWindow）库。支持背景模糊、镜面缩放，1像素填充、贝赛尔曲线、屏幕圆角插值、内容一次渲染、预测式返回、并行动画等特性；旨在减少开发流程、提高可定制性。

![cover](src/cover.jpg)

## [开发文档](docs/Developer.md)

## [Demo App](https://github.com/Chiu-xaH/SharedNav/releases/download/1.0.0-dev01/app-release.apk)

## 快速开始

**⚠️ 本库目前仍处于Dev开发阶段，目前已在[聚在工大](https://github.com/Chiu-xaH/HFUT-Schedule)项目中实际使用，其余项目推荐等正式版发布后再接入本库**

### 引入依赖

在settings.gradle添加

Groovy使用
```Groovy
maven { 
    url 'https://jitpack.io'
}
```
Kotlin使用
```Kotlin
maven {
    url = uri("https://jitpack.io")
}
```
添加依赖，版本以 Release 的 Tag 为准
```Groovy
implementation("com.github.Chiu-xaH:SharedNav:<version>")
```

---

### 创建第一个 Destination

每个页面对应一个 `Destination` 对象，继承抽象类并实现 `key` 与 `Content()`：

```kotlin
object HomeDestination : Destination() {
    override val key = "home"   // 全局唯一，同时用作容器共享的匹配 Key

    @Composable
    override fun Content() {
        HomeScreen()
    }
}
```

---

### 初始化导航宿主

在 Activity 或顶层 Composable 中启动导航：

```kotlin
val navController = rememberNavController(startDestination = HomeDestination)

SharedNavHost(
    navController = navController,
)
```

---

### 页面跳转与返回

在任意 Composable 中通过 `LocalNavController` 或 `LocalNavControllerSafely` 获取控制器：

> **提示**：如果无法保证 Composable 函数一定在 `SharedNav` 下调用，请使用 `LocalNavControllerSafely`，它返回可空对象；`LocalNavController` 获取不到控制器时会直接抛出异常导致 Crash。

```kotlin
@Composable
fun HomeScreen() {
    val navController = LocalNavController.current

    Button(onClick = { navController.push(DetailDestination) }) {
        Text("进入详情")
    }

    Button(onClick = { navController.pop() }) {
        Text("返回")
    }
}
```

---

### 添加容器共享动效

用 `SharedContainer` 包裹触发跳转的组件，`key` 与目标 `Destination.key` 保持一致，即可获得类 Launcher 的展开/收起动画。

**写法 1：**

```kotlin
@Composable
fun HomeScreen() {
    val navController = LocalNavController.current
    val dest = DetailDestination

    SharedContainer(
        key = dest.key,
        shape = MaterialTheme.shapes.medium,
    ) {
        Card(
            shape = RectangleShape,
            onClick = { navController.push(dest) }
        ) { /* 内容 */ }
    }
}
```

**写法 2：Modifier 扩展**

```kotlin
@Composable
fun HomeScreen() {
    val navController = LocalNavController.current
    val dest = DetailDestination

    Card(
        modifier = Modifier.sharedContainer(
            key = dest.key,
            shape = MaterialTheme.shapes.medium
        ),
        shape = RectangleShape,
        onClick = { navController.push(dest) }
    ) { /* 内容 */ }
}
```

> **注意**：`SharedContainer` 内层组件的 `shape` 必须设置为无圆角，圆角统一由外层 `SharedContainer` 管理，否则在提取 1 像素时会缺失边角。

## 编译为 Release aar
```bash
./gradlew assembleRelease
```

## 后续计划
3. 大屏适配         [P1]
4. 支持更自由的自定义
4. 浮窗并行动画            [P1]
8. Kotlin Multiplatform         [P4]


## Pull Request 须知（参与本项目）
- 本项目AI真的Hold不住（可能是同类项目较少），请优先考虑使用古法编程
- 本项目一般作为应用的骨架，要详细测试多个安卓版本、多种情况下的运行状态，以防万一
- 修改后需临时测试时，可以编译运行项目中的:app模块，也可以打包为aar，然后放置到其他项目导入，运行测试效果。
