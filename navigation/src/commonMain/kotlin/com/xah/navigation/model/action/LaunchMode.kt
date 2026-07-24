package com.xah.navigation.model.action

sealed class LaunchMode(
    open var actionType: ActionType,
    open val reuse : Boolean
) {
    /**
     * 压入到栈顶，如果reuse则检查栈顶是否已有实例，有则复用
     * @param reuse 复用
     * @param keepPreviousAlive
     * ## 是否保留上一个Destination不销毁（仅被覆盖），仅enableKeepAlive=false时生效。传null则跟随上一个Entry的keepPreviousAlive（如果没有上一个Entry，则跟随enableKeepAlive）。
     * ## 原则上只推荐一些浮窗、对话框等轻量场景使用keepPreviousAlive，且不推荐继续Push keepPreviousAlive=false的页面，原因如下：
     * - 例如 [A(keepPreviousAlive=false), B(keepPreviousAlive=true), C(keepPreviousAlive=false)]，在B使用Push到C之后，未设置keepPreviousAlive=true，B的UI会被销毁，但是A因为上次Push仍活着，不跟随B的生命周期，造成了断裂，所以存在潜在的OOM风险。
     * - 例如 [A(keepPreviousAlive=false), B(keepPreviousAlive=true), C(keepPreviousAlive=true), ...]，在B使用Push到C之后，B的UI不会被销毁，A也仍活着，这是正确的。
     * ## 如何规避上面提到的OOM风险
     * 只传入true和null（默认值），就永远不会发生链式断裂，null会自动跟随上一个Entry的keepPreviousAlive，形成链的形式。
     */
    data class Push(
        override val reuse : Boolean = true,
        val keepPreviousAlive : Boolean? = null
    ) : LaunchMode(
        ActionType.PUSH,
        reuse
    )
    /**
     * # 检查栈内是否已有实例
     * ## 没有则Push
     * ## 有则判断reuse
     * ### reuse=true则复用并清除其顶部项
     * ### reuse=false则清除其顶部项（包括自己）然后重新压入
     */
    data class PopToExisting(
        override val reuse : Boolean = true
    ): LaunchMode(
        ActionType.POP,
        reuse
    )
    /**
     * 栈内只有一个项目，如果reuse则检查栈顶是否已有实例，有则复用，并将其余所有项目清除；如果reuse=false则直接清空栈并压入
     * @param reuse 复用
     */
    data class Clear(
        override val reuse : Boolean = true,
    ) : LaunchMode(
        ActionType.POP,
        reuse
    )

    /**
     * 将栈顶替换为当前实例.如果reuse则检查栈顶是否就是该实例，是则复用；如果reuse=false则直接替换栈顶
     * @param reuse 复用
     */
    data class Replace(
        override val reuse : Boolean = true
    ) : LaunchMode(
        ActionType.PUSH,
        reuse
    )
}