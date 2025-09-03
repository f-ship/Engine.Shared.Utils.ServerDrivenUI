package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client.Client2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2.Companion.autoMetaId2

@Serializable
@SerialName("FilterGroupMeta2")
data class FilterGroupMeta2(
    override val metaId: MetaId2 = autoMetaId2(),
    val filters: List<FilterMeta2>,
) : Meta2() {
    fun filtersSatisfied(client: Client2) = filters.mapNotNull { filter ->
        (client.get(filter.targetGroup) as? FilterStoreMeta2)?.metas?.run { contains(filter) || isEmpty() }
    }.all { it }
}