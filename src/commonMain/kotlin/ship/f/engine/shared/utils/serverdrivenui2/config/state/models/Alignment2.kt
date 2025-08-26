package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable

@Serializable
sealed class Alignment2 {
    @Serializable
    sealed class VerticalAlignment2 : Alignment2() {
        @Serializable
        object Top2 : VerticalAlignment2()
        @Serializable
        object CenterVertically2 : VerticalAlignment2()
        @Serializable
        object Bottom2 : VerticalAlignment2()
    }

    @Serializable
    sealed class HorizontalAlignment2 : Alignment2() {
        @Serializable
        object Start2 : HorizontalAlignment2()
        @Serializable
        object CenterHorizontally2 : HorizontalAlignment2()
        @Serializable
        object End2 : HorizontalAlignment2()
    }

    @Serializable
    sealed class HorizontalAndVerticalAlignment2 : Alignment2() {
        @Serializable
        object TopStart2 : HorizontalAndVerticalAlignment2()
        @Serializable
        object TopCenter2 : HorizontalAndVerticalAlignment2()
        @Serializable
        object TopEnd2 : HorizontalAndVerticalAlignment2()
        @Serializable
        object CenterStart2 : HorizontalAndVerticalAlignment2()
        @Serializable
        object Center2 : HorizontalAndVerticalAlignment2()
        @Serializable
        object CenterEnd2 : HorizontalAndVerticalAlignment2()
        @Serializable
        object BottomStart2 : HorizontalAndVerticalAlignment2()
        @Serializable
        object BottomCenter2 : HorizontalAndVerticalAlignment2()
        @Serializable
        object BottomEnd2 : HorizontalAndVerticalAlignment2()
    }
}