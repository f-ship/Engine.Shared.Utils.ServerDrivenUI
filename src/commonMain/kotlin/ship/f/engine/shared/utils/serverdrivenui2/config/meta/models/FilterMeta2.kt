package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2

@Serializable
@SerialName("FilterMeta2")
data class FilterMeta2(
    override val metaId: MetaId2,
    val targetStore: MetaId2,
) : Meta2()