package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Size2")
sealed class Size2 {
    @Serializable
    @SerialName("Fixed2")
    data class Fixed2(
        val height: Int = 0,
        val width: Int = 0,
    ) : Size2()

    @Serializable
    @SerialName("HorizontalFill2")
    data class HorizontalFill2(
        val fill: Float = 1f,
    ) : Size2()

    @Serializable
    @SerialName("VerticalFill2")
    data class VerticalFill2(
        val fill: Float = 1f,
    ) : Size2()

    @Serializable
    @SerialName("Fill2")
    data class Fill2(
        val horizontalFill: Float = 1f,
        val verticalFill: Float = 1f,
    ) : Size2()

    @Serializable
    @SerialName("Window2")
    data object Window2 : Size2()

    @Serializable
    @SerialName("MatchParent2")
    data object MatchParent2 : Size2()

    @Serializable
    @SerialName("DefaultSize2")
    data object DefaultSize2 : Size2()
}


