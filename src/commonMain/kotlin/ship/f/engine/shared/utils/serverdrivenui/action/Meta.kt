package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import ship.f.engine.shared.utils.serverdrivenui.ElementConfig
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.ID
import ship.f.engine.shared.utils.serverdrivenui.ext.id

@Serializable
@SerialName("Meta")
sealed class Meta {
    abstract val id: ID
    @Serializable
    @SerialName("None")
    data class None(
        override val id: ID = none,
    ) : Meta()

    @Serializable
    @SerialName("Json")
    data class Json(
        val json: JsonObject,
        override val id: ID = autoMetaId(),
    ) : Meta()

    @Serializable
    @SerialName("ScreenConfig")
    data class ScreenConfigMeta(
        val screenConfig: ScreenConfig,
        override val id: ID = autoMetaId(),
    ) : Meta()

    @Serializable
    @SerialName("ElementConfig")
    data class ElementConfigMeta(
        val elementConfig: ElementConfig,
        override val id: ID = autoMetaId(),
    ) : Meta()

    companion object {
        var metaCount = 0
        fun autoMetaId() = id("meta-${metaCount++}")
        val none = id("None")
    }
}