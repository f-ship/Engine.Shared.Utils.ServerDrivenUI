package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Arrangement2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface ArrangementModifier2 <S : State2> : StateModifier2 {
    val arrangement: Arrangement2
    fun c(arrangement: Arrangement2): S
}