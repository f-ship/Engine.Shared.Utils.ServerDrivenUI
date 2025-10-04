package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("FontWeight2")
sealed class FontWeight2 {
    @Serializable
    @SerialName("Normal2")
    data object Normal2 : FontWeight2()

    @Serializable
    @SerialName("SemiBold2")
    data object SemiBold2 : FontWeight2()

    @Serializable
    @SerialName("Bold2")
    data object Bold2 : FontWeight2()

    @Serializable
    @SerialName("ExtraBold2")
    data object ExtraBold2 : FontWeight2()
}