package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Color2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.ConditionalValue
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface ColorModifier2<S : State2> : StateModifier2 {
    val color: Color2
    fun c(color: Color2): S

    val liveColor: ConditionalValue?

    fun liveColor(liveColor: ConditionalValue): S
}