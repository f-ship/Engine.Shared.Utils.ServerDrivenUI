package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable

@Serializable
sealed class Arrangement2 {
    @Serializable

    sealed class HorizonalArrangement2 : Arrangement2() {
        @Serializable
        data object Start2 : HorizonalArrangement2()
        @Serializable
        data object End2 : HorizonalArrangement2()
    }

    @Serializable
    sealed class VerticalArrangement2 : Arrangement2() {
        @Serializable
        data object Top2 : VerticalArrangement2()
        @Serializable
        data object Bottom2 : VerticalArrangement2()
    }

    @Serializable
    sealed class HorizontalOrVerticalArrangement2 : Arrangement2() {
        @Serializable
        data object Center2 : HorizontalOrVerticalArrangement2()
        @Serializable
        data object SpaceBetween2 : HorizontalOrVerticalArrangement2()
        @Serializable
        data object SpaceAround2 : HorizontalOrVerticalArrangement2()
        @Serializable
        data object SpaceEvenly2 : HorizontalOrVerticalArrangement2()
    }
}
