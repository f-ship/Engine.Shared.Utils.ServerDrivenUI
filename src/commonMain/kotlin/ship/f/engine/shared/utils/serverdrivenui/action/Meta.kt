package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import ship.f.engine.shared.utils.serverdrivenui.ElementOperation
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.*
import ship.f.engine.shared.utils.serverdrivenui.ext.metaId
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Action2

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
    @SerialName("Acknowledge")
    data class Acknowledge(
        override val id: MetaId = autoMetaId(),
        val completedId: MetaId = none,
    ) : Meta()

    @Serializable
    @SerialName("Json")
    data class Json(
        val json: JsonObject,
        override val id: MetaId = autoMetaId(),
    ) : Meta()

    @Serializable
    @SerialName("ScreenConfigMeta")
    data class ScreenConfigMeta(
        val screenConfig: ScreenConfig,
        override val id: MetaId = autoMetaId(),
    ) : Meta()

    @Serializable
    @SerialName("ElementConfigMeta")
    data class ElementConfigMeta(
        val elementConfig: ElementOperation,
        override val id: MetaId = autoMetaId(),
    ) : Meta()

    @Serializable
    @SerialName("ScreenReferenceMeta")
    data class ScreenReferenceMeta(
        val screenId: ScreenId,
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

    @Serializable
    @SerialName("SideEffectMeta")
    data class SideEffectMeta(
        override val id: MetaId,
        val screenId: ScreenId = ScreenId.none,
        val elements: List<ElementId> = listOf(),
        val metas: List<MetaId> = listOf(),
        val onExpected: Map<ID, Action2> = mapOf(),
    ) : Meta()

    companion object {
        var metaCount = 0
        fun autoMetaId() = metaId("meta-${metaCount++}")
        val none = metaId("None")
        const val DEFAULT_META_SCOPE = "DefaultMetaScope"
    }
}