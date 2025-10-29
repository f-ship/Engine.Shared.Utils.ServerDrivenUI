package ship.f.engine.shared.utils.serverdrivenui2.client

import ship.f.engine.shared.utils.serverdrivenui2.client.BackStackEntry2.Direction2.Backward2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Action2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.DeferredAction2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.FilterVisibility2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.RemoteAction2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.MetaPublisherActionModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.StatePublisherActionModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.InsertionOperation2.*
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.PopulatedSideEffectMeta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Path2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.Trigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.ext.g2
import ship.f.engine.shared.utils.serverdrivenui2.ext.sduiLog
import ship.f.engine.shared.utils.serverdrivenui2.state.ScreenState2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

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

    val tempStateList = mutableListOf<StateId2>()

    fun hasFired(action: Action2) = firedActionMap[action.id] != null
    fun addFired(action: Action2) {
        firedActionMap[action.id] = action
    }
    fun addDeferredAction(remoteAction: RemoteAction2<DeferredAction2<*>>) {
        if (deferredActionMap[remoteAction.action.deferKey] == null) {
            deferredActionMap[remoteAction.action.deferKey] = listOf()
        }
        deferredActionMap[remoteAction.action.deferKey] =
            deferredActionMap.g2(remoteAction.action.deferKey) + listOf(remoteAction)
    }

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

    fun clearDeferredActions(key: String?) = deferredActionMap.remove(key)
    fun getDeferredActions(key: String?) = deferredActionMap[key]

    fun updateChildren(path: Path2, children: List<State2>, reason: String) {
        println("updating children for path: $path, reason: $reason")
        println("--------------------------------------------------------")
        childrenMap[path] = children
        reactiveUpdateChildren(path)
    }

//    inline fun <reified T : State2> get(stateId2: StateId2): T = stateMap.g2(stateId2) as T

    inline fun <reified T : State2> get(stateId2: StateId2): T { // TODO temporarily used to return first instance, will be upgraded to return list
        if (stateId2.name == "CameraGallery") sduiLog("Found CameraGallery", idPathsMap[stateId2])
        val paths = idPathsMap[stateId2] ?: error("Paths has not been found for stateId: $stateId2")
        val path = paths.firstOrNull() ?: error("no paths exist for stateId: $stateId2")
        return pathStateMap[path] as? T ?: error("no state exists for path: $path")
    }

    inline fun <reified T : State2> get(path: Path2): T = pathStateMap[path] as T
    inline fun <reified T : State2> getOrNull(path: Path2): T? = pathStateMap[path] as? T

    fun get(metaId2: MetaId2): Meta2 = metaMap.g2(metaId2)

    fun update(
        state: State2,
        renderChain: List<StateId2> = listOf(),
        insertIntoParent: Boolean = false
    ): State2 { //TODO probably need to find a better way of dynamic tree building incase a view isn't found as a fallback
        var currentState = state

        //Use the presence of an empty path to know that this state needs to be setup
        if (currentState.path.path.isEmpty() || renderChain.isNotEmpty()) {
            currentState = buildPaths(currentState, renderChain)
            setPaths(currentState)
            setViewModels(currentState)
            setReactivity(currentState)
        }

        //Ensure the current state itself is correctly updated
        if (!state.id.isAutoGenerated) {
            propagate(currentState)
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

    fun setViewModels(state: State2) {
        setTriggers(state)
        state.metas.forEach { meta -> metaMap[meta.metaId] = meta }
        (state as? ChildrenModifier2<*>)?.let {
            it.children.map { child -> setViewModels(child) }
        }
    }

    fun setReactivity(state: State2) {
        if (!state.id.isAutoGenerated) reactiveUpdate(state)
        (state as? ChildrenModifier2<*>)?.let {
            it.children.map { child -> setReactivity(child) }
        }
    }

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

    fun update(meta: Meta2) {
        metaMap[meta.metaId] = meta
        propagate(meta)
    }

    fun propagate(state: State2) {
        stateListeners2[state.id]?.forEach { listener ->
            listener.action.run(
                state = get(listener.targetStateId),
                client = this,
            )
        }
    }

    fun propagate(meta: Meta2) {
        metaListeners2[meta.metaId]?.forEach { it ->
            it.action.run(
                state = get(it.targetStateId),
                client = this
            )
        }
    }

    fun navigate(config: NavigationConfig2) {
        when (val op = config.operation) {
            is NavigationConfig2.StateOperation2.InsertionOperation2 -> {
                val paths = idPathsMap[op.inside]
                    ?: error("During insertion operation, no paths found in idPathsMap ${op.inside}")

                println("Insertion operation: ${op.inside}")

                val curatedPaths = paths.filter { path ->
                    val rootId = path.path.firstOrNull()
                    val possibleScreen = rootId?.let { getOrNull<State2>(path.copy(path = listOf(it))) }
                    sduiLog(possibleScreen)
                    (possibleScreen is ScreenState2)
                }.run {
                    sduiLog("Curated paths found for insertion operation: ${op.inside}")
                    ifEmpty {
                        sduiLog("No curated paths found for insertion operation: ${op.inside}", "Insertion Operation")
                        paths
                    }
                }

                curatedPaths.forEach { path ->
                    val parent = get<State2>(path) as? ChildrenModifier2<*>
                        ?: error("During insertion operation, parent was not of type ChildrenModifier2<*> ${op.inside}")

                    val child = get<State2>(op.stateId)

                    val children = childrenMap[path] ?: parent.children

                    val u = when (op) {
                        is Start2 -> parent.c(listOf(child) + children)
                        is End2 -> parent.c(children + child)
                        is Before2 -> parent.c(insert(children, child, op.before, op.offset))
                        is After2 -> parent.c(insert(children, child, op.after, op.offset))
                        is Remove2 -> parent.c(children.filter { child -> child.id != op.stateId })
                    }

                    updateChildren(u.path, (u as ChildrenModifier2<*>).children, "Happening inside Insertion") // TODO added update children
                    reactiveUpdate(u) // TODO replace with normal update

                    sduiLog(
                        "Insertion operation completed: $op",
                        path,
                        paths,
                        header = "Insertion Start",
                        footer = "Insertion End",
                    )
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
                    val upChild = update(child, path.path) //TODO Hopefully this will get things hooked up properly, I think this foolishness caused the bug, because of the render chain

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
                    val parentPath = path.copy(path.path.subList(0, path.path.lastIndex))
                    val parent = get<State2>(parentPath) as? ChildrenModifier2<*>
                        ?: error("During replaceChild operation, parent was not of type ChildrenModifier2<*> $parentPath")

                    val u = parent.let {
                        val renderChain = path.path.subList(0, path.path.lastIndex)
                        val updatedChildren = it.children.map { child ->
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
        }?.let {
            update(it, renderChain = it.path.path, insertIntoParent = true) // TODO pretty hacky to be honest
        }
    }

    fun push(state: State2) {
        backstack.add(BackStackEntry2(direction = BackStackEntry2.Direction2.Forward2, state = state))
        reactivePush()
    }

    fun pop() {
        backstack.removeLast()
        backstack.dropLastWhile { !it.canPopBack }
        backstack[backstack.lastIndex] = backstack[backstack.lastIndex].copy(direction = Backward2)
        reactivePop()
    }

    fun canPop() = backstack.isNotEmpty() && backstack.subList(0, backstack.lastIndex).any { it.canPopBack }

    fun pop(stateId: StateId2) = containerBackStack[stateId]!!.let {
        it.removeLast()
        it.dropLastWhile { item -> !item.canPopBack }
        it[it.lastIndex] = it[it.lastIndex].copy(direction = Backward2)
        reactivePop(stateId)
    }

    fun canPop(stateId: StateId2) = containerBackStack[stateId]!!.let {
        it.isNotEmpty() && it.subList(0, it.lastIndex).any { item -> item.canPopBack }
    }

    var emitSideEffect: (PopulatedSideEffectMeta2) -> Unit = { }

    abstract fun reactiveUpdate(state: State2)
    abstract fun reactivePush()
    abstract fun reactivePop()
    abstract fun reactivePush(stateId: StateId2)
    abstract fun reactivePop(stateId: StateId2)

    abstract fun reactiveUpdateChildren(path: Path2)

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