package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface StateIdModifier2<S : State2> : StateModifier2 {
    val id: StateId2
    fun c(id: StateId2): S
}