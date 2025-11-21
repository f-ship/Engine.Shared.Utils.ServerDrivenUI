package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.ConditionalLiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface ChildrenModifier2<S : State2> : StateModifier2 {
    val children: List<State2>
    fun c(children: List<State2>): S

    val filteredChildren: List<State2>?
    fun filteredChildren(filteredChildren: List<State2>?): S

    val filter: List<ConditionalLiveValue2>?
    fun filter(filter: List<ConditionalLiveValue2>?): S

    val sort: LiveValue2?
    fun c(sort: LiveValue2?): S

    val jumpTo: List<ConditionalLiveValue2>?
    fun jumpTo(jumpTo: List<ConditionalLiveValue2>?): S
}