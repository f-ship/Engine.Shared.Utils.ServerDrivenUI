package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Alignment2")
sealed class Alignment2 {
    @Serializable
    @SerialName("VerticalAlignment2")
    sealed class VerticalAlignment2 : Alignment2() {
        @Serializable
        @SerialName("Top2")
        object Top2 : VerticalAlignment2()
        @Serializable
        @SerialName("CenterVertically2")
        object CenterVertically2 : VerticalAlignment2()
        @Serializable
        @SerialName("Bottom2")
        object Bottom2 : VerticalAlignment2()
    }

    @Serializable
    @SerialName("HorizontalAlignment2")
    sealed class HorizontalAlignment2 : Alignment2() {
        @Serializable
        @SerialName("Start2")
        object Start2 : HorizontalAlignment2()
        @Serializable
        @SerialName("CenterHorizontally2")
        object CenterHorizontally2 : HorizontalAlignment2()
        @Serializable
        @SerialName("End2")
        object End2 : HorizontalAlignment2()
    }

    @Serializable
    @SerialName("HorizontalAndVerticalAlignment2")
    sealed class HorizontalAndVerticalAlignment2 : Alignment2() {
        @Serializable
        @SerialName("TopStart2")
        object TopStart2 : HorizontalAndVerticalAlignment2()
        @Serializable
        @SerialName("TopCenter2")
        object TopCenter2 : HorizontalAndVerticalAlignment2()
        @Serializable
        @SerialName("TopEnd2")
        object TopEnd2 : HorizontalAndVerticalAlignment2()
        @Serializable
        @SerialName("CenterStart2")
        object CenterStart2 : HorizontalAndVerticalAlignment2()
        @Serializable
        @SerialName("Center2")
        object Center2 : HorizontalAndVerticalAlignment2()
        @Serializable
        @SerialName("CenterEnd2")
        object CenterEnd2 : HorizontalAndVerticalAlignment2()
        @Serializable
        @SerialName("BottomStart2")
        object BottomStart2 : HorizontalAndVerticalAlignment2()
        @Serializable
        @SerialName("BottomCenter2")
        object BottomCenter2 : HorizontalAndVerticalAlignment2()
        @Serializable
        @SerialName("BottomEnd2")
        object BottomEnd2 : HorizontalAndVerticalAlignment2()
    }
}
