package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client3.Client3
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Action2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2.Companion.autoMetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2

@Serializable
@SerialName("SideEffectMeta2")
data class SideEffectMeta2(
    override val metaId: MetaId2 = autoMetaId2(),
    val states: List<StateId2> = listOf(),
    val metas: List<MetaId2> = listOf(),
    val onExpected: Map<Id2, List<Pair<StateId2, Action2>>> = mapOf(),
) : Meta2() {
    fun toPopulated(client: Client3) = PopulatedSideEffectMeta2(
        metaId = metaId,
        states = states.map { client.get(it) }, // TODO this is probably a bug
        metas = metas.mapNotNull { client.getOrNull(it) },
        onExpected = onExpected,
    )
}