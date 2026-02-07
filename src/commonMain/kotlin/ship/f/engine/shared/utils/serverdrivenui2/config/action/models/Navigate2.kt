package ship.f.engine.shared.utils.serverdrivenui2.config.action.models

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames.Companion.ENGLISH_FULL
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client3.Client3
import ship.f.engine.shared.utils.serverdrivenui2.client3.Client3.Companion.client3
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.ReplaceChild2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.Swap2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.StateMachineMeta2.StateMachineOperation2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.Companion.NONE
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.LiveValue3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.Ref3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.LoadingModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.TextModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ValidModifier2
import ship.f.engine.shared.utils.serverdrivenui2.ext.getRandomString
import ship.f.engine.shared.utils.serverdrivenui2.ext.sduiLog
import ship.f.engine.shared.utils.serverdrivenui2.state.State2
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
@SerialName("Navigate2")
data class Navigate2(
    val config: NavigationConfig2,
) : Action2() {
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        client.navigationEngine.navigate(config.operation)
    }
}

@Serializable
@SerialName("Navigate3")
data class Navigate3(
    val operation: StateOperation2
) : Action2() {
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        val operation = if (operation is StateOperation2.InsertionStateOperation2.StateEnd2) operation.copy(state = state) else operation
        client.navigationEngine.navigate(operation)
    }
}

@Serializable
@SerialName("StateUpdateOnMetaUpdate2")
data class StateUpdateOnMetaUpdate2(
    override val targetMetaId: MetaId2
) : Action2(), TargetableMetaModifier2 {
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        TODO("Do not use")
    }
}

@Serializable
@SerialName("SideEffectOnMetaUpdate2")
data class SideEffectOnMetaUpdate2(
    val metaId: MetaId2,
    override val targetMetaId: MetaId2,
) : Action2(), TargetableMetaModifier2 {
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        TODO("Do not use")
    }
}

@Serializable
@SerialName("Loading2")
data class Loading2(
    val value: Boolean,
) : Action2() {
    override fun execute3(
        state: State2,
        client: Client3
    ) {
        (state as? LoadingModifier2<*>)?.c(loading = LoadingModifier2.Loading2(value))?.let {
            client.update(it)
            client.commit()
        }
    }
}

@Serializable
@SerialName("MatchValid2")
data class MatchValid2(
    override val publishers: List<StateId2>,
) : Action2(), StatePublisherActionModifier2 {
    // TODO("Do not use")
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        val valid = publishers.mapNotNull { client.get<State2>(it) as? ValidModifier2<*> }.all { it.valid.value }
        (state as? ValidModifier2<*>)?.c(valid = ValidModifier2.Valid2(valid))?.let {
            client.update(it)
        }
        client.commit()
    }
}

@Serializable
@SerialName("EmitSideEffect2")
data class EmitSideEffect2(
    val sideEffect: SideEffectMeta2,
) : Action2() {
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        client.emitSideEffect(sideEffect.toPopulated(client))
    }
}

@Serializable
@SerialName("EmitLocalEffect2")
data class EmitLocalEffect2(
    val sideEffect: SideEffectMeta2,
) : Action2() {
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        client.emitLocalEffect(sideEffect.toPopulated(client))
    }
}

@Serializable
@SerialName("EmitStateAsSideEffect2")
data class EmitStateAsSideEffect(
    val sideEffect: SideEffectMeta2,
) : Action2() {
    override fun execute3(
        state: State2,
        client: Client3
    ) {
        val populatedSideEffect = sideEffect.toPopulated(client)
        val appendedSideEffect = populatedSideEffect.copy(states = populatedSideEffect.states + state)
        client.emitSideEffect(appendedSideEffect)
    }

}

@Serializable
@SerialName("EmitPopulatedSideEffect2")
data class EmitPopulatedSideEffect2(
    val sideEffect: PopulatedSideEffectMeta2,
) : Action2() {
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        TODO("Do not use")
    }
}

@Serializable
@SerialName("ToggleVisibility2")
data class ToggleVisibility2(
    override val targetStateId: StateId2,
) : Action2(), TargetableStateModifier2 {
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        TODO("Do not use")
    }
}

@Serializable
@SerialName("Select")
data class Select2(
    override val targetMetaId: MetaId2,
    val selected: String,
) : Action2(), TargetableMetaModifier2 {
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        TODO("Do not use")
    }
}

@Serializable
@SerialName("ToggleMachineSelect2")
data class ToggleMachineSelect2(
    override val targetMetaId: MetaId2,
    val selected: StateId2,
) : Action2(), TargetableMetaModifier2 {
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        (client.get(targetMetaId) as? ToggleMachineMeta2)?.let { store ->
            if (store.selected.contains(selected)) {
                store.selected.remove(selected)
                client.navigationEngine.navigate(
                    operation = Swap2(
                        swap = store.map[selected]!!.inactive,
                        stateId = selected,
                    )
                )
                store.stateListeners.forEach { id ->
                    val s = client.get<State2>(id)
                    (s as? ValidModifier2<*>)?.let {
                        val update = it.c(valid = ValidModifier2.Valid2(false))
                        client.update(update)
                    }
                }
            } else if (store.selected.size < store.limit) {
                store.selected.add(selected)
                client.navigationEngine.navigate(
                    operation = Swap2(
                        swap = store.map[selected]!!.active,
                        stateId = selected,
                    )
                )
                if (store.selected.size == store.limit) {
                    store.stateListeners.forEach { id ->
                        val s = client.get<State2>(id)
                        (s as? ValidModifier2<*>)?.let {
                            val update = it.c(valid = ValidModifier2.Valid2(true))
                            client.update(update)
                        }
                    }
                }
            }
        }
        client.commit()
    }
}

@Serializable
@SerialName("StateMachineSelect2")
data class StateMachineSelect2(
    override val targetMetaId: MetaId2,
    val selected: List<String>,
    val addCurrentToBackstack: Boolean = false,
    val backStackable: List<List<String>> = listOf() // TODO need to make this smarter to include wildcards
) : Action2(), TargetableMetaModifier2 {
    fun List<StateMachineOperation2>.runOps3(isActive: Boolean, client: Client3) {
        forEach { operation ->
            when (operation) {
                is StateMachineOperation2.NestedOperation2 -> Unit
                is StateMachineOperation2.PushOperation2 -> {
                    if (isActive) {
                        client.navigationEngine.navigate(
                            operation = ReplaceChild2(
                                container = operation.container,
                                stateId = operation.stateId,
                                addToBackStack = operation.addToBackStack,
                            )
                        )
                    }
                }

                is StateMachineOperation2.SwapOperation2 -> {
                    client.navigationEngine.navigate(
                        operation = Swap2(
                            swap = if (isActive) operation.active else operation.inactive,
                            stateId = if (isActive) operation.active.id else operation.inactive.id,
                        )
                    )
                }
            }
        }
    }

    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        (client.get(targetMetaId) as? StateMachineMeta2)?.let { store ->
// TODO code needs to be rewritten to use the new unified backstack approach
//            if (addCurrentToBackstack) {
//                if (backStackable.isEmpty() || backStackable.contains(store.selected)) {
//                    if (client.navigationEngine.backstack.lastOrNull() != store.selected) {
//                        client.cheapBackStack.add(Client2.CheapBackStackEntry(store.selected, store.metaId))
//                    }
//                }
//            }
            println("keys: ${store.map.keys}")
            // Update inactive operations
            val inactiveOperations = store.getOperations(store.selected)
            inactiveOperations.runOps3(isActive = false, client = client)

            // Update active operations
            client.update(store.copy(selected = selected))
            val activeOperations = store.getOperations(selected)
            activeOperations.runOps3(isActive = true, client = client)
            println("-----------------------------")
            println("StateMachineSelect2: $selected vs ${store.selected}")
            println("inactiveOperations")
            inactiveOperations.filterIsInstance<StateMachineOperation2.SwapOperation2>().forEach {
                println(it)
            }
            println("activeOperations")
            activeOperations.filterIsInstance<StateMachineOperation2.SwapOperation2>().forEach {
                println(it)
            }
            println("-----------------------------")
        }
    }
}

@Serializable
@SerialName("ConfirmSideEffect2")
data class ConfirmSideEffect2(
    val templateId: StateId2,
    val sideEffectId: MetaId2,
    val replacements: List<ReplacementOperation5>,
    val navigation: StateOperation2,
    val metas: List<Meta2> = listOf(),
) : Action2() {
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        TODO("We should priortise removing this as it's not very good")
    }

    @Serializable
    @SerialName("ReplacementOperation5")
    data class ReplacementOperation5(
        val replacementType: ReplacementType4,
        val target: ReplacementTarget,
        val stateId: StateId2,
    )

    @Serializable
    @SerialName("ReplacementType4")
    sealed class ReplacementType4 { // TODO to create a much more extensive list of these
        @Serializable
        @SerialName("Random")
        data object Random : ReplacementType4()

        @Serializable
        @SerialName("Time")
        data object Time : ReplacementType4()

        @Serializable
        @SerialName("Copy")
        data class Copy(val id: StateId2) : ReplacementType4()
    }

    @Serializable
    @SerialName("ReplacementTarget")
    sealed class ReplacementTarget { // TODO to create a much more extensive list of these
        @Serializable
        @SerialName("Scope")
        data object Scope : ReplacementTarget()

        @Serializable
        @SerialName("Text")
        data object Text : ReplacementTarget()
    }
}

@Serializable
@SerialName("Create3")
data class Create3(
    override val targetMetaId: MetaId2,
    val copyMap: Map<String, String>,
    val newState: State2,
    val scope: LiveValue3,
    val stateActions: List<Action2>,
    val actions: List<Action2>,
) : Action2(), TargetableMetaModifier2 {
    override fun execute3(
        state: State2,
        client: Client3
    ) {
        //1) Update the scope of the state and descendants using the LiveValue3 (Will need to create RandomStringValue)
        val scopeValue = client3.computationEngine.getValue(scope)
        val scopeString = when(scopeValue) {
            is RandomStringValue -> getRandomString()
            is StringValue -> scopeValue.value
            is VoidValue -> NONE
            else -> error("Not supported yet $scopeValue")
        }
        val updatedState = replaceScope(newState, scopeString)
        //2) Get New Meta by searching state
        val zoneModel = updatedState.metas.filterIsInstance<ZoneViewModel3>().firstOrNull() ?: error("ZoneViewModel3 not found in $state")
        val copiedZoneModel = zoneModel.copy(
            metaId = zoneModel.metaId,
            map = zoneModel.map.toMutableMap(),
        )

        sduiLog("meta: $zoneModel", tag = "create3")
        //3) Get Base Meta
        val baseMeta = client3.get(targetMetaId) as? ZoneViewModel3 ?: error("ZoneViewModel3 not found for ${zoneModel.metaId}")
        //4) Copy the values over
        copyMap.forEach { (key, value) ->
            copiedZoneModel.map[value] = baseMeta.map[key] ?: error("Value not found for $value")
        }
        val updatedStateWithMeta = updatedState.cM(metas = updatedState.metas.map { meta -> if (meta.metaId == copiedZoneModel.metaId) copiedZoneModel else meta })

        sduiLog("meta: $copiedZoneModel", tag = "create3 > copyMap")
        //5) Perform actions on current state
        stateActions.forEach {
            it.run3(updatedStateWithMeta, client)
        }
        //6) Perform actions in general
        actions.forEach {
            it.run3(state, client)
        }
    }

    fun replaceScope(state: State2, scope: String): State2 {
        var moddedState = state
        if (!moddedState.id.isAutoGenerated) {
            moddedState = moddedState.c(id = moddedState.id.copy(scope = scope))
        }
        moddedState = moddedState.cM(moddedState.metas.filterIsInstance<ZoneViewModel3>().map {
            meta -> meta.copy(metaId = MetaId2(meta.metaId.name, scope))
        })
        moddedState = (moddedState as? ChildrenModifier2<*>)?.c(children = moddedState.children.map { replaceScope(it, scope) }) ?: moddedState
        return moddedState
    }
}

@Serializable
@SerialName("UpdateZoneModel3")
data class UpdateZoneModel3(
    override val targetMetaId: MetaId2,
    val liveValue: LiveValue3,
    val operation: Operation2,
) : Action2(), TargetableMetaModifier2 {
    @Serializable
    @SerialName("Operation2")
    sealed class Operation2 {
        @Serializable
        @SerialName("Set")
        data class Set(val property: String) : Operation2()

        @Serializable
        @SerialName("Remove")
        data class Remove(val property: String) : Operation2()

        @Serializable
        @SerialName("Insert")
        data class Insert(val property: String) : Operation2()

        @Serializable
        @SerialName("Toggle")
        data class Toggle(val property: String) : Operation2()
    }

    // TODO("Need to upgrade this method to be better")
    @OptIn(ExperimentalTime::class)
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        val start = Clock.System.now()
        val vm = client.get(targetMetaId) as? ZoneViewModel3 ?: error("ZoneViewModel3 not found for $targetMetaId")
        when (operation) {
            is Operation2.Toggle -> when (liveValue) {
                is LiveValue3.StaticLiveValue3 -> {
                    val listValue = vm.map[operation.property] as? ListValue<*>
                        ?: error("ListValue not for ${operation.property} in $targetMetaId")
                    sduiLog(listValue.value, operation.property, liveValue, tag = "updateZoneModel > Toggle")
                    if (listValue.value.contains(liveValue.value)) {
                        vm.map[operation.property] = ListValue(listValue.value - liveValue.value)
                    } else {
                        vm.map[operation.property] = ListValue(listValue.value + liveValue.value)
                    }
                }
                else -> error("Not supported yet $liveValue in $operation")
            }
            is Operation2.Set -> when (liveValue) {
                is LiveValue3.StaticLiveValue3 -> vm.map[operation.property] = liveValue.value
                is LiveValue3.ReferenceableLiveValue3 -> when(liveValue.ref) {
                    is Ref3.StateRef3 -> (client.get<State2>(liveValue.ref.id) as? TextModifier2<*> ?: error("Text not found for $liveValue")).let {
                        vm.map[operation.property] = StringValue(it.text)
                    }
                    is Ref3.VmRef3 -> error("Not supported yet $liveValue in $operation")
                }
                // TODO a very naive implementation for now, will need to create a more robust implementaiton with it's own type
                is LiveValue3.InstantNowLiveValue3 -> vm.map[operation.property] = StringValue(
                    formatDateEpoch(Clock.System.now().epochSeconds, TimeZone.currentSystemDefault())
                )
            }
            is Operation2.Insert -> when (liveValue) {
                is LiveValue3.StaticLiveValue3 -> {
                    val listValue = vm.map[operation.property] as? ListValue<*> ?: error("ListValue not for $targetMetaId")
                    if (listValue.value.contains(liveValue.value)) return
                    vm.map[operation.property] = ListValue(listValue.value + liveValue.value)
                }
                else -> error("Not supported yet $liveValue in $operation")
            }
            is Operation2.Remove -> when (liveValue) {
                is LiveValue3.StaticLiveValue3 -> {
                    val listValue = vm.map[operation.property] as? ListValue<*> ?: error("ListValue not for $targetMetaId")
                    if (!listValue.value.contains(liveValue.value)) return
                    vm.map[operation.property] = ListValue(listValue.value - liveValue.value)
                }
                else -> error("Not supported yet $liveValue in $operation")
            }
        }
        client.update(vm)
        client.commit()
        sduiLog("took ${Clock.System.now().minus(start).inWholeMilliseconds} ms", tag = "updateZoneModel")
    }

    fun formatDateEpoch(date: Long, timeZone: TimeZone): String { // TODO use a complex really smart date formatter
        val dateInstant = Instant.fromEpochSeconds(date)
        val dateFormat = LocalDateTime.Format {
            dayOfMonth()
            char(' ')
            monthName(ENGLISH_FULL)
            char(' ')
            hour()
            char(':')
            minute()
        }
        return dateFormat.format(dateInstant.toLocalDateTime(timeZone))
    }
}

@Serializable
@SerialName("LiveAction2")
data class LiveAction2(
    override val action: Action2,
    val liveValue: ConditionalValue,
) : Action2(), HigherOrderModifier2 {
    // TODO will be upgraded shortly
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        val shouldRun = when(liveValue){
            is AllConditionalValue -> liveValue.values.map {
                (client3.computationEngine.computeConditionalValue(it) as BooleanValue).value
            }.all { it } && liveValue.values.isNotEmpty()
            is AnyConditionalValue -> liveValue.values.map {
                (client3.computationEngine.computeConditionalValue(it) as BooleanValue).value
            }.any { it } && liveValue.values.isNotEmpty()
            is SingleConditionalValue -> (client3.computationEngine.computeConditionalValue(liveValue) as BooleanValue).value
        }
        if (shouldRun) {
            action.run3(state, client) // TODO the state will be incorrect because this is not a remote action
            sduiLog(shouldRun, tag = "liveAction")
        }
    }
}

@Serializable
@SerialName("ExecuteDeferred2")
data class ExecuteDeferred2(
    override val deferKey: String,
) : Action2(), DeferredModifier2 {
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        client.getDeferredActions(deferKey)?.let { actions ->
            actions.forEach { remoteAction ->
                remoteAction.action.action.run3(
                    state = client.get(remoteAction.targetStateId),
                    client = client,
                )
            }
        }
        client.clearDeferredActions(deferKey)
    }
}

@Serializable
@SerialName("ClearDeferred2")
data class ClearDeferred2(
    override val deferKey: String,
) : Action2(), DeferredModifier2 {
    override fun execute3(
        state: State2,
        client: Client3
    ) {
        client.getDeferredActions(deferKey)?.let { actions ->
            actions.forEach { action ->
                action.action.cachedState?.let { client.update(it) }
            }
        }
        client.clearDeferredActions(deferKey)
        client.commit()
    }
}

@Serializable
@SerialName("DeferredAction2")
data class DeferredAction2<T : Action2>(
    override val action: T,
    override val deferKey: String,
    override val cachedState: State2? = null,
) : Action2(), HigherOrderModifier2, DeferredModifier2, CacheableModifier2 {
    override fun execute3(
        state: State2,
        client: Client3
    ) {
        client.addDeferredAction(
            RemoteAction2(
                targetStateId = state.id,
                action = this,
            )
        )
    }
}

@Serializable
@SerialName("RemoteAction2")
data class RemoteAction2<T : Action2>(
    override val action: T,
    override val targetStateId: StateId2,
) : Action2(), HigherOrderModifier2, TargetableStateModifier2 {
    override fun execute3(
        state: State2,
        client: Client3
    ) {
        action.run3(
            state = client.get(targetStateId),
            client = client,
        )
    }
}

@Serializable
@SerialName("ToJsonAction2")
data class ToJsonAction2(
    override val targetStateId: StateId2,
    override val targetMetaId: MetaId2,
) : Action2(), TargetableStateModifier2, TargetableMetaModifier2 {
    override fun execute3(
        state: State2,
        client: Client3
    ) {
        TODO("Do not user this action")
    }
}

@Serializable
@SerialName("ResetState2")
data class ResetState2(
    override val targetStateId: StateId2,
) : Action2(), TargetableStateModifier2 {
    override fun execute3(
        state: State2,
        client: Client3
    ) {
//        client.update(client.get<State2>(targetStateId).reset())
        val targetState = client.get<State2>(targetStateId)
        val reactiveTargetState = client.getReactive<State2>(targetState.path3)
        client.update(reactiveTargetState.value.reset())
        client.commit()
    }
}

@Serializable
@SerialName("ResetChildrenState2")
data class ResetChildrenState2(
    override val targetStateId: StateId2,
) : Action2(), TargetableStateModifier2 {
    override fun execute3(
        state: State2,
        client: Client3
    ) {
        (client.get<State2>(targetStateId) as? ChildrenModifier2<*>)?.children?.forEach {
            client.update(client.getReactive<State2>(it.path3).value.reset())
        }
        client.update(client.get<State2>(targetStateId).reset())
    }
}

@Serializable
@SerialName("ResetDescendantState2")
data class ResetDescendantState2(
    override val targetStateId: StateId2,
) : Action2(), TargetableStateModifier2 {
    @OptIn(ExperimentalTime::class)
    override fun execute3(
        state: State2,
        client: Client3
    ) {
        val start = Clock.System.now()
//        sduiLog("ResetDescendantState2 $targetStateId", tag = "resetDescendantState")
        (client.get<State2>(targetStateId) as? ChildrenModifier2<*>)?.children?.forEach {
//            sduiLog("ResetDescendantState2 ${it.id}", tag = "resetDescendantState > forEach")
            client.getReactiveOrNull<State2>(it.path3)?.value?.let { child ->
                client.update(child.reset())
                ResetDescendantState2(targetStateId = child.id).run3(child, client)
            }
        }
        client.update(client.get<State2>(targetStateId).reset())
        sduiLog("took ${Clock.System.now().minus(start).inWholeMilliseconds} ms", tag = "resetDescendantState")
    }
}

@Serializable
@SerialName("CommitState2")
data class CommitState2(
    val stateId: StateId2 = StateId2()
) : Action2() {
    override fun execute3(
        state: State2,
        client: Client3
    ) {
        client.commit()
    }
}

@Serializable
@SerialName("ClearState2")
data class ClearState2(
    override val targetStateId: StateId2,
) : Action2(), TargetableStateModifier2 {
    override fun execute3(
        state: State2,
        client: Client3
    ) {
        (client.get<State2>(targetStateId) as? TextModifier2<*>)?.text(text = "")?.let{
            client.update(it)
            client.commit()
        }
    }
}
