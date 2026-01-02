package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.LiveValue3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.ConditionalValue
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.IntValue
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface ChildrenModifier2<S : State2> : StateModifier2 {
    val children: List<State2>
    fun c(children: List<State2>): S

    val filteredChildren: List<State2>?
    fun modifiedChildren(modifiedChildren: List<State2>?): S

    val filter3: ConditionalValue?
    fun filter3(filter3: ConditionalValue?): S

    val sort3: LiveValue3?
    fun c(sort3: LiveValue3?): S

    val jumpTo3: ConditionalValue?
    fun jumpTo3(jumpTo3: ConditionalValue?): S

    val focus: IntValue?
    fun focus(focus: IntValue?): S
}