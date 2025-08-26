package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Shapes2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface ShapesModifier2<S : State2> : StateModifier2 {
    val shapes: Shapes2
    fun c(shapes: Shapes2): S
}