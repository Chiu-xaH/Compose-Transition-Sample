# SharedNav
自研容器共享库与导航库，类似桌面(Launcher)打开关闭动画，支持背景压暗、镜面缩放、模糊，容器底部1像素填充、自适应贝赛尔曲线路径、屏幕圆角插值、内容层一次渲染等特性（正在开发中）

## 快速开始
### 新增全屏界面
1. 新增一个新界面

继承NavDestination
```Kotlin
object NewPageDestination : Destination() {
    override val key = "new_page"

    @Composable
    override fun Screen() {
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
1. Clip 动画结束时稍微位移抽搐的Bug      [P0]
**************************************
13. README书写      [P1]
19. 导航并行动画     [P1]
24. 大屏适配（平行视界）         [P1]
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
1. 一定要保证SharedContainer包裹的组件形状不带圆角，将圆角挪到挪到SharedContainer的corner中