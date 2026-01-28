package com.xah.transition.ui.model

// 动画驱动状态机
enum class NavPhase {
    Entering,   // 刚 push
    Active,     // 稳定显示
    Exiting,    // pop 中
    Predictive  // 预测返回
}

/*
            ┌──────────────┐
            │   Entering   │  ← push
            └──────┬───────┘
                   │ 动画完成
                   ▼
            ┌──────────────┐
            │    Active    │  ← 稳定显示
            └──────┬───────┘
                   │ requestPop()
                   ▼
        ┌─────────────────────┐
        │     Exiting         │
        └─────────┬───────────┘
                  │ 动画完成
                  ▼
              （移除）

Predictive 是一个「并行态」：
Active ↔ Predictive ↔ Active
Predictive → Exiting（确认返回）
 */