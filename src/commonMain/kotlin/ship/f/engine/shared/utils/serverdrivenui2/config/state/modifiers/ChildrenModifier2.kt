package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface ChildrenModifier2<S : State2> : StateModifier2 {
    val children: List<State2>
    fun c(children: List<State2>): S
}