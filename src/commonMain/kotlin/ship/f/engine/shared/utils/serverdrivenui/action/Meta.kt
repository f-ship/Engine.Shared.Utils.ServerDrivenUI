package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Meta")
sealed class Meta {
    @Serializable
    @SerialName("None")
    data object None : Meta()
}