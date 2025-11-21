package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Draw2")
sealed class Draw2 {
    @Serializable
    @SerialName("Behind2")
    sealed class Behind2 : Draw2() {
        @Serializable
        @SerialName("Circle2")
        data class Circle2(
            val radius: Float? = null,
            val color: ColorScheme2.Color2,
        ) : Behind2()

        @Serializable
        @SerialName("Rectangle2")
        data class Rectangle2(
            val roundRect: RoundRect2 = RoundRect2(),
            val color: ColorScheme2.Color2,
        ) : Behind2() {
            @Serializable
            @SerialName("RoundRect2")
            data class RoundRect2(
                val rect: Rect2 = Rect2(),
                val topLeft: Float = 0f,
                val topRight: Float = 0f,
                val bottomLeft: Float = 0f,
                val bottomRight: Float = 0f,
            )

            @Serializable
            @SerialName("Rect2")
            data class Rect2(
                val left: Float? = null, // Defaults to 0
                val top: Float? = null, // Defaults to 0
                val right: Float? = null, // Defaults to size.width
                val bottom: Float? = null, // Defaults to size.height
            )
        }
    }

    @Serializable
    @SerialName("Blank2")
    data object Blank2 : Draw2()

    @Serializable
    @SerialName("Border2")
    data class Border2(
        val width: Float = 1f,
        val color: ColorScheme2.Color2,
        val fill: ColorScheme2.Color2 = ColorScheme2.Color2.Unspecified,
        val padding: PaddingValues2 = PaddingValues2(),
        val shape: Shapes2.CornerBasedShape2 = Shapes2.DefaultShapes2.small,
    ) : Draw2()

    @Serializable
    @SerialName("Offset2")
    data class Offset2(
        val x: Float = 0f,
        val y: Float = 0f,
    ) : Draw2()

    @Serializable
    @SerialName("RadialOffset2")
    data class RadialOffset2(
        val radius: Float = 0f,
        val angle: Float = 0f,
    ) : Draw2()
}