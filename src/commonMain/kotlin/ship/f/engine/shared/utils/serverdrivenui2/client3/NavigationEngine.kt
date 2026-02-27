package ship.f.engine.shared.utils.serverdrivenui2.client3

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ship.f.engine.shared.utils.serverdrivenui2.client3.BackStackEntry3.ScreenEntry
import ship.f.engine.shared.utils.serverdrivenui2.client3.BackStackEntry3.ViewEntry
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.ResetDescendantState2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.*
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.InsertionOperation2.*
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.PushState2.SavedZone
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.PopulatedSideEffectMeta2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.ZoneViewModel3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.ext.sduiLog
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

class NavigationEngine(val client: Client3) {
    val backstack: MutableList<BackStackEntry3> = mutableListOf()
    val currentScreen: MutableState<ScreenEntry?> = mutableStateOf(null)
    val canPopState: MutableState<Boolean> = mutableStateOf(false)

    val currentQueue: MutableList<StateId2> = mutableListOf()
    val currentQueueKeys: MutableList<String> = mutableListOf() // TODO Replace with a way to make sure actions can be fired only once

    val safeNavigationQueue: MutableList<SafeNavigationRecord> = mutableListOf()

    data class SafeNavigationRecord(val stateId: StateId2, val flowId: StateId2?)

    val recordMap: MutableMap<StateId2, Record> = mutableMapOf()

    val flowManager: MutableMap<StateId2, Flow2> = mutableMapOf()

    val droppedStates: MutableSet<StateId2> = mutableSetOf()

    fun navigate(operation: NavigationConfig2.StateOperation2) {
        try {
            when (operation) {
                is InsertionOperation2 -> {
                    val inside = client.get<State2>(operation.inside)
                    val parent = inside as? ChildrenModifier2<*>
                        ?: error("During insertion operation, parent was not of type ChildrenModifier<*> ${operation.inside}")
                    val child = client.get<State2>(operation.stateId)
                    client.initState(child, inside.path3.toRenderChain())

                    val updatedParent = when (operation) {
                        is Start2 -> parent.c(listOf(child) + parent.children)
                        is End2 -> parent.c(parent.children + child)
                        is Before2 -> parent.c(insert(parent.children, child, operation.before, operation.offset))
                        is After2 -> parent.c(insert(parent.children, child, operation.after, operation.offset))
                        is Remove2 -> parent.c(parent.children.filter { child -> child.id != operation.stateId })
                    }

                    client.update(updatedParent)
                }

                is Push2 -> {
                    val state = client.getOrNull<State2>(operation.stateId)
                    sduiLog(operation.stateId, state, tag = "NavigationEngine > navigate > Push2")
                    state?.let {
                        val entry = ScreenEntry(
                            state2 = state,
                            flowId = operation.flowId, // TODO usually null but not when doing safe navigation for a flow
                            canPopBack = operation.addToBackStack,
                        )
                        backstack.add(entry)
                        client.commit() // Need to commit before setting it to the current screen
                        currentScreen.value = entry
                        canPopState.value = canPop()
                    } ?: addRecordTimer(stateId = operation.stateId)
                }

                is CompleteFlow2 -> {
                    flowManager[operation.stateId]?.let { flow ->
                        flowManager[operation.stateId] = flow.copy(isComplete = true, dropped = flow.dropped + flow.dropOnCompletion)

                    } ?: sduiLog("Flow ${operation.stateId} not found", tag = "NavigationEngine > navigate > CompleteFlow2")

                    val state = client.getOrNull<State2>(operation.push.stateId)
                    sduiLog(operation.push.stateId, state, tag = "NavigationEngine > navigate > Push2")
                    state?.let {
                        val entry = ScreenEntry(
                            state2 = state,
                            canPopBack = operation.push.addToBackStack,
                        )
                        backstack.add(entry)
                        client.commit() // Need to commit before setting it to the current screen
                        currentScreen.value = entry
                        canPopState.value = canPop()
                    } ?: addRecordTimer(stateId = operation.push.stateId)
                }

                is Flow2 -> {
                    if (flowManager[operation.stateId] != null) {
                        return
                    }
                    flowManager[operation.stateId] = operation
                    if (operation.push) {
                        val state = client.getOrNull<State2>(operation.flow.first())
                        state?.let {
                            val entry = ScreenEntry(state2 = state, flowId = operation.stateId)
                            backstack.add(entry)
                            client.commit()
                            currentScreen.value = entry
                            canPopState.value = canPop()
                            flowManager[operation.stateId] = operation.copy(current = state.id)
                        } ?: addRecordTimer(stateId = operation.flow.first(), flowId = operation.stateId)
                    }
                }


                // TODO This and flow has now been marked for major reword, this does not currently interact with safeQueue
                // TODO it is possible to get stuck in a flow by removing all subsequent nexts in the stack if they load in too slow
                is Next2 -> {
                    val flow = flowManager[operation.stateId] ?: error("Flow ${operation.stateId} not found")
                    val index = flow.flow.indexOf(operation.current)
                    var newIndex = index + operation.skip + 1
                    var newItem = flow.flow[index]
                    while (newIndex < flow.flow.size) {
                        if (!flow.dropped.contains(flow.flow[newIndex])) {
                            newItem = flow.flow[newIndex]
                            break
                        }
                        newIndex++
                    }
                    if (newItem != operation.stateId) {
                        client.getOrNull<State2>(newItem)?.let { state ->
                            val entry = ScreenEntry(state2 = state, flowId = operation.stateId)
                            backstack.add(entry)
                            client.commit()
                            currentScreen.value = entry
                            canPopState.value = canPop()
                            flowManager[operation.stateId] = flow.copy(
                                current = newItem,
                                dropped = flow.dropped + if (operation.dropOnNext) setOf(operation.current) else emptySet(),
                                dropOnCompletion = flow.dropOnCompletion + if (operation.dropOnCompletion) setOf(operation.current) else emptySet()
                            )
                        } ?: addRecordTimer(stateId = newItem)
                    }
                }

                is ReplaceChild2 -> {
                    val inside = client.get<State2>(operation.container)
                    val parent = inside as? ChildrenModifier2<*>
                        ?: error("During insertion operation, parent was not of type ChildrenModifier<*> ${operation.container}")

                    val child = client.get<State2>(operation.stateId)
                    val updatedParent = parent.c(listOf(child))
                    client.update(updatedParent)
                }

                is ReplaceChildSave2 -> {
                    val inside = client.get<State2>(operation.container)
                    val parent = inside as? ChildrenModifier2<*>
                        ?: error("During insertion operation, parent was not of type ChildrenModifier<*> ${operation.container}")
                    val child = client.get<State2>(operation.stateId)
                    val updatedParent = parent.c(listOf(child))
                    client.update(updatedParent)
                }

                is Swap2 -> {
                    val originalState = client.get<State2>(operation.swap.id)
                    val renderingChain = originalState.path3.toRenderChain().run { subList(0, lastIndex) }
                    val updatedState = client.buildPaths(operation.swap, renderingChain).also { client.setPaths(it) }
                    client.update(updatedState)
                }

                is PushState2 -> {
//                    val start = Clock.System.now()
//                    client.getOrNull<State2>(operation.stateId)
//                        ?: safeNavigationQueue.add(operation.stateId).let { return }
//                    (client.get<State2>(operation.container) as? ChildrenModifier2<*>)
//                        ?: safeNavigationQueue.add(operation.stateId).let { return }

                    client.getOrNull<State2>(operation.stateId) ?: return
                    if (client.get<State2>(operation.container) !is ChildrenModifier2<*>) return

//                    val getStates = Clock.System.now()

                    var entry = ViewEntry(
                        containerId = operation.container,
                        stateId = operation.stateId,
                        remoteActions = listOf(),
                        canPopBack = true,
                        groupKey = operation.groupKey,
                        restoreContainer = operation.container,
                        restoreState = (client.get<State2>(operation.container) as ChildrenModifier2<*>).children.first().id,
                    )

                    when (val last = backstack.lastOrNull()) {
                        is ViewEntry -> {
                            if (last.groupKey == operation.groupKey) {
                                val old = backstack.removeLast() as ViewEntry
                                entry = entry.copy(restoreContainer = old.restoreContainer, restoreState = old.restoreState)
                            } else {
                                backstack.removeLast()
                                val updatedSavedZones = operation.savedZones.mapNotNull { (ref, value) ->
                                    (client.viewModels[ref.vm] as? ZoneViewModel3)?.map[ref.property]?.let {
                                        SavedZone(ref, it)
                                    }
                                }
                                backstack.add(last.copy(refreshStates = operation.refreshStates, savedZones = updatedSavedZones))
                            }
                        }

                        is ScreenEntry -> {
                            backstack.removeLast()
                            val updatedSavedZones = operation.savedZones.mapNotNull { (ref, value) ->
                                (client.viewModels[ref.vm] as? ZoneViewModel3)?.map[ref.property]?.let {
                                    SavedZone(ref, it)
                                }
                            }
                            backstack.add(last.copy(refreshStates = operation.refreshStates, savedZones = updatedSavedZones))
                        }

                        else -> Unit
                    }

//                    val handleBackStack = Clock.System.now()

                    backstack.add(entry)

//                    val addToBackstack = Clock.System.now()

                    navigate(ReplaceChild2(operation.container, operation.stateId))

//                    val replaceChild = Clock.System.now()

                    client.commit {
//                        sduiLog("realCommitTime ${Clock.System.now().minus(replaceChild).inWholeMilliseconds} ms", tag = "NavigationEngine > navigate > PushState2 > Perf Test")
                    } // Need to commit before setting it to the current screen
                    canPopState.value = canPop()

//                    val commitTime = Clock.System.now()

//                    sduiLog("getStates ${getStates.minus(start).inWholeMilliseconds} ms", tag = "NavigationEngine > navigate > PushState2 > Perf Test")
//                    sduiLog("handleBackStack ${handleBackStack.minus(getStates).inWholeMilliseconds} ms", tag = "NavigationEngine > navigate > PushState2 > Perf Test")
//                    sduiLog("addToBackstack ${addToBackstack.minus(handleBackStack).inWholeMilliseconds} ms", tag = "NavigationEngine > navigate > PushState2 > Perf Test")
//                    sduiLog("replaceChild ${replaceChild.minus(addToBackstack).inWholeMilliseconds} ms", tag = "NavigationEngine > navigate > PushState2 > Perf Test")
//                    sduiLog("commitTime ${commitTime.minus(replaceChild).inWholeMilliseconds} ms", tag = "NavigationEngine > navigate > PushState2 > Perf Test")
//                    sduiLog("total ${Clock.System.now().minus(start).inWholeMilliseconds} ms", tag = "NavigationEngine > navigate > PushState2 > Perf Test")

                }

                is Back2 -> pop()
                is InsertionStateOperation2.StateEnd2 -> {
                    if (operation.state == null) {
                        sduiLog(
                            "No state was passed for insertion state operation, nothing was done",
                            tag = "NavigationEngine > navigate > InsertionStateOperation2.End2"
                        )
                        return
                    }
                    val inside = client.get<State2>(operation.stateId)
                    val parent = inside as? ChildrenModifier2<*>
                        ?: error("During insertion operation, parent was not of type ChildrenModifier<*> ${operation.stateId}")

                    sduiLog(operation.state.metas, operation.state, tag = "NavigationEngine > navigate > InsertionStateOperation2.End2")

                    val updatedState = client.initState(operation.state, inside.path3.toRenderChain())
                    client.setViewModels(updatedState) // TODO because initState prevents this from happening due to renderChain
                    val updatedParent = parent.c(parent.children + updatedState)

                    client.update(updatedParent)
                    updateAscendants(updatedParent)
                }

                is MultiPush2 -> {
                    operation.stateIds.forEach { stateId ->
                        if (!droppedStates.contains(stateId)) {
                            navigate(Push2(stateId = stateId, addToBackStack = operation.addToBackStack))
                            return
                        }
                    }
                }

                else -> Unit
            }
            client.commit()
        } catch (e: Exception) {
            sduiLog("Navigation error: ${e.message}", tag = "NavigationEngine > navigate > error")
            client.emitSideEffect(
                PopulatedSideEffectMeta2(
                    metaId = MetaId2("%SDUIError%", "navigation"),
                )
            )
        }
    }

    fun updateAscendants(state: State2) {
        val parents = when (val p = state.path3) {
            is Path3.Anon, Path3.Init -> listOf()
            is Path3.Global -> listOf()
            is Path3.Local -> p.path
        }
        if (parents.isNotEmpty()) tryUpdateAscendants(state, parents.subList(0, parents.lastIndex))
        client.commit()
    }

    fun tryUpdateAscendants(state: State2, ascendants: List<StateId2>) {
        val parent = ascendants.last()
        val parentState = client.getOrNull<State2>(parent) ?: error("Failed to find parent in try Update Ascendants")
        val cM = parentState as? ChildrenModifier2<*> ?: error("Parent state was not of type ChildrenModifier2")
        val updatedParent = cM.c(cM.children.map { child -> if (child.id == state.id) state else child })
        client.update(updatedParent)
        val nextAscendants = ascendants.subList(0, ascendants.lastIndex)
        if (nextAscendants.isNotEmpty()) tryUpdateAscendants(updatedParent, nextAscendants)
    }

    fun checkNavigation(stateId: StateId2) {
        safeNavigationQueue.find { it.stateId == stateId }?.let { safeNav ->
            safeNavigationQueue.remove(safeNav)
            recordMap.remove(stateId)
            navigate(Push2(stateId = stateId, flowId = safeNav.flowId))
        }
    }

    fun canPop() = backstack.isNotEmpty() && backstack.subList(0, backstack.lastIndex).any { it.canPopBack().also { sduiLog("for backhandler", tag = "NavigationEngine > canPopBack") } }
        .also { sduiLog("result of canPop: $it, backstack size ${backstack.size}", tag = "NavigationEngine > canPopBack") }

    fun BackStackEntry3.canPopBack() = when (this) {
        is ScreenEntry -> {
            val flow = flowManager[flowId] ?: return canPopBack.also { sduiLog("result of canPopBack $it with ${state2.id} as can't find $flowId", tag = "NavigationEngine > canPopBack") }
            (!flow.dropped.contains(state2.id)). also { sduiLog("result of canPopBack $it with ${state2.id} for $flow", tag = "NavigationEngine > canPopBack") }
        }
        is ViewEntry -> canPopBack
    }


    fun pop() {
        try {
            val old = backstack.removeLast()
            sduiLog(backstack, tag = "NavigationEngine > pop > old")
            sduiLog(backstack.map { it.canPopBack }, tag = "NavigationEngine > pop > old")
            while (!backstack.last().canPopBack().also { sduiLog("in while loop", tag = "NavigationEngine > canPopBack") }) backstack.removeLast().also { sduiLog("removed last $it", tag = "NavigationEngine > canPopBack") }
            when (val entry = backstack.last()) {
                is ScreenEntry -> {
                    when (old) {
                        is ScreenEntry -> currentScreen.value =
                            entry.copy(direction2 = ScreenEntry.BackStackEntry2.Direction2.Backward2) // TODO wrap in an self destructing animation
                        is ViewEntry -> {
                            navigate(
                                ReplaceChild2(
                                    container = old.restoreContainer,
                                    stateId = old.restoreState,
                                )
                            )
                            sduiLog(entry.savedZones, tag = "NavigationEngine > pop > ScreenEntry > ViewEntry")
                            entry.savedZones.forEach {
                                (client.viewModels[it.ref.vm] as? ZoneViewModel3)?.let { zM ->
                                    zM.map[it.ref.property] = it.value
                                    client.update(zM)
                                }
                            }
                            entry.refreshStates.forEach {
                                ResetDescendantState2(it).run3(state = client.get<State2>(it), client = client)
                            }
                            client.commit()
                        }
                    }
                }

                is ViewEntry -> {
                    navigate(
                        ReplaceChild2(
                            stateId = entry.stateId,
                            container = entry.containerId
                        )
                    )
                    sduiLog(entry.savedZones, tag = "NavigationEngine > pop > ViewEntry > ViewEntry")
                    entry.savedZones.forEach {
                        (client.viewModels[it.ref.vm] as? ZoneViewModel3)?.let { zM ->
                            zM.map[it.ref.property] = it.value
                            client.update(zM)
                        }
                    }
                    entry.refreshStates.forEach {
                        ResetDescendantState2(it).run3(state = client.get<State2>(it), client = client)
                    }
                    client.commit {
                        if (old is ScreenEntry) {
                            backstack.filterIsInstance<ScreenEntry>().lastOrNull()?.let {
                                currentScreen.value = it.copy(direction2 = ScreenEntry.BackStackEntry2.Direction2.Backward2)
                            }
                        }
                    }
                }
            }
            canPopState.value = canPop()
        } catch (e: Exception) {
            sduiLog("Navigation error: ${e.message}", tag = "NavigationEngine > pop > error")
            client.emitSideEffect(
                PopulatedSideEffectMeta2(
                    metaId = MetaId2("%SDUIError%", "pop"),
                )
            )
        }
    }

    fun clear() {
        backstack.clear()
        currentQueue.clear()
        currentQueueKeys.clear()
        safeNavigationQueue.clear()
        recordMap.clear()
        flowManager.clear()
        droppedStates.clear()
    }

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

    private fun addRecordTimer(stateId: StateId2, flowId: StateId2? = null) {
        client.emitViewRequest(stateId)
        if (safeNavigationQueue.find { it.stateId == stateId } != null) return
        safeNavigationQueue.add(SafeNavigationRecord(stateId, flowId))
        val timestamp = Clock.System.now()
        recordMap[stateId] = Record(id = stateId, timestamp = timestamp)
        client.computationEngine.timer.createTimer(intervalMillis = 30000) {
            if (recordMap.containsKey(stateId)) {
                sduiLog("State $stateId not found, emitting view request", tag = "NavigationEngine > addRecordTimer")
                client.emitViewRequest(stateId)
            } else {
                sduiLog("State $stateId found, removing record timer", tag = "NavigationEngine > addRecordTimer")
            }
            recordMap.containsKey(stateId)
        }
    }

    data class Record(
        val id: StateId2,
        val timestamp: Instant,
    )
}
