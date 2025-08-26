package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Border2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface BorderModifier2<S : State2> : StateModifier2 {
    val border: Border2
    fun c(border: Border2): S
}