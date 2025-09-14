package ship.f.engine.shared.utils.serverdrivenui2.client

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.jetbrains.compose.resources.Resource
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.ToJsonAction2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.ext.g2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

@Suppress("UNCHECKED_CAST")
open class CommonClient2 protected constructor(override val projectName: String? = null) : Client2() {

    @Dev
    val focusedState: MutableState<Id2.StateId2?> = mutableStateOf(Id2.StateId2("agenda"))

    @Dev
    var isDev: Boolean = true

    @Dev
    fun changeFocus(direction: FocusDirection) { //TODO rewrite ugly debugging code with build variant
        when(direction) {
            FocusDirection.After -> {
                focusedState.value?.let { focused ->
                    val id = reverseIdMap[focusedState.value]
                    if (id == null) return
                    (get<State2>(id) as? ChildrenModifier2<State2>)?.let { parent ->
                        val index = parent.children.indexOfFirst { it.id == focused }
                        if (index + 1 < parent.children.size) {
                            val focus = parent.children[index + 1].id
                            focusedState.value = focus
                            get<State2>(focus).apply {
                                update { reset() }
                            }
                            ToJsonAction2(
                                targetStateId = focus,
                                targetMetaId = Id2.MetaId2("json-meta")
                            ).run(
                                state = get<State2>(focus),
                                client = this@CommonClient2
                            )
                        }
                    }
                }
            }
            FocusDirection.Before -> {
                focusedState.value?.let { focused ->
                    val id = reverseIdMap[focusedState.value]
                    if (id == null) return
                    (get<State2>(id) as? ChildrenModifier2<State2>)?.let { parent ->
                        val index = parent.children.indexOfFirst { it.id == focused }
                        if (index - 1 >= 0) {
                            val focus = parent.children[index - 1].id
                            focusedState.value = focus
                            get<State2>(focus).apply {
                                update { reset() }
                            }
                            ToJsonAction2(
                                targetStateId = focus,
                                targetMetaId = Id2.MetaId2("json-meta")
                            ).run(
                                state = get<State2>(focus),
                                client = this@CommonClient2
                            )
                        }
                    }
                }
            }
            FocusDirection.Down -> {
                focusedState.value?.let {
                    (get<State2>(it) as? ChildrenModifier2<State2>)?.let { parent ->
                        val focus = parent.children.firstOrNull()?.id
                        if (focus == null) return
                        focusedState.value = focus
                        get<State2>(focus).apply {
                            update { reset() }
                        }
                        ToJsonAction2(
                            targetStateId = focus,
                            targetMetaId = Id2.MetaId2("json-meta")
                        ).run(
                            state = get<State2>(focus),
                            client = this@CommonClient2
                        )
                    }
                }
            }
            FocusDirection.Up -> {
                val focus = reverseIdMap[focusedState.value]
                if (focus == null) return
                focusedState.value = focus
                get<State2>(focus).apply {
                    update { reset() }
                }
                ToJsonAction2(
                    targetStateId = focus,
                    targetMetaId = Id2.MetaId2("json-meta")
                ).run(
                    state = get<State2>(focus),
                    client = this@CommonClient2
                )
            }
        }
    }

    @Dev
    sealed class FocusDirection {
        data object Up : FocusDirection()
        data object Down : FocusDirection()
        data object Before : FocusDirection()
        data object After : FocusDirection()
    }

    val reactiveStateMap: MutableMap<Id2, MutableState<State2>> = mutableMapOf()
    val reactiveBackStack: SnapshotStateList<BackStackEntry2> = mutableStateListOf()

    val resourceMap: MutableMap<String, Resource> = mutableMapOf()
    fun addResource(id: String, resource: Resource) = resourceMap.put(id, resource)
    fun addResources(resources: Map<String, Resource>) = resourceMap.putAll(resources)
    inline fun <reified R : Resource> getResource(id: String) = resourceMap.g2(id) as R

    fun <T : State2> getReactiveState(id: Id2.StateId2) = reactiveStateMap.g2(id) as MutableState<T>

    override fun reactiveUpdate(state: State2) {
        if (reactiveStateMap[state.id] == null) {
            reactiveStateMap[state.id] = mutableStateOf(state)
        }
        getReactiveState<State2>(state.id).value = state
    }
    override fun reactivePush() {
        reactiveBackStack.add(backstack.last())
    }
    override fun reactivePop() {
        reactiveBackStack.apply {
            clear()
            addAll(backstack)
        }
    }

    companion object {
        fun create(projectName: String? = null) = ClientHolder2.add(projectName) { CommonClient2(projectName = it) }
    }
}