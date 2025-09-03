package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ContentScale2")
sealed class ContentScale2 {
    @Serializable
    @SerialName("Crop2")
    data object Crop2 : ContentScale2()
    @Serializable
    @SerialName("Fit2")
    data object Fit2 : ContentScale2()
    @Serializable
    @SerialName("FillHeight2")
    data object FillHeight2 : ContentScale2()
    @Serializable
    @SerialName("FillWidth2")
    data object FillWidth2 : ContentScale2()
    @Serializable
    @SerialName("Inside2")
    data object Inside2 : ContentScale2()
    @Serializable
    @SerialName("None2")
    data object None2 : ContentScale2()
    @Serializable
    @SerialName("FillBounds2")
    data object FillBounds2 : ContentScale2()
}