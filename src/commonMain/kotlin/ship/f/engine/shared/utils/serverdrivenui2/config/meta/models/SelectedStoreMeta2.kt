package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.RemoteAction2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.Swap2.Companion.swap2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2.Companion.autoMetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

@Serializable
@SerialName("SelectedStoreMeta2")
data class SelectedStoreMeta2(
    override val metaId: MetaId2 = autoMetaId2(),
    val map: MutableMap<String, Pair<MutableList<RemoteAction2<*>>, MutableList<RemoteAction2<*>>>> = mutableMapOf(),
    val selected: String? = null,
) : Meta2() { // TODO optimise to only run on positive and negative based on previous selection
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

    //TODO is hacky but need to refresh a swap, damn I just need to reimplement this completely
    fun replaceSwap(key: String, positive: State2) {
        map.remove(key)
        addSwap(key, positive)
    }
}

@Serializable
@SerialName("ToggleMachineMeta2")
data class ToggleMachineMeta2(
    override val metaId: MetaId2 = autoMetaId2(),
    val selected: MutableList<StateId2> = mutableListOf(),
    val map: MutableMap<StateId2, SwapOperation2> = mutableMapOf(),
    val limit: Int = Int.MAX_VALUE,
) : Meta2() {
    @Serializable
    data class SwapOperation2(
        val active: State2,
        val inactive: State2,
    )

    fun addSwap(active: State2, inactive: State2) {
        map[active.id] = SwapOperation2(active, inactive)
    }
}

@Serializable
@SerialName("StateMachineMeta2")
data class StateMachineMeta2(
    override val metaId: MetaId2 = autoMetaId2(),
    val selected: List<String> = listOf(),
    val map: MutableMap<String, List<StateMachineOperation2>> = mutableMapOf()
) : Meta2() {

    @Serializable
    @SerialName("StateMachineOperation2")
    sealed class StateMachineOperation2 {
        @Serializable
        @SerialName("SwapOperation2")
        data class SwapOperation2(
            val active: State2,
            val inactive: State2,
        ) : StateMachineOperation2()
        @Serializable
        @SerialName("PushOperation2")
        data class PushOperation2(
            val container: StateId2,
            val stateId: StateId2,
        ) : StateMachineOperation2()
        @Serializable
        @SerialName("NestedOperation2")
        data class NestedOperation2(
            val map: MutableMap<String, List<StateMachineOperation2>> = mutableMapOf(),
        ) : StateMachineOperation2()
    }

    fun addSwap(keys: List<String>, active: State2, inactive: State2, map: MutableMap<String, List<StateMachineOperation2>>): StateMachineOperation2 {
        val key = keys.first()
        return if (keys.size > 1) {
            StateMachineOperation2.NestedOperation2().also {
                val newKeys = keys.drop(1)
                val newFirst = newKeys.first()
                it.map[newFirst] = map.getOrElse(newFirst) { listOf() } + addSwap(newKeys, active, inactive, mutableMapOf())
            }
        } else {
           StateMachineOperation2.SwapOperation2(active, inactive)
        }.also {
            map[key] = map.getOrElse(key) { listOf() } + it
        }
    }

    fun addPush(keys: List<String>, container: StateId2, stateId: StateId2, map: MutableMap<String, List<StateMachineOperation2>>): StateMachineOperation2 {
        val key = keys.first()
        return if (keys.size > 1) {
            StateMachineOperation2.NestedOperation2().also {
                val newKeys = keys.drop(1)
                val newFirst = newKeys.first()
                it.map[newFirst] = map.getOrElse(newFirst) { listOf() } + addPush(newKeys, container, stateId, mutableMapOf())
            }
        } else {
            StateMachineOperation2.PushOperation2(container, stateId)
        }.also {
            map[key] = map.getOrElse(key) { listOf() } + it
        }
    }

    fun getOperations(keys: List<String>, map: MutableMap<String, List<StateMachineOperation2>> = this.map): List<StateMachineOperation2> {
        val operations = mutableListOf<StateMachineOperation2>()
        keys.forEachIndexed { index, key ->
            val newOps = map[key] ?: listOf()
            operations += newOps
            operations += newOps.filterIsInstance<StateMachineOperation2.NestedOperation2>().flatMap {
                getOperations(keys.drop(1), it.map)
            }
        }
        return operations
    }
}
