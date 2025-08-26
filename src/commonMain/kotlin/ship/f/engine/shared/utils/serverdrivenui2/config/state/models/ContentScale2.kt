package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable

@Serializable
sealed class ContentScale2 {
    @Serializable
    data object Crop2 : ContentScale2()
    @Serializable
    data object Fit2 : ContentScale2()
    @Serializable
    data object FillHeight2 : ContentScale2()
    @Serializable
    data object FillWidth2 : ContentScale2()
    @Serializable
    data object Inside2 : ContentScale2()
    @Serializable
    data object None2 : ContentScale2()
    @Serializable
    data object FillBounds2 : ContentScale2()
}