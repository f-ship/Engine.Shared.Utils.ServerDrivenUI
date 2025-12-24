package ship.f.engine.shared.utils.serverdrivenui2.client

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client.BackStackEntry2.Direction2.Backward2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.MetaPublisherActionModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.StatePublisherActionModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.InsertionOperation2.*
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.PopulatedSideEffectMeta2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.ZoneViewModel2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.ZoneViewModel2.Property.StringProperty
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.ConditionalLiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.Ref2.VmRef2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.Ref2.ZoneRef2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Path2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.Trigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.ext.g2
import ship.f.engine.shared.utils.serverdrivenui2.ext.sduiLog
import ship.f.engine.shared.utils.serverdrivenui2.state.BoxState2
import ship.f.engine.shared.utils.serverdrivenui2.state.ScreenState2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2
import ship.f.engine.shared.utils.serverdrivenui2.state.TextState2

abstract class Client2(open val projectName: String? = null) {
    val stateMap: MutableMap<StateId2, State2> = mutableMapOf()
    val idPathsMap: MutableMap<StateId2, List<Path2>> = mutableMapOf()
    val pathStateMap: MutableMap<Path2, State2> = mutableMapOf()
    val childrenMap: MutableMap<Path2, List<State2>> = mutableMapOf()

    val metaMap: MutableMap<Id2, Meta2> = mutableMapOf()

    val stateListeners2: MutableMap<StateId2, MutableList<RemoteAction2<*>>> = mutableMapOf()

    val metaListeners2: MutableMap<MetaId2, MutableList<RemoteAction2<*>>> = mutableMapOf()
    val deferredActionMap: MutableMap<String, List<RemoteAction2<DeferredAction2<*>>>> = mutableMapOf()

    val firedActionMap: MutableMap<ActionId2, Action2> = mutableMapOf()

    val reverseIdMap: MutableMap<StateId2, StateId2> = mutableMapOf()
    val backstack: MutableList<BackStackEntry2> = mutableListOf()
    val containerBackStack: MutableMap<StateId2, MutableList<BackStackEntry2>> = mutableMapOf()

    val cheapBackStack: MutableList<CheapBackStackEntry> = mutableListOf()

    // TODO Planned
    // TODO only currently in use for statemachine
    @Serializable
    @SerialName("CheapBackStackEntry")
    data class CheapBackStackEntry(
        val selected: List<String>,
        val targetMetaId: MetaId2,
    )

    // TODO Planned
    fun computeLiveText(liveValue: LiveValue2.TextLiveValue2) = when (liveValue.ref) {
        is LiveValue2.Ref2.StateRef2 -> {
            val paths = idPathsMap[liveValue.ref.id] ?: error("No paths found for id: ${liveValue.ref.id}")
            val path = paths.firstOrNull() ?: error("Paths were empty for id: ${liveValue.ref.id}")
            val state = get<State2>(path)
            (state as? TextState2)?.text ?: error("Not a text state ${liveValue.ref.id}")
        }

        is VmRef2 -> {
            val vm = metaMap[liveValue.ref.vm] as? ZoneViewModel2 ?: error("No vm found for id: ${liveValue.ref.vm}")
            (vm.map[liveValue.ref.property] as? StringProperty)?.value
                ?: error("No value found for ref: ${liveValue.ref} in ${liveValue.ref.vm} for ${liveValue.ref.property}")
        }
        else -> TODO()
    }

    // TODO Planned
    fun computeConditionalLive(liveValue2: ConditionalLiveValue2): Boolean {
        return when(liveValue2.value1){
            is LiveValue2.TextLiveValue2 -> when(liveValue2.value2){
                is LiveValue2.MultiLiveValue2 -> {
                    val vm = get(liveValue2.value2.ref.vm) as? ZoneViewModel2 ?: error("No vm found for id: ${liveValue2.value2.ref.vm}")
                    val multiProperty = vm.map[liveValue2.value2.ref.property] as? ZoneViewModel2.Property.MultiProperty ?: error("No multi property found for ref: ${liveValue2.value2.ref} in ${liveValue2.value2.ref.vm}")
                    val prop = when(liveValue2.value1.ref) {
                        is ZoneRef2 -> StringProperty(
                            value = (metaMap[liveValue2.value1.ref.vm] as? ZoneViewModel2)
                                ?.let { it.map[liveValue2.value1.ref.property] as? StringProperty }
                                ?.value ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for ${liveValue2.value1.ref.property}"),
                        )
                        else -> TODO()
                    }
                    when(liveValue2.condition){
                        is LiveValue2.Condition2.InOrEmpty -> multiProperty.value.isEmpty() || multiProperty.value.contains(prop)
                        else -> TODO()
                    }
                }
                else -> TODO("Only handling TextLiveValue2 > MultiLiveValue2 for now")
            }
            is LiveValue2.IntLiveValue2 -> when(liveValue2.value2){
                is LiveValue2.IntLiveValue2 -> {
                    val value1 = when(val ref = liveValue2.value1.ref) {
                        is ZoneRef2 -> ((get(ref.vm) as? ZoneViewModel2 ?: error("no vm ${ref.vm}"))
                            .map[ref.property] as? ZoneViewModel2.Property.IntProperty)?.value
                        else -> TODO()
                    }

                    val value2 = when(val ref = liveValue2.value2.ref) {
                        is ZoneRef2 -> ((get(ref.vm) as? ZoneViewModel2 ?: error("no vm ${ref.vm}"))
                            .map[ref.property] as? ZoneViewModel2.Property.IntProperty)?.value
                        else -> TODO()
                    }

                    when(liveValue2.condition){
                        is LiveValue2.Condition2.GreaterThan -> value1!! > value2!!
                        is LiveValue2.Condition2.LessThan -> value1!! < value2!!
                        else -> TODO()
                    }
                }
                is LiveValue2.InstantNowLiveValue2 -> {
                    val value1 = when(val ref = liveValue2.value1.ref) {
                        is ZoneRef2 -> ((get(ref.vm) as? ZoneViewModel2 ?: error("no vm ${ref.vm}"))
                            .map[ref.property] as? ZoneViewModel2.Property.IntProperty)?.value
                        else -> TODO()
                    }

                    val value2 = Clock.System.now().epochSeconds.toInt() // TODO not converting to Int causing issues? Not doesn't seem to be the issue

                    when(liveValue2.condition) {
                        is LiveValue2.Condition2.GreaterThan -> value1!! > value2
                        is LiveValue2.Condition2.LessThan -> value1!! < value2
                        else -> TODO()
                    }
                }
                else -> TODO()
            }
            else -> TODO()
        }
    }

    // TODO Planned
    inline fun <reified T: LiveValue2> computeConditionalBranchLive(liveValue2: LiveValue2.ConditionalBranchLiveValue2): T {
        return when(liveValue2.value1){
            is LiveValue2.IntLiveValue2 -> when(liveValue2.value2){
                is LiveValue2.IntLiveValue2 -> {
                    val prop1 = when(liveValue2.value1.ref) {
                        is ZoneRef2 -> (metaMap[liveValue2.value1.ref.vm] as? ZoneViewModel2)?.let {
                            it.map[liveValue2.value1.ref.property] ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for ${liveValue2.value1.ref.property}")
                        } ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for ${liveValue2.value1.ref.property}")
                        else -> TODO()
                    }

                    val prop2 = when(liveValue2.value2.ref) {
                        is ZoneRef2 -> (metaMap[liveValue2.value2.ref.vm] as? ZoneViewModel2)?.let {
                            it.map[liveValue2.value2.ref.property] ?: error("No value found for ref: ${liveValue2.value2.ref} in ${liveValue2.value2.ref.vm} for1 ${liveValue2.value2.ref.property}")
                        } ?: error("No value found for ref: ${liveValue2.value2.ref} in ${liveValue2.value2.ref.vm} for ${liveValue2.value2.ref.property}")
                        else -> TODO()
                    }

                    val bool = when(liveValue2.condition){
                        LiveValue2.Condition2.Eq -> prop1 == prop2
                        else -> TODO()
                    }

                    if (bool){
                        liveValue2.trueBranch as T
                    } else {
                        liveValue2.falseBranch as T
                    }
                }
                is LiveValue2.StaticIntLiveValue2 -> {
                    val prop1 = when(liveValue2.value1.ref) {
                        is ZoneRef2 -> (metaMap[liveValue2.value1.ref.vm] as? ZoneViewModel2)?.let {
                            it.map[liveValue2.value1.ref.property] ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for2 ${liveValue2.value1.ref.property}")
                        } ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for3 ${liveValue2.value1.ref.property}")
                        else -> TODO()
                    }

                    val bool = when(liveValue2.condition){
                        LiveValue2.Condition2.Eq -> when(prop1){
                            is ZoneViewModel2.Property.IntProperty -> prop1.value == liveValue2.value2.value
                            else -> false
                        }

                        LiveValue2.Condition2.Mod -> when(prop1){
                            is ZoneViewModel2.Property.IntProperty -> (prop1.value % liveValue2.value2.value) == 0
                            else -> false
                        }
                        else -> TODO()
                    }

                    if (bool){
                        liveValue2.trueBranch as T
                    } else {
                        liveValue2.falseBranch as T
                    }
                }
                else -> TODO()
            }
            else -> TODO()
        }
    }

    // TODO Deferred, working fine
    fun hasFired(action: Action2) = firedActionMap[action.id] != null

    // TODO Deferred, working fine
    fun addFired(action: Action2) {
        firedActionMap[action.id] = action
    }

    // TODO Deferred, working fine
    fun addDeferredAction(remoteAction: RemoteAction2<DeferredAction2<*>>) {
        if (deferredActionMap[remoteAction.action.deferKey] == null) {
            deferredActionMap[remoteAction.action.deferKey] = listOf()
        }
        deferredActionMap[remoteAction.action.deferKey] =
            deferredActionMap.g2(remoteAction.action.deferKey) + listOf(remoteAction)
    }

    // TODO Deferred, working fine
    fun addRemoteAction(metaId: MetaId2, stateId: StateId2, action: Action2) {
        if (metaListeners2[metaId] == null) {
            metaListeners2[metaId] = mutableListOf()
        }

        metaListeners2[metaId]?.add(
            RemoteAction2(
                action = action,
                targetStateId = stateId,
            )
        )
    }

    // TODO Deferred, working fine
    fun clearDeferredActions(key: String?) = deferredActionMap.remove(key)

    // TODO Deferred, working fine
    fun getDeferredActions(key: String?) = deferredActionMap[key]

    // TODO Planned, will be removed
    fun updateChildren(path: Path2, children: List<State2>, reason: String) {
        println("updating children for path: $path, reason: $reason")
        println("--------------------------------------------------------")
        childrenMap[path] = children
        reactiveUpdateChildren(path)
    }

//    inline fun <reified T : State2> get(stateId2: StateId2): T = stateMap.g2(stateId2) as T

    // TODO Planned
    inline fun <reified T : State2> get(stateId2: StateId2): T { // TODO temporarily used to return first instance, will be upgraded to return list
        val paths = idPathsMap[stateId2] ?: error("Paths has not been found for stateId: $stateId2")
        val path = paths.maxByOrNull { it.path.size }
            ?: error("no paths exist for stateId: $stateId2") // TODO small modification to prefer returning rooted paths
        return pathStateMap[path] as? T ?: error("no state exists for path: $path")
    }

    // TODO Planned
    inline fun <reified T : State2> get(path: Path2): T = pathStateMap[path] as T

    // TODO Planned
    inline fun <reified T : State2> getOrNull(path: Path2): T? = pathStateMap[path] as? T

    // TODO Planned, improve to support casting
    fun get(metaId2: MetaId2): Meta2 = metaMap.g2(metaId2)

    // TODO Planned
    fun update(
        state: State2,
        renderChain: List<StateId2> = listOf(),
        insertIntoParent: Boolean = false
    ): State2 { //TODO probably need to find a better way of dynamic tree building in case a view isn't found as a fallback
        var currentState = state
        if (currentState.path.path.isEmpty() || renderChain.isNotEmpty()) {
            currentState = buildPaths(currentState, renderChain)
            setPaths(currentState)
            setViewModels(currentState)
            setFilters(currentState)
            setReactivity(currentState)
        }

        //Ensure the current state itself is correctly updated
        if (!state.id.isAutoGenerated) {
            propagate(currentState)
            pathStateMap[currentState.path] = currentState // TODO may not be what I want
            reactiveUpdate(currentState)
        }

        //Use insertIntoParent to determine if we should do a reactive update to the parent of this state
        //First get parent by using the render chain
        //This is working, but not exactly sure how
        if (insertIntoParent) {
            val parentId = renderChain.last()
            val parent = get<State2>(parentId)
            val updatedParent =
                (parent as ChildrenModifier2<*>).c(children = parent.children.map { if (it.id == state.id) currentState else it })
            pathStateMap[updatedParent.path] = updatedParent
            val parentPaths = idPathsMap[updatedParent.id] ?: error("Seriously")
            idPathsMap[updatedParent.id] =
                if (parentPaths.contains(updatedParent.path)) parentPaths else parentPaths + updatedParent.path
            reactiveUpdate(updatedParent)
        }

        (currentState as? OnBuildCompleteModifier2)?.onBuildCompleteTrigger2?.actions?.forEach {
            // TODO in the future I may need to be careful this only get's triggered once
            // TODO this should be handled during the refactor when we introduce the concept of rooted states vs stranded states
            println("Running onBuildCompleteTrigger2 action")
            it.run(currentState, this)
        }

        return currentState
    }

    // TODO Planned
    fun buildPaths(state: State2, renderChain: List<StateId2> = listOf()): State2 {
        var currentState = state
        val currentPath = if (state.id.isAutoGenerated) renderChain else renderChain + state.id
        if (!state.id.isAutoGenerated) {
            currentState = currentState.c(path = Path2(currentPath))
        }
        (currentState as? ChildrenModifier2<*>)?.let {
            currentState = currentState.c(
                children = it.children.map { child -> buildPaths(child, currentPath) }
            )
        }
        return currentState
    }

    // TODO Planned
    fun setPaths(state: State2) {
        if (!state.id.isAutoGenerated) {
            pathStateMap[state.path] = state
            if (idPathsMap[state.id] == null) idPathsMap[state.id] = listOf()
            if (!idPathsMap[state.id]!!.contains(state.path)) idPathsMap[state.id] =
                idPathsMap[state.id]!! + Path2(state.path.path)
        }
        (state as? ChildrenModifier2<*>)?.let {
            // Add to the children map if not autogenerated
            if (!state.id.isAutoGenerated) updateChildren(state.path, it.children, "Happening inside SetPaths")
            it.children.forEach { child -> setPaths(child) }
        }
    }

    // TODO Planned
    fun setFilters(state: State2) {
        (state as? ChildrenModifier2<*>)?.let {
            state.filter?.let { filterConfig ->
                val vmRefs = mutableListOf<VmRef2>()
                filterConfig.forEach { filter ->
                    if (filter.value1 is LiveValue2.MultiLiveValue2) vmRefs.add(filter.value1.ref)
                    if (filter.value2 is LiveValue2.MultiLiveValue2) vmRefs.add(filter.value2.ref)
                }

                vmRefs.distinct().forEach { vmRef ->
                    if (metaListeners2[vmRef.vm] == null) metaListeners2[vmRef.vm] = mutableListOf()
                    metaListeners2[vmRef.vm]?.add(
                        RemoteAction2(
                            action = ResetState2(state.id),
                            targetStateId = state.id,
                        )
                    )
                }
            }
            it.children.forEach { child -> setFilters(child) }
        }
    }

    // TODO Planned
    fun setViewModels(state: State2) {
        setTriggers(state)
        state.metas.forEach { meta -> metaMap[meta.metaId] = meta }
        (state as? ChildrenModifier2<*>)?.let {
            it.children.map { child -> setViewModels(child) }
        }
    }

    // TODO Planned
    fun setReactivity(state: State2) {
        if (!state.id.isAutoGenerated) reactiveUpdate(state)
        (state as? ChildrenModifier2<*>)?.let {
            it.children.map { child -> setReactivity(child) }
        }
    }

    // TODO Planned, to be removed of course
    fun update2(
        state: State2,
        forceUpdate: Boolean = false,
        renderChain: List<StateId2> = listOf()
    ): State2 { //TODO probably need to find a better way of dynamic tree building incase a view isn't found as a fallback
        var currentState = state

        // TODO this is code is intended to stop generating a lot of repetitive states that are static
        // TODO this optimisation only works if the state is already connected to the tree, hence the issue
        // TODO We are going to depreciate swap because it causes too many issues
        // TODO if we encourage having duplicate IDs it makes it harder to spot instances where this was an accident
        // TODO this can either be an accident because an ID was hardcoded even though it needs to by dynamic
        // TODO or even harder to find, the dynamic part of the id comes back null effectively making the id static
//        if (state.id.isAutoGenerated){
//            val parentId = reverseIdMap[state.id]
//            if (parentId != null) {
//                val index = (get<State2>(parentId) as? ChildrenModifier2<*>)?.children?.indexOf(state)
//                if (index != null) {
//                    currentState = state.c(id = StateId2("${parentId.name}-$index-${state::class.simpleName}"))
//                    reverseIdMap[currentState.id] = parentId
//                }
//            }
//        }

//        if (!currentState.id.isAutoGenerated) tempStateList.add(currentState.id).also { println(tempStateList) }

        //TODO new paths logic setup
        //What happens with items that are currently floating?
        //Well floating items cannot exist in the rendering Queue, it is up to the navigation to correct the path
        //This sort of means that a floating state can cleanly be cloned into several different parents without there being an issue
        //Changes to one clone will also propagate to all clones
        val currentRenderChain = if (currentState.id.isAutoGenerated) renderChain else renderChain + currentState.id
        val currentPath = Path2(currentRenderChain)
        if (!currentState.id.isAutoGenerated) currentState = currentState.c(path = currentPath)

        println("currentPath: $currentPath stateId: ${currentState.id}")

        //TODO new path binding logic
        //What happens with items that are currently floating?
        //TODO we are currently adding too many identical items to paths, we can trim this down
        if (pathStateMap[currentPath] == null || forceUpdate || currentState.id.isAutoGenerated) {
            (currentState as? ChildrenModifier2<*>)?.run {
                val updatedChildren = children.map { child ->
                    update2(child, forceUpdate, currentRenderChain)
                }
                currentState = currentState.c(updatedChildren) // TODO will probably be removed
                if (!currentState.id.isAutoGenerated) childrenMap[currentPath] = updatedChildren
            }
            if (!state.id.isAutoGenerated) {
                pathStateMap[currentPath] = currentState
                if (idPathsMap[currentState.id] == null) idPathsMap[currentState.id] = listOf()
                idPathsMap[currentState.id] = idPathsMap[currentState.id]!! + listOf(currentPath)
            }
            setTriggers(currentState) // TODO this should probably be moved into block above
            currentState.metas.forEach { meta -> metaMap[meta.metaId] = meta }
        } else {
            if (!state.id.isAutoGenerated) {
                pathStateMap[currentPath] = currentState
                if (idPathsMap[currentState.id] == null) idPathsMap[currentState.id] = listOf()
                idPathsMap[currentState.id] = idPathsMap[currentState.id]!! + listOf(currentPath)
            }
        }

        if (!state.id.isAutoGenerated) propagate(currentState)
        if (!state.id.isAutoGenerated) reactiveUpdate(currentState)

//        //TODO old state binding logic
//        if (stateMap[currentState.id] == null || forceUpdate) {
//            if (!state.id.isAutoGenerated && !state.id.ignoreReactive) stateMap[currentState.id] = currentState // TODO moved this to above the logic for the children
//            setTriggers(currentState)
//            (currentState as? ChildrenModifier2<*>)?.children?.forEach { child ->
//                reverseIdMap[child.id] = currentState.id
//                update(child, forceUpdate, currentRenderChain)
//            }
//            currentState.metas.forEach { meta -> metaMap[meta.metaId] = meta }
//        } else {
//            if (!state.id.isAutoGenerated && !state.id.ignoreReactive) stateMap[currentState.id] = currentState // TODO moved this to above the logic for the children
//        }
//
//        //TODO old propagation logic
//        if (!state.id.isAutoGenerated && !state.id.ignoreReactive) propagate(currentState)
//        if (!state.id.isAutoGenerated && !state.id.ignoreReactive) reactiveUpdate(currentState)
//        if (state.id.ignoreReactive) nonReactiveStateMap[state.id] = currentState
        return currentState
    }

    // TODO Planned
    fun update(meta: Meta2) {
        metaMap[meta.metaId] = meta
        propagate(meta)
    }

    // TODO Planned
    fun propagate(state: State2) {
        stateListeners2[state.id]?.forEach { listener ->
            listener.action.run(
                state = get(listener.targetStateId),
                client = this,
            )
        }
    }

    // TODO Planned
    fun propagate(meta: Meta2) {
        metaListeners2[meta.metaId]?.forEach { it ->
            it.action.run(
                state = get(it.targetStateId),
                client = this
            )
        }
    }

    // TODO Planned, to be extracted
    fun navigate(config: NavigationConfig2) {
        when (val op = config.operation) {
            is NavigationConfig2.StateOperation2.InsertionOperation2 -> {
                val paths = idPathsMap[op.inside]
                    ?: error("During insertion operation, no paths found in idPathsMap ${op.inside}")

                val curatedPaths = paths.filter { path ->
                    val rootId = path.path.firstOrNull()
                    val possibleScreen = rootId?.let { getOrNull<State2>(path.copy(path = listOf(it))) }
                    (possibleScreen is ScreenState2)
                }.run {
                    ifEmpty {
                        sduiLog("No curated paths found for insertion operation: ${op.inside}", "Insertion Operation")
                        paths
                    }
                }

                curatedPaths.forEach { path ->
                    val parent = get<State2>(path) as? ChildrenModifier2<*>
                        ?: error("During insertion operation, parent was not of type ChildrenModifier2<*> ${op.inside}")

                    val child = get<State2>(op.stateId)
                    val updatedChild = update(child, (parent as State2).path.path) // TODO I might regret this

                    val children = childrenMap[path] ?: parent.children

                    val u = when (op) {
                        is Start2 -> parent.c(listOf(updatedChild) + children)
                        is End2 -> parent.c(children + updatedChild)
                        is Before2 -> parent.c(insert(children, updatedChild, op.before, op.offset))
                        is After2 -> parent.c(insert(children, updatedChild, op.after, op.offset))
                        is Remove2 -> parent.c(children.filter { child -> child.id != op.stateId })
                    }

                    updateChildren(
                        u.path,
                        (u as ChildrenModifier2<*>).children,
                        "Happening inside Insertion"
                    ) // TODO added update children
                    reactiveUpdate(u) // TODO replace with normal update
                }
                return
//                val path = paths.firstOrNull()
//                    ?: error("During insertion operation, paths were empty ${op.inside}")
//
//                val parentId = path.path.getOrNull(path.path.lastIndex)
//                    ?: error("During insertion operation, last element in path was null ${op.inside}")
//
//                val parent = parentId.let { (get<State2>(it) as? ChildrenModifier2<*>) }
//                    ?: error("During insertion operation, parent was not of type ChildrenModifier2<*> ${op.inside}")
//
//                val child = get<State2>(op.stateId)
//
//                val u = when (op) {
//                    is Start2 -> parent.c(listOf(child) + parent.children)
//                    is End2 -> parent.c(parent.children + child)
//                    is Before2 -> parent.c(insert(parent.children, child, op.before, op.offset))
//                    is After2 -> parent.c(insert(parent.children, child, op.after, op.offset))
//                    is Remove2 -> parent.c(parent.children.filter { child -> child.id != op.stateId })
//                }
//
//                update(u, u.path.path)
//                return
            }

            is NavigationConfig2.StateOperation2.Replace2 -> (get<State2>(reverseIdMap.g2(op.replace)) as? ChildrenModifier2<*>)?.let {
                it.c(it.children.map { child -> if (child.id == op.replace) get<State2>(op.stateId) else child })
            }

            is NavigationConfig2.StateOperation2.ReplaceChild2 -> {
                val paths = idPathsMap[op.container]
                    ?: error("During replaceChild operation, no paths found in idPathsMap ${op.container}")

                paths.forEach { path ->
//                    val parentId = path.path.getOrNull(path.path.lastIndex)
//                        ?: error("During replaceChild operation, last element in path was null ${op.container}")
//
//                    val parent = parentId.let { (get<State2>(it) as? ChildrenModifier2<*>) }
//                        ?: error("During replaceChild operation, parent was not of type ChildrenModifier2<*> ${op.container}")

                    val parent = get<State2>(path) as? ChildrenModifier2<*>
                        ?: error("During replaceChild operation, parent was not of type ChildrenModifier2<*> ${op.container}")

                    val childFromPath = getOrNull<State2>(path.copy(path.path + listOf(op.stateId)))

                    if (childFromPath != null) {
                        val c = listOf(childFromPath)
                        val u = parent.c(c)
                        updateChildren(u.path, c, "Happening inside Shortcut")
                        reactiveUpdate(u)
                        return@forEach
                    }

//                    println("optimisticChild: ${childFromPath?.id} with path ${childFromPath?.path}")
                    val child = get<State2>(op.stateId)
                    val upChild = update(
                        child,
                        path.path
                    ) //TODO Hopefully this will get things hooked up properly, I think this foolishness caused the bug, because of the render chain

                    val children = listOf(upChild)
                    val u = parent.c(children)
//                    println("attempting to update ${op.stateId}")
//                    println("attempting to update parent ${u.id}")
//                    println("${u.path}")
//                    println("$path")
//                    println("------------------------------------------")
                    updateChildren(path, children, "Happening inside replaceChild")

//                    childrenMap[path] = children
                    update(u)
                    // TODO solution works, but has potato performance
//                    for (i in path.path.indices) {
//                        val parentPath = path.copy(path.path.subList(0, path.path.size - i))
//                        println("working on current path $parentPath on $path")
//                        val parent = get<State2>(parentPath) as? ChildrenModifier2<*>
//                            ?: error("During recursive up-chain operation, parent was not of type ChildrenModifier2<*> $parentPath")
//                        val newParent = parent.c(
//                            parent.children.map { child ->
//                                if (child.id == newU.id) newU else child
//                            }
//                        )
//                        newU = update(newParent, parentPath.path)
//                    }
                }
                return

//                val path = paths.firstOrNull()
//                    ?: error("During replaceChild operation, paths were empty ${op.container}")
//
//                val parentId = path.path.getOrNull(path.path.lastIndex)
//                    ?: error("During replaceChild operation, last element in path was null ${op.container}")
//
//                val parent = parentId.let { (get<State2>(it) as? ChildrenModifier2<*>) }
//                    ?: error("During replaceChild operation, parent was not of type ChildrenModifier2<*> ${op.container}")
//
//                val child = get<State2>(op.stateId)
//                println("Replacing child ${child.id}")
//                println("With parent $parentId")
//                println("Add to backstack? ${op.addToBackStack}")

                // TODO this doesn't work and need an internal way to handle back navigation
//                if (op.addToBackStack) {
//                    if (containerBackStack[op.container] == null) containerBackStack[op.container] = mutableListOf()
//                    containerBackStack[op.container]!!.add(
//                        BackStackEntry2(
//                            direction = BackStackEntry2.Direction2.Forward2,
//                            state = child,
//                        )
//                    )
//                    reactivePush(op.container)
//                }

//                val u = parent.c(listOf(child))
//                update(u, u.path.path)
//                return
            }

            is NavigationConfig2.StateOperation2.Push2 -> {
                push(get<State2>(op.stateId))
                reactivePush()
                return
            }

            is NavigationConfig2.StateOperation2.Swap2 -> { // TODO swap relies on there being something to swap with, otherwise things breakdown
                val paths = idPathsMap[op.swap.id]
                    ?: error("During swap operation, no paths found in idPathsMap ${op.swap.id}")

                paths.forEach { path ->
                    if (path.path.size < 2) return@forEach

                    val parentPath = path.copy(path.path.subList(0, path.path.lastIndex))
                    val parent = get<State2>(parentPath) as? ChildrenModifier2<*>
                        ?: error("During replaceChild operation, parent was not of type ChildrenModifier2<*> $parentPath")

                    val u = parent.let {
                        val renderChain = path.path.subList(0, path.path.lastIndex)
                        val remoteChildren = childrenMap[(parent as State2).path] ?: it.children
                        val updatedChildren = remoteChildren.map { child ->
                            if (child.id == op.swap.id) op.swap.let { swap ->
                                // TODO I'm an idiot I used also here instead of let...
                                update(
                                    swap,
                                    renderChain
                                ) // TODO update is harder to call, need to provide the correct renderChain
                            } else child
                        }
                        // TODO I might have messed up here by using path instead of parentPath
                        updateChildren(parentPath, updatedChildren, "Happening inside swap")
                        it.c(updatedChildren)
                    }

                    update(
                        u,
                        renderChain = u.path.path
                    )
                }
                return

//                val path = paths.firstOrNull()
//                    ?: error("During swap operation, paths were empty ${op.swap.id}")
//
//                val parentId = path.path.getOrNull(path.path.lastIndex - 1)
//                    ?: error("During swap operation, last element in path was null ${op.swap.id}")
//
//                val parent = parentId.let { (get<State2>(it) as? ChildrenModifier2<*>) }
//                    ?: error("During swap operation, parent was not of type ChildrenModifier2<*> ${op.swap.id}")
//
//                val u = parent.let {
//                    val renderChain = path.path.subList(0, path.path.lastIndex)
//                    val updatedChildren = it.children.map { child ->
//                        if (child.id == op.swap.id) op.swap.also { swap ->
//                            update(
//                                swap,
//                                renderChain
//                            ) // TODO update is harder to call, need to provide the correct renderChain
//                        } else child
//                    }
//                    childrenMap[path] = updatedChildren
//                    it.c(
//                        updatedChildren
//                    )
//                }
//                update(
//                    u,
//                    renderChain = u.path.path
//                )
//
//                return
            }

            is NavigationConfig2.StateOperation2.PushDialog2 -> TODO()
            is NavigationConfig2.StateOperation2.Flow2 -> TODO()
            is NavigationConfig2.StateOperation2.Next2 -> TODO()
            is NavigationConfig2.StateOperation2.PushState2 -> TODO()
            is NavigationConfig2.StateOperation2.Back2 -> TODO()
            else -> TODO()
        }?.let {
            update(it, renderChain = it.path.path, insertIntoParent = true) // TODO pretty hacky to be honest
        }
    }

    // TODO Planned
    fun push(state: State2) {
        backstack.add(BackStackEntry2(direction = BackStackEntry2.Direction2.Forward2, state = state))
        reactivePush()
    }

    // TODO Planned
    fun pop() {
        if (cheapBackStack.isNotEmpty()) {
            val entry = cheapBackStack.removeLast()
            StateMachineSelect2(
                selected = entry.selected,
                targetMetaId = entry.targetMetaId,
            ).run(BoxState2(), this) // TODO box state is not used.
        } else {
            backstack.removeLast()
            backstack.dropLastWhile { !it.canPopBack }
            backstack[backstack.lastIndex] = backstack[backstack.lastIndex].copy(direction = Backward2)
            reactivePop()
        }
    }

    // TODO Planned
    fun canPop() = (backstack.isNotEmpty() && backstack.subList(0, backstack.lastIndex)
        .any { it.canPopBack }) || cheapBackStack.isNotEmpty()

    // TODO Planned
    fun pop(stateId: StateId2) = containerBackStack[stateId]!!.let {
        if (cheapBackStack.isNotEmpty()) {
            val entry = cheapBackStack.removeLast()
            StateMachineSelect2(
                selected = entry.selected,
                targetMetaId = entry.targetMetaId
            ).run(BoxState2(), this) // TODO box state is not used.
        } else {
            it.removeLast()
            it.dropLastWhile { item -> !item.canPopBack }
            it[it.lastIndex] = it[it.lastIndex].copy(direction = Backward2)
            reactivePop(stateId)
        }
    }

    // TODO Planned
    fun canPop(stateId: StateId2) = containerBackStack[stateId]!!.let {
        it.isNotEmpty() && it.subList(0, it.lastIndex).any { item -> item.canPopBack }
    }

    var emitSideEffect: (PopulatedSideEffectMeta2) -> Unit = { }

    // TODO Planned
    abstract fun reactiveUpdate(state: State2)
    // TODO Planned
    abstract fun reactivePush()
    // TODO Planned
    abstract fun reactivePop()
    // TODO Planned
    abstract fun reactivePush(stateId: StateId2)
    // TODO Planned
    abstract fun reactivePop(stateId: StateId2)
    // TODO Planned
    abstract fun reactiveUpdateChildren(path: Path2)

    // TODO Planned
    private fun insert(
        existing: List<State2>,
        addition: State2,
        insertion: StateId2,
        offset: Int = 0,
    ) = existing
        .indexOfFirst { it.id == insertion }
        .let { index ->
            if (index == -1) {
                existing + addition
            } else {
                existing.subList(0, index + offset) + addition + existing.subList(index + offset, existing.size)
            }
        }

    // TODO Planned
    private fun setTriggers(state: State2) {
        val triggers = mutableListOf<Trigger2>()
        if (state is OnClickModifier2) {
            triggers.add(state.onClickTrigger)
        }
        if (state is OnFieldUpdateModifier2) {
            triggers.add(state.onFieldUpdateTrigger)
        }
        if (state is OnMetaUpdateModifier2) {
            triggers.add(state.onMetaUpdateTrigger)
        }
        if (state is OnStateUpdateModifier2) {
            triggers.add(state.onStateUpdateTrigger)
        }
        if (state is OnToggleModifier2<out State2>) {
            triggers.add(state.onToggleTrigger)
        }

        val publisherStateActions = triggers.flatMap { it.actions }.filterIsInstance<StatePublisherActionModifier2>()
        publisherStateActions.forEach { action ->
            action.publishers.forEach { publisher ->
                if (stateListeners2[publisher] == null) {
                    stateListeners2[publisher] = mutableListOf()
                }
                stateListeners2[publisher]?.add(
                    RemoteAction2(
                        targetStateId = state.id, // TODO this is going to lead to problems as autogenerated ids are no longer added
                        action = action as Action2
                    )
                )
            }
        }

        val publisherMetaActions = triggers.flatMap { it.actions }.filterIsInstance<MetaPublisherActionModifier2>()
        publisherMetaActions.forEach { action ->
            action.publishers.forEach { publisher ->
                if (metaListeners2[publisher] == null) {
                    metaListeners2[publisher] = mutableListOf()
                }
                metaListeners2[publisher]?.add(
                    RemoteAction2(
                        targetStateId = state.id,
                        action = action as Action2
                    )
                )
            }
        }

        val filterMetaActions = triggers.flatMap { it.actions }.filterIsInstance<FilterVisibility2>()
        filterMetaActions.forEach { action ->
            action.filterGroup.filters.forEach { filter ->
                if (metaListeners2[filter.targetStore] == null) {
                    metaListeners2[filter.targetStore] = mutableListOf()
                }
                metaListeners2[filter.targetStore]?.add(
                    RemoteAction2(
                        targetStateId = state.id,
                        action = action as Action2
                    )
                )
            }
        }
    }
}