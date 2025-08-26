package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.FontWeight2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface FontWeightModifier2<S : State2> : StateModifier2 {
    val fontWeight: FontWeight2
    fun c(value: FontWeight2): S
}