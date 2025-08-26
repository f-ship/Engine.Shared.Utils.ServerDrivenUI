package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.UIType2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface UITypeModifier2<S : State2> : StateModifier2 {
    val uiType: UIType2
    fun c(type: UIType2): S
}