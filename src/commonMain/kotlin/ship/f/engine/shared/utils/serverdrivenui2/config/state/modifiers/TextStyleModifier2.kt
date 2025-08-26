package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.TextStyle2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface TextStyleModifier2<S : State2> : StateModifier2 {
    val textStyle: TextStyle2
    fun c(textStyle: TextStyle2): S
}