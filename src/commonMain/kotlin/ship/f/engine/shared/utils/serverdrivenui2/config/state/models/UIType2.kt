package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("UIType2")
sealed class UIType2 {
    @Serializable
    @SerialName("Primary2")
    data object Primary2 : UIType2()
    @Serializable
    @SerialName("Secondary2")
    data object Secondary2 : UIType2()
    @Serializable
    @SerialName("Tertiary2")
    data object Tertiary2 : UIType2()
}
