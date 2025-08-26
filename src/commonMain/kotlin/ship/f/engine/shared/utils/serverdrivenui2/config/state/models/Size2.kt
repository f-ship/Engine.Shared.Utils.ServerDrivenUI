package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable

@Serializable
sealed class Size2 {
    @Serializable
    data class Fixed2(
        val height: Int = 0,
        val width: Int = 0,
    ) : Size2()

    @Serializable
    data class HorizontalFill2(
        val fill: Float = 1f,
    ) : Size2()

    @Serializable
    data class VerticalFill2(
        val fill: Float = 1f,
    ) : Size2()

    @Serializable
    data class Fill2(
        val horizontalFill: Float = 1f,
        val verticalFill: Float = 1f,
    ) : Size2()

    @Serializable
    data object Window2 : Size2()

    @Serializable
    data object MatchParent2 : Size2()

    @Serializable
    data object DefaultSize2 : Size2()
}


