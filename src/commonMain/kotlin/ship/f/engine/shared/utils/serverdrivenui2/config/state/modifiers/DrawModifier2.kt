package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Draw2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface DrawModifier2<S : State2> : StateModifier2 {
    val draws: List<Draw2>
    fun cD(draws: List<Draw2>): S
}