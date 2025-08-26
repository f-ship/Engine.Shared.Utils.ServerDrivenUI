package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.PaddingValues2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface PaddingModifier2<S : State2> : StateModifier2 {
    val padding: PaddingValues2
    fun c(padding: PaddingValues2): S
}