package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import ship.f.engine.shared.utils.serverdrivenui.ElementConfig
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig

@Serializable
@SerialName("Meta")
sealed class Meta {
    @Serializable
    @SerialName("None")
    data object None : Meta()

    @Serializable
    @SerialName("Json")
    data class Json(val json: JsonObject) : Meta()

    @Serializable
    @SerialName("ScreenConfig")
    data class ScreenConfigMeta(val screenConfig: ScreenConfig) : Meta()

    @Serializable
    @SerialName("ElementConfig")
    data class ElementConfigMeta(val elementConfig: ElementConfig) : Meta()
}