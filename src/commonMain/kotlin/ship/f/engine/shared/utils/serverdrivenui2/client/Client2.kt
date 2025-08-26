package ship.f.engine.shared.utils.serverdrivenui2.client

import ship.f.engine.shared.utils.serverdrivenui2.client.BackStackEntry2.Direction2.Backward2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Action2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.ExecuteDeferred2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.FilterVisibility2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.MetaPublisherActionModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.StatePublisherActionModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.InsertionOperation2.*
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.PopulatedSideEffectMeta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.Trigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.ext.g2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2


abstract class Client2(open val projectName: String? = null) {
    val stateMap: MutableMap<StateId2, State2> = mutableMapOf()
    val stateListeners: MutableMap<StateId2, MutableList<Pair<StateId2, Action2>>> = mutableMapOf()
    val metaMap: MutableMap<Id2, Meta2> = mutableMapOf()
    val metaListeners: MutableMap<MetaId2, MutableList<Pair<StateId2, Action2>>> = mutableMapOf()

    val deferredActionMap: MutableMap<String, List<Triple<StateId2, Action2, State2?>>> = mutableMapOf()

    val firedActionMap: MutableMap<ActionId2, Action2> = mutableMapOf()

    val reverseIdMap: MutableMap<StateId2, StateId2> = mutableMapOf()
    val backstack: MutableList<BackStackEntry2> = mutableListOf()

    fun hasFired(action: Action2) = firedActionMap[action.id] != null
    fun addFired(action: Action2) {
        firedActionMap[action.id] = action
    }

    fun getDeferredActions(key: String?) = deferredActionMap[key]
    fun clearDeferredActions(key: String?) = deferredActionMap.remove(key)

    fun addDeferredAction(action: Action2, key: String, stateId: StateId2, cacheState: State2? = null) {
        if (action is ExecuteDeferred2) return // Do no at Execute actions here as this will cause infinite recursion
        if (deferredActionMap[key] == null) {
            deferredActionMap[key] = listOf()
        }
        deferredActionMap[key] = deferredActionMap.g2(key) + listOf(Triple(stateId,action, cacheState))
    }

    inline fun <reified T : State2> get(stateId2: StateId2): T = stateMap.g2(stateId2) as T
    fun get(metaId2: MetaId2): Meta2 = metaMap.g2(metaId2)

    fun update(state: State2) {
        if (stateMap[state.id] == null) {
            setTriggers(state)
            (state as? ChildrenModifier2<*>)?.children?.forEach { child ->
                update(child)
                reverseIdMap[child.id] = state.id
            }
            state.metas.forEach { meta -> metaMap[meta.id] = meta }
        }
        stateMap[state.id] = state
        propagate(state)
        reactiveUpdate(state)
    }

    fun update(meta: Meta2) {
        metaMap[meta.id] = meta
        propagate(meta)
    }

    fun propagate(state: State2) {
        stateListeners[state.id]?.forEach { listener ->
            listener.second.run(
                state = get(listener.first),
                client = this
            )
        }
    }

    fun propagate(meta: Meta2) {
        metaListeners[meta.id]?.forEach { it ->
            it.second.run(
                state = get(it.first),
                client = this
            )
        }
    }

    fun navigate(config: NavigationConfig2) {
        when (val op = config.operation) {
            is NavigationConfig2.StateOperation2.InsertionOperation2 -> (get<State2>(op.inside) as? ChildrenModifier2<*>)?.let {
                when (op) {
                    is Start2 -> it.c(listOf(get<State2>(op.stateId)) + it.children)
                    is End2 -> it.c(it.children + get<State2>(op.stateId))
                    is Before2 -> it.c(insert(it.children, get<State2>(op.stateId), op.before, op.offset))
                    is After2 -> it.c(insert(it.children, get<State2>(op.stateId), op.after, op.offset))
                }
            }
            is NavigationConfig2.StateOperation2.Replace2 -> (get<State2>(reverseIdMap.g2(op.replace)) as? ChildrenModifier2<*>)?.let {
                it.c(it.children.map { child -> if (child.id == op.replace) get<State2>(op.stateId) else child })
            }
            is NavigationConfig2.StateOperation2.Push2 -> {
                push(get<State2>(op.stateId))
                reactivePush()
                return
            }
        }?.let {
            update(it)
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

    var emitSideEffect: (PopulatedSideEffectMeta2) -> Unit = { }

    abstract fun reactiveUpdate(state: State2)
    abstract fun reactivePush()
    abstract fun reactivePop()

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
                if (stateListeners[publisher] == null) {
                    stateListeners[publisher] = mutableListOf()
                }
                stateListeners[publisher]?.add(Pair(state.id,action as Action2))
            }
        }

        val publisherMetaActions = triggers.flatMap { it.actions }.filterIsInstance<MetaPublisherActionModifier2>()
        publisherMetaActions.forEach { action ->
            action.publishers.forEach { publisher ->
                if (metaListeners[publisher] == null) {
                    metaListeners[publisher] = mutableListOf()
                }
                metaListeners[publisher]?.add(Pair(state.id, action as Action2))
            }
        }

        val filterMetaActions = triggers.flatMap { it.actions }.filterIsInstance<FilterVisibility2>()
        filterMetaActions.forEach { action ->
            action.filterGroup.filters.forEach { filter ->
                if (metaListeners[filter.targetGroup] == null) {
                    metaListeners[filter.targetGroup] = mutableListOf()
                }
                metaListeners[filter.targetGroup]?.add(Pair(state.id, action as Action2))
            }
        }
    }
}