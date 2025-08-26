package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.state.State2

// TODO(Alpha-1 <MaterialTheme Update>: Include full support for typography)
interface TypographyModifier2<S : State2> : StateModifier2 {
    val font: String
    fun c(font: String): S
}