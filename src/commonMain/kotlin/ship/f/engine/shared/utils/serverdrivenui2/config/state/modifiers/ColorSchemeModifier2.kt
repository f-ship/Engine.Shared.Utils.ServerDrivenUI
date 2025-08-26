package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.ColorScheme2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface ColorSchemeModifier2<S : State2> : StateModifier2 {
    val lightColorScheme: ColorScheme2?
    val darkColorScheme: ColorScheme2?
    fun c(
        lightColorScheme: ColorScheme2,
        darkColorScheme: ColorScheme2,
    ): S
}