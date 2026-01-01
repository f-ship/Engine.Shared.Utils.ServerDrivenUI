package ship.f.engine.shared.utils.serverdrivenui2.client3

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ship.f.engine.shared.utils.serverdrivenui2.client.BackStackEntry2.Direction2.Backward2
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

    val safeNavigationQueue: MutableList<StateId2> = mutableListOf()

    fun navigate(operation: NavigationConfig2.StateOperation2) {
        try {
            when(operation) {
                is InsertionOperation2 -> {
                    val inside = client.get<State2>(operation.inside)
                    val parent = inside as? ChildrenModifier2<*>
                        ?: error("During insertion operation, parent was not of type ChildrenModifier<*> ${operation.inside}")
                    val child = client.get<State2>(operation.stateId)
                    client.initState(child, inside.path3.toRenderChain())

                    val updatedParent = when(operation){
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
                    state?.let {
                        val entry = ScreenEntry(state)
                        backstack.add(entry)
                        client.commit() // Need to commit before setting it to the current screen
                        currentScreen.value = entry
                    } ?: safeNavigationQueue.add(operation.stateId)
                }

                is Flow2 -> {
                    currentQueue.addAll(operation.flow)
                    if (operation.push) {
                        val item = currentQueue.removeFirst()
                        val state = client.getOrNull<State2>(item)
                        state?.let {
                            val entry = ScreenEntry(state)
                            backstack.add(entry)
                            client.commit() // Need to commit before setting it to the current screen
                            currentScreen.value = entry
                        } ?: safeNavigationQueue.add(item)
                    }
                }

                is Next2 -> {
                    operation.idempotentKey?.let { key ->
                        if (currentQueueKeys.contains(key)) return else currentQueueKeys.add(key)
                    }
                    repeat(operation.skip) {
                        sduiLog("Skipping item ${currentQueue.firstOrNull()}", tag = "NavigationEngine > navigate > Next2")
                        currentQueue.removeFirstOrNull()
                    }
                    val newItem = currentQueue.removeFirstOrNull() ?: return
                    val state = client.getOrNull<State2>(newItem)
                    state?.let {
                        val entry = ScreenEntry(state)
                        backstack.add(entry)
                        client.commit()
                        currentScreen.value = entry
                    } ?: safeNavigationQueue.add(newItem)
                }

                is ReplaceChild2 -> {
                    sduiLog("Replacing child ${operation.stateId} inside ${operation.container}", tag = "NavigationEngine > navigate > ReplaceChild2")
                    val inside = client.get<State2>(operation.container)
                    val parent = inside as? ChildrenModifier2<*>
                        ?: error("During insertion operation, parent was not of type ChildrenModifier<*> ${operation.container}")
                    val child = client.get<State2>(operation.stateId)
                    val updatedChild = client.initState(child, inside.path3.toRenderChain(), operation.clearState)
                    val updatedParent = parent.c(listOf(updatedChild))
                    client.update(updatedParent)
                }

                is ReplaceChildSave2 -> {
                    sduiLog("Replacing child ${operation.stateId} inside ${operation.container}", tag = "NavigationEngine > navigate > ReplaceChild2")
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
                    client.getOrNull<State2>(operation.stateId)
                        ?: safeNavigationQueue.add(operation.stateId).let { return }
                    (client.get<State2>(operation.container) as? ChildrenModifier2<*>)
                        ?: safeNavigationQueue.add(operation.stateId).let { return }

                    var entry = ViewEntry(
                        containerId = operation.container,
                        stateId = operation.stateId,
                        remoteActions = listOf(),
                        canPopBack = true,
                        groupKey = operation.groupKey,
                        restoreContainer = operation.container,
                        restoreState = (client.get<State2>(operation.container) as ChildrenModifier2<*>).children.first().id,
                    )

                    when(val last = backstack.lastOrNull()) {
                        is ViewEntry -> {
                            if (last.groupKey == operation.groupKey) {
                                val old = backstack.removeLast() as ViewEntry
                                entry = entry.copy(restoreContainer = old.restoreContainer, restoreState = old.restoreState)
                            }
                            else {
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

                    backstack.add(entry)
                    canPopState.value = canPop()
                    navigate(ReplaceChild2(operation.container, operation.stateId))
                    client.commit() // Need to commit before setting it to the current screen
                }

                is Back2 -> pop()
                is InsertionStateOperation2.StateEnd2 -> {
                    if (operation.state == null) {
                        sduiLog("No state was passed for insertion state operation, nothing was done", tag = "NavigationEngine > navigate > InsertionStateOperation2.End2")
                        return
                    }
                    val inside = client.get<State2>(operation.stateId)
                    val parent = inside as? ChildrenModifier2<*>
                        ?: error("During insertion operation, parent was not of type ChildrenModifier<*> ${operation.stateId}")

                    sduiLog(operation.state.metas, tag = "NavigationEngine > navigate > InsertionStateOperation2.End2")

                    val updatedState = client.initState(operation.state, inside.path3.toRenderChain())
                    client.setViewModels(updatedState) // TODO because initState prevents this from happening due to renderChain
                    val updatedParent = parent.c(parent.children + updatedState)

                    client.update(updatedParent)
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

    fun checkNavigation(stateId: StateId2) {
        if (safeNavigationQueue.contains(stateId)) {
            safeNavigationQueue.remove(stateId)
            navigate(Push2(stateId))
        }
    }

    fun canPop() = backstack.isNotEmpty() && backstack.subList(0, backstack.lastIndex).any { it.canPopBack }
    fun pop() {
        try {
            val old = backstack.removeLast()
            backstack.dropLastWhile { !it.canPopBack }
            when(val entry = backstack.last()) {
                is ScreenEntry -> {
                    when (old) {
                        is ScreenEntry -> currentScreen.value = entry.copy(direction2 = Backward2) // TODO wrap in an self destructing animation
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
                    client.commit()
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
}