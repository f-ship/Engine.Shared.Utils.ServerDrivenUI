package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Size2")
sealed class Size2 {
    @Serializable
    @SerialName("HorizontalSize2")
    sealed class HorizontalSize2 : Size2()
    @Serializable
    @SerialName("VerticalSize2")
    sealed class VerticalSize2 : Size2()
    @Serializable
    @SerialName("HorizontalAndVertical2")
    sealed class HorizontalAndVerticalSize2 : Size2()

    @Serializable
    @SerialName("Fixed2")
    data class Fixed2(
        val height: Int = 0,
        val width: Int = 0,
    ) : HorizontalAndVerticalSize2()

    @Serializable
    @SerialName("FixedHorizontal2")
    data class FixedHorizontal2(
        val width: Int,
    ) : HorizontalSize2()

    @Serializable
    @SerialName("FixedVertical")
    data class FixedVertical2(
        val height: Int,
    ) : VerticalSize2()

    @Serializable
    @SerialName("HorizontalOnlyFill2")
    data class HorizontalOnlyFill2(
        val fill: Float = 1f,
    ) : HorizontalSize2()

    @Serializable
    @SerialName("VerticalOnlyFill2")
    data class VerticalOnlyFill2(
        val fill: Float = 1f,
    ) : VerticalSize2()

    @Serializable
    @SerialName("HorizontalFill2")
    data class HorizontalFill2(
        val fill: Float = 1f,
        val vertical: VerticalSize2? = null,
    ) : HorizontalAndVerticalSize2()

    @Serializable
    @SerialName("VerticalFill2")
    data class VerticalFill2(
        val fill: Float = 1f,
        val horizontal: HorizontalSize2? = null,
    ) : HorizontalAndVerticalSize2()

    @Serializable
    @SerialName("Fill2")
    data class Fill2(
        val horizontalFill: Float = 1f,
        val verticalFill: Float = 1f,
    ) : HorizontalAndVerticalSize2()

    @Serializable
    @SerialName("IntrinsicHorizontalSize2")
    data class IntrinsicHorizontalSize2(
        val horizontal: Intrinsic2,
        val vertical: VerticalSize2? = null,
    ) : HorizontalAndVerticalSize2()

    @Serializable
    @SerialName("IntrinsicVerticalSize2")
    data class IntrinsicVerticalSize2(
        val horizontal: HorizontalSize2? = null,
        val vertical: Intrinsic2,
    ) : HorizontalAndVerticalSize2()

    @Serializable
    @SerialName("Intrinsic2")
    sealed class Intrinsic2 {
        @Serializable
        @SerialName("Max2")
        data object Max2 : Intrinsic2()

        @Serializable
        @SerialName("Min2")
        data object Min : Intrinsic2()
    }

    @Serializable
    @SerialName("Window2")
    data object Window2 : HorizontalAndVerticalSize2()

    @Serializable
    @SerialName("MatchParent2")
    data object MatchParent2 : HorizontalAndVerticalSize2()

    @Serializable
    @SerialName("DefaultSize2")
    data object DefaultSize2 : HorizontalAndVerticalSize2()
}


