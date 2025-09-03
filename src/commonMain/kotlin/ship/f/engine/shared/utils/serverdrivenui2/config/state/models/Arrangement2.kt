package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Arrangement2")
sealed class Arrangement2 {
    @Serializable
    @SerialName("HorizonalArrangement2")
    sealed class HorizonalArrangement2 : Arrangement2() {
        @Serializable
        @SerialName("Start2")
        data object Start2 : HorizonalArrangement2()
        @Serializable
        @SerialName("End2")
        data object End2 : HorizonalArrangement2()
    }

    @Serializable
    @SerialName("VerticalArrangement2")
    sealed class VerticalArrangement2 : Arrangement2() {
        @Serializable
        @SerialName("Top2")
        data object Top2 : VerticalArrangement2()
        @Serializable
        @SerialName("Bottom2")
        data object Bottom2 : VerticalArrangement2()
    }

    @Serializable
    @SerialName("HorizontalOrVerticalArrangement2")
    sealed class HorizontalOrVerticalArrangement2 : Arrangement2() {
        @Serializable
        @SerialName("Center2")
        data object Center2 : HorizontalOrVerticalArrangement2()
        @Serializable
        @SerialName("SpaceBetween2")
        data object SpaceBetween2 : HorizontalOrVerticalArrangement2()
        @Serializable
        @SerialName("SpaceAround2")
        data object SpaceAround2 : HorizontalOrVerticalArrangement2()
        @Serializable
        @SerialName("SpaceEvenly2")
        data object SpaceEvenly2 : HorizontalOrVerticalArrangement2()
    }
}
