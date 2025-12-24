package ship.f.engine.shared.utils.serverdrivenui2.client

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.Resource
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Path2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.ext.g2
import ship.f.engine.shared.utils.serverdrivenui2.ext.sduiLog
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

@Suppress("UNCHECKED_CAST")
open class CommonClient2 protected constructor(override val projectName: String? = null) : Client2() {

    val reactiveStateMap: MutableMap<Id2, MutableState<State2>> = mutableMapOf()
    val reactiveIdPathsMap: MutableMap<Path2, MutableState<State2>> = mutableMapOf()

    val reactiveChildMap: MutableMap<Path2, MutableState<List<State2>>> = mutableMapOf()

    val reactiveBackStack: SnapshotStateList<BackStackEntry2> = mutableStateListOf()
    val reactiveBackStackMap: MutableMap<Id2.StateId2, SnapshotStateList<BackStackEntry2>> = mutableMapOf()

    val resourceMap: MutableMap<String, Resource> = mutableMapOf()
    val imageVectorMap: MutableMap<String, ImageVector> = mutableMapOf()
    fun addResource(id: String, resource: Resource) = resourceMap.put(id, resource)
    fun addResources(resources: Map<String, Resource>) = resourceMap.putAll(resources)
    fun addVector(id: String, resource: ImageVector) = imageVectorMap.put(id, resource)
    fun addVectors(resources: Map<String, ImageVector>) = imageVectorMap.putAll(resources)
    inline fun <reified R : Resource> getResource(id: String) = resourceMap.g2(id) as R
    fun getImageVector(id: String) = imageVectorMap.g2(id)

    // TODO Planned, to be removed
    fun <T : State2> getReactiveState(id: Id2.StateId2) = reactiveStateMap.g2(id) as MutableState<T>
    // TODO Planned
    fun <T : State2> getReactivePathState(path: Path2, s: State2): MutableState<T> {
        val ms = reactiveIdPathsMap[path] ?: error("Path not found: $path with state: $s \nwith keys ${reactiveIdPathsMap.keys}")
        return ms as MutableState<T>
    }

    // TODO Planned
    fun <T : State2> getReactivePathStateOrNull(path: Path2, s: State2): MutableState<T>? {
        val ms = reactiveIdPathsMap[path]
        return ms as? MutableState<T>
    }

    // TODO Planned
    override fun reactiveUpdate(state: State2) {
        if (reactiveIdPathsMap[state.path] == null) {
            reactiveIdPathsMap[state.path] = mutableStateOf(state)
        }
//        if (state.id.name == "Bottom-Nav-Agenda") println("Debugging State $state")
//        println("Attempting to trigger reactive update for ${state.id} with path ${state.path}")

        getReactivePathState<State2>(state.path, state).value = state

//        if (reactiveStateMap[state.id] == null) {
//            reactiveStateMap[state.id] = mutableStateOf(state)
//        }
//        getReactiveState<State2>(state.id).value = state
    }

    // TODO Planned
    override fun reactivePush() { // TODO do I need to change this as well? Unlikely
        reactiveBackStack.add(backstack.last())
    }

    // TODO Planned, to be removed
    override fun reactivePush(stateId: Id2.StateId2) {
        reactiveBackStackMap.getOrPut(stateId) { mutableStateListOf() }.add(containerBackStack[stateId]!!.last())
    }

    // TODO Planned
    override fun reactivePop() { // TODO do I need to change this as well? Unlikely
        reactiveBackStack.apply {
            clear()
            addAll(backstack)
        }
    }

    // TODO Planned, to be removed
    override fun reactivePop(stateId: Id2.StateId2) {
        reactiveBackStackMap.getOrPut(stateId) { mutableStateListOf() }.apply {
            clear()
            addAll(containerBackStack[stateId]!!)
        }
    }

    // TODO Planned, to be removed
    override fun reactiveUpdateChildren(path: Path2) {
        if (reactiveChildMap[path] == null) reactiveChildMap[path] = mutableStateOf(listOf())
        (reactiveChildMap[path] ?: error("Oh no bro")).value = childrenMap[path] ?: error("Oh no, no children")
    }

    // TODO Planned, to be removed
    companion object {
        fun create(projectName: String? = null) = ClientHolder2.add(projectName) { CommonClient2(projectName = it) }
    }
}