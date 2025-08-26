package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface SizeModifier2<S : State2> : StateModifier2 {
    val size: Size2
    fun c(size: Size2): S
}