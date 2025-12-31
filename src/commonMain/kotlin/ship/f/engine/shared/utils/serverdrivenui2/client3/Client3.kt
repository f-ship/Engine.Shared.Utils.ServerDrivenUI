package ship.f.engine.shared.utils.serverdrivenui2.client3

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.compose.resources.Resource
import ship.f.engine.shared.utils.serverdrivenui2.client3.Path3.*
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Action2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.DeferredAction2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.RemoteAction2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.ResetState2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.MetaPublisherActionModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.StatePublisherActionModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.PopulatedSideEffectMeta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.Ref2.VmRef2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.Trigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.ext.defaultIfNull
import ship.f.engine.shared.utils.serverdrivenui2.ext.sduiLog
import ship.f.engine.shared.utils.serverdrivenui2.state.RefState2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

@Suppress("UNCHECKED_CAST")
open class Client3 {
    val viewModels: MutableMap<MetaId2, Meta2> = mutableMapOf()

    val idPaths: MutableMap<StateId2, List<Path3>> = mutableMapOf()
    val states: MutableMap<Path3, State2> = mutableMapOf()
    val reactiveStates: MutableMap<Path3, MutableState<State2>> = mutableMapOf()
    val stateQueue: MutableList<State2> = mutableListOf()

    val listeners: MutableMap<Id2, List<RemoteAction2<out Action2>>> = mutableMapOf()
    val deferredActions: MutableMap<String, List<RemoteAction2<DeferredAction2<out Action2>>>> = mutableMapOf()
    val firedActions: MutableMap<ActionId2, Action2> = mutableMapOf()

    val resources: MutableMap<String, Resource> = mutableMapOf()
    fun addResources(items: Map<String, Resource>) = resources.putAll(items)
    inline fun <reified T : Resource> getResource(id: String) =
        resources[id] as? T ?: error("Resource with id: $id not found")

    val vectors: MutableMap<String, ImageVector> = mutableMapOf()
    fun addVectors(items: Map<String, ImageVector>) = vectors.putAll(items)
    fun getImageVector(id: String) = vectors[id] ?: error("ImageVector with id: $id not found")


    val computationEngine = ComputationEngine(this)
    val navigationEngine = NavigationEngine(this)
    fun hasFired(action: Action2) = firedActions[action.id] != null
    fun addFired(action: Action2) {
        firedActions[action.id] = action
    }

    fun addDeferredAction(remoteAction: RemoteAction2<DeferredAction2<out Action2>>) {
        deferredActions.defaultIfNull(remoteAction.action.deferKey, listOf()) { it + remoteAction }
    }

    fun addRemoteAction(metaId: MetaId2, stateId: StateId2, action: Action2) {
        listeners.defaultIfNull(metaId, mutableListOf()) { it + RemoteAction2(action, stateId) }
    }

    fun clearDeferredActions(key: String?) = deferredActions.remove(key)
    fun getDeferredActions(key: String?) = deferredActions[key]

    var emitSideEffect: (PopulatedSideEffectMeta2) -> Unit = { }
    val commitScope = CoroutineScope(Dispatchers.Main)
    val queueMutex = Mutex()

    /**
     * By default, if a rootPath is not provided, the first path in the list will be returned.
     * If the rootPath is provided, the state that most local to the rootPath will be returned.
     * TODO to implement locality implementation
     */
    inline fun <reified T : State2> get(stateId2: StateId2, rootPath: Path3? = null): T {
        val paths = idPaths[stateId2] ?: sduiLog(idPaths.keys).let { null } ?: error("Paths has not been found for stateId: $stateId2")
        val path = paths.firstOrNull() ?: error("paths are empty for stateId: $stateId2")
        return states[path] as? T ?: sduiLog(list = states.keys, tag = "get > states").let { null }
        ?: error("get with stateId > no state exists for path: $path")
    }

    inline fun <reified T : State2> getOrNull(stateId2: StateId2, rootPath: Path3? = null): T? {
        val paths = idPaths[stateId2] ?: return null
        val path = paths.firstOrNull() ?: return null
        return states[path] as? T
    }

    inline fun <reified T : State2> getOrNull(path: Path3): T? = states[path] as? T
    inline fun <reified T : State2> get(path: Path3): T =
        getOrNull(path) ?: error("get > no state exists for path: $path")

    inline fun <reified T : State2> getReactiveOrNull(path: Path3): MutableState<T>? =
        reactiveStates[path] as? MutableState<T>

    inline fun <reified T : State2> getReactive(path: Path3): MutableState<T> =
        getReactiveOrNull(path) ?: error("no reactive state exists for path: $path")

    inline fun <reified T : Meta2> get(metaId2: MetaId2): T =
        viewModels[metaId2] as? T ?: sduiLog(viewModels.keys, tag = "ContentZoneModel").let{ null } ?: error("no meta exists for metaId: $metaId2")

    fun update(state: State2) {
        if (state !is RefState2) {
            states.defaultIfNull(state.path3, state) { state }
            commitScope.launch {
                queueMutex.withLock { stateQueue.add(state) }
            }
            propagate(state)
        }
    }

    fun update(viewModel: Meta2) {
        if (viewModels[viewModel.metaId] != null) sduiLog("Tried to Update ${viewModel.metaId} multiple times, ensure metaIds are unique")
        viewModels[viewModel.metaId] = viewModel
        propagate(viewModel)
    }

    fun propagate(state: State2) {
        listeners[state.id]?.forEach { listener ->
            listener.action.run3(
                state = get(listener.targetStateId),
                client = this,
            )
        }
    }

    fun propagate(viewModel: Meta2) {
        listeners[viewModel.metaId]?.forEach { listener ->
            listener.action.run3(
                state = get(listener.targetStateId),
                client = this,
            )
        }
    }

    fun commit() {
        commitScope.launch {
            try {
                val distinct = queueMutex.withLock {
                    val distinct = stateQueue.distinct()
                    stateQueue.clear()
                    distinct
                }

                distinct.forEach { state ->
                    // This is done to ensure parents that are already rendered can accept children on the main thread
                    if (reactiveStates[state.path3] == null) {
                        reactiveStates[state.path3] = mutableStateOf(state)
                        navigationEngine.checkNavigation(state.id)
                    }
                }
                distinct.forEach { state ->
                    reactiveStates.defaultIfNull(state.path3, mutableStateOf(state)) { it.also { it.value = state } }
                }
            } catch (e: Exception) {
                sduiLog("Commit error: ${e.message}", tag = "Client3 > commit > error")
                emitSideEffect(
                    PopulatedSideEffectMeta2(
                        metaId = MetaId2("%SDUIError%", "commit"),
                    )
                )
            }
        }
    }

    fun initState(
        state: State2,
        renderChain: List<StateId2> = listOf(),
        clearState: Boolean = true,
    ): State2 {
        return if (state.path3 !is Init && renderChain.isEmpty()) state
        else if (renderChain.isNotEmpty()) {
            resetPaths(state, clearState).run {
                buildPaths(this, renderChain).also {
                    setPaths(it)
                    setStates(it)
                }
            }
        } else buildPaths(state, renderChain).also {
            setPaths(it)
            setStates(it)
            setViewModels(it)
            setListeners(it)
            buildTrigger(it)
        }
    }

    private fun buildTrigger(state: State2) {
        (state as? OnBuildCompleteModifier2)?.onBuildCompleteTrigger2?.actions?.forEach {
            it.run3(state = state, client = this)
        }
        (state as? ChildrenModifier2<State2>)?.children?.forEach { buildTrigger(it) }
    }

    private fun resetPaths(state: State2, clearState: Boolean): State2 {
        if (!clearState) return state
        if (state.path3 !is Global) {
            val paths = idPaths[state.id] ?: listOf()
            idPaths[state.id] = paths.filterNot { it == state.path3 }
            states.remove(state.path3)
            reactiveStates.remove(state.path3)
        }

        (state as? ChildrenModifier2<State2>)?.run {
            children.forEach { child ->
                resetPaths(child, clearState)
            }
        }
        return state.c(path3 = Init)
    }

    /**
     * Initializes states with path information as is not currently done server side
     * TODO move this work to the server so that this information can be cached
     */
    fun buildPaths(state: State2, renderChain: List<StateId2> = listOf()): State2 {
        var updatedRenderChain = renderChain
        val stateWithPath = with(state) {
            if (id.isAutoGenerated) {
                c(path3 = Anon)
            } else if (id.isGlobal) {
                c(Global(id))
            } else {
                updatedRenderChain = renderChain + id
                c(Local(updatedRenderChain))
            }
        }

        return (stateWithPath as? ChildrenModifier2<State2>)?.run {
            c(children = children.map { child -> buildPaths(child, updatedRenderChain) })
        } ?: stateWithPath
    }

    /**
     * Path -> should never be Init here
     * Anon -> don't do anything
     * Local -> Allow a list of paths to be added, local paths should generally be unique
     * Global -> Ensure that global paths are unique by only setting the last state
     */
    fun setPaths(state: State2) {
        when (state.path3) {
            is Init -> error("This shouldn't have happened, trying to setPath on an $state which is Init")
            is Anon -> Unit
            is Local -> idPaths.defaultIfNull(state.id, listOf()) { it + state.path3 }
            is Global -> if (state !is RefState2) {
                idPaths.defaultIfNull(state.id, listOf()) { listOf(state.path3) }
            }
        }

        (state as? ChildrenModifier2<State2>)?.run {
            children.forEach { child -> setPaths(child) }
        }
    }

    fun setViewModels(state: State2) {
        setListeners(state)
        state.metas.forEach { meta ->
            if (viewModels[meta.metaId] != null) sduiLog("Tried to set ${meta.metaId} multiple times, ensure metaIds are unique")
            viewModels[meta.metaId] = meta
            propagate(meta)
        }
        (state as? ChildrenModifier2<State2>)?.run {
            children.forEach { child -> setViewModels(child) }
        }
    }

    // TODO consider using path instead of state to make functionality more explicit for system calls
    private fun setListeners(state: State2) {
        val triggers = mutableListOf<Trigger2>()
        when (state) {
            is OnClickModifier2 -> triggers.add(state.onClickTrigger)
            is OnFieldUpdateModifier2 -> triggers.add(state.onFieldUpdateTrigger)
            is OnMetaUpdateModifier2 -> triggers.add(state.onMetaUpdateTrigger)
            is OnStateUpdateModifier2 -> triggers.add(state.onStateUpdateTrigger)
            is OnToggleModifier2<out State2> -> triggers.add(state.onToggleTrigger)
            else -> Unit
        }

        val publisherStateActions = triggers.flatMap { it.actions }.filterIsInstance<StatePublisherActionModifier2>()
        publisherStateActions.forEach { action ->
            action.publishers.forEach { publisher ->
                listeners.defaultIfNull(publisher, listOf()) {
                    it + RemoteAction2(
                        action = action as Action2,
                        targetStateId = state.id
                    )
                }
            }
        }

        val publisherMetaActions = triggers.flatMap { it.actions }.filterIsInstance<MetaPublisherActionModifier2>()
        publisherMetaActions.forEach { action ->
            action.publishers.forEach { publisher ->
                listeners.defaultIfNull(publisher, listOf()) {
                    it + RemoteAction2(
                        action = action as Action2,
                        targetStateId = state.id
                    )
                }
            }
        }

        (state as? ChildrenModifier2<State2>)?.run {
            filter?.let { filterConfig ->
                val vmRefs = mutableListOf<VmRef2>()
                filterConfig.forEach { filter ->
                    if (filter.value1 is LiveValue2.MultiLiveValue2) vmRefs.add(filter.value1.ref)
                    if (filter.value2 is LiveValue2.MultiLiveValue2) vmRefs.add(filter.value2.ref)
                }
                vmRefs.distinct().forEach { vmRef ->
                    listeners.defaultIfNull(vmRef.vm, listOf()) {
                        it + RemoteAction2(
                            action = ResetState2(state.id),
                            targetStateId = state.id
                        )
                    }
                }
            }
        }
    }

    private fun setStates(state: State2) {
        when (state.path3) {
            is Init -> error("Should not be calling this on a state that has not been initialised yet")
            is Anon -> Unit // No need to set here
            is Local, is Global -> update(state) // We are not committing yet to avoid screen jank
        }
        (state as? ChildrenModifier2<State2>)?.run {
            children.forEach { child -> setStates(child) }
        }
    }

    companion object {
        val client3 = Client3()
    }
}