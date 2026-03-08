# SharedNav
自研容器共享库与导航库，类似桌面(Launcher)打开关闭动画，支持背景压暗、镜面缩放、模糊，容器底部1像素填充、自适应贝赛尔曲线路径、屏幕圆角插值、内容层一次渲染等特性（正在开发中）

<img src="/src/example.png" alt="UpgradeLink" height="500">

## 特点
1. 背景：模糊、压暗、缩放
2. 容器：提取底部1像素填充、颜色填充、裁切放大填充
3. 动画曲线：贝塞尔曲线路径、回弹
4. 适配屏幕圆角，插值过渡
5. 性能开销低，内容层不重复测量，一次渲染
6. 支持大屏适配
7. 并行打断动画
8. 更符合直觉的预测式手势

## 快速开始
### 新增全屏界面
1. 新增一个新界面

继承NavDestination
```Kotlin
object NewPageDestination : SharedDestination() {
    override val key = "new_page"

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        NewPageScreen(vm)
    }
}
```
在需要进入本界面的地方调用
```Kotlin
@Composable
fun FromScreen() {
    val navController = LocalNavController.current
    
    ListItem(
        onClick = {
            navController.push(NewPageDestination)
        }
    )
}
```

### 引入容器共享动效
用SharedContainer包裹，以Destination的key作为key传入，传入容器的Shape、ContainerColor，并将内容器形状置为Rectangle
```Kotlin
@Composable
fun FromScreen() {
    val navController = LocalNavController.current
    val dest = NewPageDestination
    
    SharedContainer(
        key = dest.key,
        shape = Material.shapes.medium,
        containerColor = Material.colorScheme.primaryContainer
    ) {
        ListItem(
            onClick = {
                navController.push(dest)
            }
        )
    }
}
```
写完后务必测试无问题

## TODO
打开关闭的模糊度不同优化     [P0]
**************************************
31. 导航曲线优化      [P1]
13. README书写      [P1]
19. 导航并行动画     [P1]
24. 大屏适配（采用平行视界方案）         [P1]
3. spring回弹时最后卡顿的Bug    [P1]
**************************************
10. 容器共享预测式返回的适配     [P2]
11. 导航预测式返回的适配     [P2]
**************************************
22. 背景模糊、缩放speedRadio         [P3]
25. deeplink         [P4]
20. 元素共享及其导航适配     [P4]
12. KMP适配         [P4]

## 注意事项
1. 打开时因为点击水波纹，会与填充形成差异，使用1像素提取或将clickable挪到SharedContainer的modifier中即可解决
2. 使用1像素提取时，一定要保证SharedContainer包裹的组件形状不带圆角，将圆角挪到挪到SharedContainer的corner中