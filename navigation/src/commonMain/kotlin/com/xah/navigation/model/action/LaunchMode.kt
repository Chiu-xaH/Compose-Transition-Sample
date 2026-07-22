package com.xah.navigation.model.action

sealed class LaunchMode(
    open var actionType: ActionType,
    open val reuse : Boolean
) {
    /**
     * 压入到栈顶，如果reuse则检查栈顶是否已有实例，有则复用
     * @param reuse 复用
     * @param alive 保持上一个Destination仍活跃，仅ALive策略为SAVE_STATE时才有效
     */
    data class Push(
        override val reuse : Boolean = true,
        val alive : Boolean = true
    ) : LaunchMode(ActionType.PUSH,reuse)
    /**
     * # 检查栈内是否已有实例
     * ## 没有则Push
     * ## 有则判断reuse
     * ### reuse=true则复用并清除其顶部项
     * ### reuse=false则清除其顶部项（包括自己）然后重新压入
     */
    data class PopToExisting(
        override val reuse : Boolean = true
    ): LaunchMode(ActionType.POP,reuse)
    /**
     * 栈内只有一个项目，如果reuse则检查栈顶是否已有实例，有则复用，并将其余所有项目清除；如果reuse=false则直接清空栈并压入
     * @param reuse 复用
     */
    data class Single(
        override val reuse : Boolean = true,
        override var actionType: ActionType = ActionType.PUSH
    ) : LaunchMode(actionType,reuse)

    /**
     * 将栈顶替换为当前实例.如果reuse则检查栈顶是否就是该实例，是则复用；如果reuse=false则直接替换栈顶
     * @param reuse 复用
     */
    data class Replace(
        override val reuse : Boolean = true
    ) : LaunchMode(ActionType.PUSH,reuse)
}