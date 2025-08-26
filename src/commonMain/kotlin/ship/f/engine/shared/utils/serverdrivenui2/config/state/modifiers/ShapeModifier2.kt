package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Shapes2.CornerBasedShape2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface ShapeModifier2<S : State2> : StateModifier2 {
    val shape: CornerBasedShape2
    fun c(shape: CornerBasedShape2): S
}