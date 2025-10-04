package ship.f.engine.shared.utils.serverdrivenui2.client

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.Resource
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.ext.g2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

@Suppress("UNCHECKED_CAST")
open class CommonClient2 protected constructor(override val projectName: String? = null) : Client2() {

    val reactiveStateMap: MutableMap<Id2, MutableState<State2>> = mutableMapOf()
    val reactiveBackStack: SnapshotStateList<BackStackEntry2> = mutableStateListOf()

    val resourceMap: MutableMap<String, Resource> = mutableMapOf()
    val imageVectorMap: MutableMap<String, ImageVector> = mutableMapOf()
    fun addResource(id: String, resource: Resource) = resourceMap.put(id, resource)
    fun addResources(resources: Map<String, Resource>) = resourceMap.putAll(resources)
    fun addVector(id: String, resource: ImageVector) = imageVectorMap.put(id, resource)
    fun addVectors(resources: Map<String, ImageVector>) = imageVectorMap.putAll(resources)
    inline fun <reified R : Resource> getResource(id: String) = resourceMap.g2(id) as R
    fun getImageVector(id: String) = imageVectorMap.g2(id)

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