package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.ColorScheme2.Color2

@Serializable
@SerialName("Border2")
data class Border2(
    val width: Int,
    val color: Color2,
) {
    companion object {
        val default = Border2(1, Color2.Outline)
    }
}
