package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import ship.f.engine.shared.utils.serverdrivenui.ElementConfig
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.MetaId
import ship.f.engine.shared.utils.serverdrivenui.ext.metaId

@Serializable
@SerialName("Meta")
sealed class Meta {
    abstract val id: MetaId
    @Serializable
    @SerialName("None")
    data class None(
        override val id: MetaId = none,
    ) : Meta()

    @Serializable
    @SerialName("Json")
    data class Json(
        val json: JsonObject,
        override val id: MetaId = autoMetaId(),
    ) : Meta()

    @Serializable
    @SerialName("ScreenConfig")
    data class ScreenConfigMeta(
        val screenConfig: ScreenConfig,
        override val id: MetaId = autoMetaId(),
    ) : Meta()

    @Serializable
    @SerialName("ElementConfig")
    data class ElementConfigMeta(
        val elementConfig: ElementConfig,
        override val id: MetaId = autoMetaId(),
    ) : Meta()

    @Serializable
    @SerialName("ListMeta") // TODO this has an awful name, should probably be renamed to be more specific to filters
    data class FilterMetaStore(
        val metas: List<MetaId> = emptyList(),
        override val id: MetaId = autoMetaId(),
    ) : Meta()

    @Serializable
    @SerialName("FilterMeta")
    data class FilterMeta(
        val name: String,
        val targetGroup: MetaId,
        override val id: MetaId = autoMetaId(),
    ) : Meta()

    @Serializable
    @SerialName("FilterGroupMeta")
    data class FilterGroupMeta(
        val filters: List<FilterMeta>,
        override val id: MetaId = autoMetaId(),
    ) : Meta()

    companion object {
        var metaCount = 0
        fun autoMetaId() = metaId("meta-${metaCount++}")
        val none = metaId("None")
    }
}