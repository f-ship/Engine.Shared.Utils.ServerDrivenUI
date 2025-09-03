package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Action2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

@Serializable
@SerialName("PopulatedSideEffectMeta2")
data class PopulatedSideEffectMeta2(
    override val metaId: MetaId2,
    val states: List<State2>,
    val metas: List<Meta2>,
    val onExpected: Map<Id2, List<Pair<StateId2, Action2>>> = mapOf(),
) : Meta2()