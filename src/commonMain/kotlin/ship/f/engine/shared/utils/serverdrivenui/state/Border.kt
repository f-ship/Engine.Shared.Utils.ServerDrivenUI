package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.state.ColorSchemeState.Color

@Serializable
data class Border(
    val width: Int,
    val color: Color,
)