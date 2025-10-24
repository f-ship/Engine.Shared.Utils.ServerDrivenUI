package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Path2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface PathModifier2<S : State2> : StateModifier2 {
    val path: Path2
    fun c(path: Path2): S
}