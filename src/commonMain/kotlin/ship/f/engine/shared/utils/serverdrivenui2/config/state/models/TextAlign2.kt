package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("TextAlign2")
sealed class TextAlign2 {
    @Serializable
    @SerialName("Left2")
    data object Left2 : TextAlign2()
    @Serializable
    @SerialName("Right2")
    data object Right2 : TextAlign2()
    @Serializable
    @SerialName("Start2")
    data object Start2 : TextAlign2()
    @Serializable
    @SerialName("End2")
    data object End2 : TextAlign2()
    @Serializable
    @SerialName("Center2")
    data object Center2 : TextAlign2()
    @Serializable
    @SerialName("Justify2")
    data object Justify2 : TextAlign2()
    @Serializable
    @SerialName("Unspecified2")
    data object Unspecified2 : TextAlign2()
}