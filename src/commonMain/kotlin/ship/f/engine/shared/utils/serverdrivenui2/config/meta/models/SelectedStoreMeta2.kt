package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.RemoteAction2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.Swap2.Companion.swap2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2.Companion.autoMetaId2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

@Serializable
@SerialName("SelectedStoreMeta2")
data class SelectedStoreMeta2(
    override val metaId: MetaId2 = autoMetaId2(),
    val map: MutableMap<String, Pair<MutableList<RemoteAction2<*>>, MutableList<RemoteAction2<*>>>> = mutableMapOf(),
    val selected: String? = null,
) : Meta2() {
    fun addSwap(key: String, positive: State2, negative: State2? = null) {
        if (map[key] == null) {
            map[key] = Pair(mutableListOf(), mutableListOf())
        }
        map[key] = map[key]!!.copy(
            first = map[key]!!.first.apply { add(positive.swap2()) },
            second = map[key]!!.second.apply {
                if (negative != null) add(negative.swap2())
            }
        )
    }
}