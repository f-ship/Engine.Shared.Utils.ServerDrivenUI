package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Alignment2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface AlignmentModifier2<S : State2> : StateModifier2 {
    val alignment: Alignment2
    fun c(alignment: Alignment2): S
}