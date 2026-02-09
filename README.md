# Navigation-Transition-Share

使用Compose Multiplatform对Navigation和Modifier.shareElement/shareBounds封装创建的一个容器过渡转场动画，支持在NavHost下无限打开N级界面，有四档背景特效调节(模糊、缩放、压暗)

## 重构计划
上周重开了项目，打算作为第二代，导航栈不用官方的库了，无论是Naviagtion2还是3，共享容器也不用Compose的Modifier.shareElements和shareBounds了，可定制性太低了，一代虽然借助上面的Api二次封装实现了动效，但是效果、性能都不太满意。

这次重写后，性能好了很多，可定制性也大大提高了，但是仍然有很多问题还未处理，例如打断动画、并发动画等，目前还无法达到能用的程度，全都处理的差不多了Push代码吧，等正式上线应该要很久了……（视频见下图链接）

https://github.com/Chiu-xaH/Navigation-Transition-Share/discussions/2

## 平台
Wasm(JS)、Desktop(JVM)、iOS、Android

## 命令
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun  # Wasm Run In Development
./gradlew :composeApp:wasmJsBrowserProductRun  # Wasm Run In Product
./gradlew :composeApp:wasmJsBrowserDistribution # Wasm打包
./gradlew :composeApp:packageDistributionForCurrentOS # 打包
./gradlew :composeApp:run --no-configuration-cache # Jvm/Desktop Run
./gradlew :composeApp:jsBrowserDevelopmentRun  # Wasm Run In Development
./gradlew :composeApp:jsBrowserProductRun  # Wasm Run In Product
./gradlew jsBrowserDistribution # Js打包
```

## 演示
![截图](/src/shot.jpg)

- 单个连续打断动画

https://github.com/user-attachments/assets/51bc9241-05bc-4c90-b066-ccab3024d21c

- 多个连续打断动画

https://github.com/user-attachments/assets/7f506d51-026e-429a-b8b1-778908eac16c

- 正常开闭动画

https://github.com/user-attachments/assets/ba3f9f38-00fb-41fc-8e40-dde1b3caa837

- 全场景打断动画

https://github.com/user-attachments/assets/5eca9ee9-7c40-4199-b082-3697d8109140

## 食用
Import 模块 :transition

[具体使用案例](https://github.com/Chiu-xaH/HFUT-Schedule)

## 下一步计划
手搓导航栈以解决Navigation2的性能问题
