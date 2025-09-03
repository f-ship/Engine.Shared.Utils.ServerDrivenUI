package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Shapes2")
data class Shapes2(
    val extraSmall: CornerBasedShape2,
    val small: CornerBasedShape2,
    val medium: CornerBasedShape2,
    val large: CornerBasedShape2,
    val extraLarge: CornerBasedShape2,
) {
    // TODO(Alpha-1 <MaterialTheme Update>: Support more corner based shapes)
    @Serializable
    @SerialName("CornerBasedShape2")
    data class CornerBasedShape2(
        val topStart: Float,
        val topEnd: Float,
        val bottomEnd: Float,
        val bottomStart: Float,
    )

    companion object {
        val DefaultShapes2 = Shapes2(
            extraSmall = CornerBasedShape2(4f, 4f, 4f, 4f),
            small = CornerBasedShape2(8f, 8f, 8f, 8f),
            medium = CornerBasedShape2(12f, 12f, 12f, 12f),
            large = CornerBasedShape2(16f, 16f, 16f, 16f),
            extraLarge = CornerBasedShape2(28f, 28f, 28f, 28f),
        )
    }
}