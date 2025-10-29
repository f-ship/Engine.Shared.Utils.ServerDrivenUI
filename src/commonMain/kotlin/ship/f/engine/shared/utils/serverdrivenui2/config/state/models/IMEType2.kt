package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("IMEType2")
sealed class IMEType2 {
    @Serializable
    @SerialName("Default2")
    data object Default2 : IMEType2()
    @Serializable
    @SerialName("Next2")
    data object Next2 : IMEType2()
}