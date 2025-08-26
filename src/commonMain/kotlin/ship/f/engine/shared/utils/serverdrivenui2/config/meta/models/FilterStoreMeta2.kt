package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2

@Serializable
@SerialName("FilterStoreMeta2")
data class FilterStoreMeta2(
    override val id: MetaId2,
    val metas: MutableList<FilterMeta2> = mutableListOf(),
) : Meta2()