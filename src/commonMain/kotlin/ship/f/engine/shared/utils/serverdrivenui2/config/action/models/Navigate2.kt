package ship.f.engine.shared.utils.serverdrivenui2.config.action.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import ship.f.engine.shared.utils.serverdrivenui2.client.Client2
import ship.f.engine.shared.utils.serverdrivenui2.client3.Client3
import ship.f.engine.shared.utils.serverdrivenui2.client3.Client3.Companion.client3
import ship.f.engine.shared.utils.serverdrivenui2.client3.Path3
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.ReplaceChild2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.Swap2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.StateMachineMeta2.StateMachineOperation2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.ZoneViewModel2.Property.MultiProperty
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.ZoneViewModel2.Property.StringProperty
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.ConditionalLiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Path2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.LiveValue3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.ListValue
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.ext.createTime
import ship.f.engine.shared.utils.serverdrivenui2.ext.sduiLog
import ship.f.engine.shared.utils.serverdrivenui2.json.json2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2
import ship.f.engine.shared.utils.serverdrivenui2.state.VariantState2

@Serializable
@SerialName("Navigate2")
data class Navigate2(
    val config: NavigationConfig2,
) : Action2() {
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        client.navigate(config)
    }

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
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        TODO("Do not use client 2")
    }

    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        client.navigationEngine.navigate(operation)
    }
}

@Serializable
@SerialName("StateUpdateOnMetaUpdate2")
data class StateUpdateOnMetaUpdate2(
    override val targetMetaId: MetaId2
) : Action2(), TargetableMetaModifier2 {
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        client.addRemoteAction(
            metaId = targetMetaId,
            stateId = state.id,
            action = ResetState2(
                targetStateId = state.id
            )
        )
    }

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
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        client.addRemoteAction(
            metaId = targetMetaId,
            stateId = state.id,
            action = EmitSideEffect2(
                sideEffect = SideEffectMeta2(
                    metaId = metaId,
                    metas = listOf(targetMetaId),
                ),
            ),
        )
    }

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
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        (state as? LoadingModifier2<*>)?.c(loading = LoadingModifier2.Loading2(value))?.let {
            client.update(it)
        }
    }

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
@SerialName("ToggleFilter2")
data class ToggleFilter2(
    val filter: FilterMeta2,
) : Action2() {
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        (client.get(filter.targetStore) as? FilterStoreMeta2)?.let {
            if (it.metas.contains(filter)) {
                it.metas.remove(filter)
            } else {
                it.metas.add(filter)
            }
            client.update(it)
        }
    }

    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        TODO("Do not use")
    }
}

@Serializable
@SerialName("FilterVisibility2")
data class FilterVisibility2(
    val filterGroup: FilterGroupMeta2,
) : Action2() {
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        (state as? VisibilityModifier2<*>)?.c(visible = Visible2(filterGroup.filtersSatisfied(client)))?.let {
            client.update(it)
        }
    }

    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        TODO("Do not use")
    }
}

@Serializable
@SerialName("MatchValid2")
data class MatchValid2(
    override val publishers: List<StateId2>,
) : Action2(),
    StatePublisherActionModifier2 {
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        val valid = publishers.mapNotNull { client.get<State2>(it) as? ValidModifier2<*> }.all { it.valid.value }
        (state as? ValidModifier2<*>)?.c(valid = ValidModifier2.Valid2(valid))?.let {
            client.update(it)
        }
    }

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
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        client.emitSideEffect(sideEffect.toPopulated(client))
    }

    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        client.emitSideEffect(sideEffect.toPopulated(client))
    }
}

@Serializable
@SerialName("EmitPopulatedSideEffect2")
data class EmitPopulatedSideEffect2(
    val sideEffect: PopulatedSideEffectMeta2,
) : Action2() {
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        val enrichedSideEffect =
            sideEffect.copy(metas = sideEffect.metas.map { if (it is DataMeta2) it.toPopulatedDataMeta2(client) else it })
        client.emitSideEffect(enrichedSideEffect)
    }

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
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        val targetState = client.get<State2>(targetStateId)
        (targetState as? VisibilityModifier2<*>)?.c(visible = Visible2(!targetState.visible.value))?.let {
            client.update(it)
        }
    }

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
    override fun execute(
        state: State2,
        client: Client2
    ) {
        (client.get(targetMetaId) as? SelectedStoreMeta2)?.let { store ->
            client.update(store.copy(selected = selected))
            for (item in store.map) { // TODO this needs to be optimised
                if (item.key != selected) {
                    item.value.second.forEach { action ->
                        action.run(state, client)
                    }
                }
            }
            store.map[selected]?.first?.forEach { action ->
                action.run(state, client)
            }
        }
    }

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
    override fun execute(
        state: State2,
        client: Client2
    ) {
        (client.get(targetMetaId) as? ToggleMachineMeta2)?.let { store ->
            if (store.selected.contains(selected)) {
                store.selected.remove(selected)
                client.navigate(
                    NavigationConfig2(
                        operation = Swap2(
                            swap = store.map[selected]!!.inactive,
                            stateId = selected,
                        )
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
                client.navigate(
                    NavigationConfig2(
                        operation = Swap2(
                            swap = store.map[selected]!!.active,
                            stateId = selected,
                        )
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
    }

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
    override fun execute(
        state: State2,
        client: Client2
    ) {
        println("StateMachineSelect2: $selected")
        (client.get(targetMetaId) as? StateMachineMeta2)?.let { store ->
            if (addCurrentToBackstack) {
                if (backStackable.isEmpty() || backStackable.contains(store.selected)) {
                    if (client.cheapBackStack.lastOrNull() != store.selected) {
                        client.cheapBackStack.add(Client2.CheapBackStackEntry(store.selected, store.metaId))
                    }
                }
            }
            println("keys: ${store.map.keys}")
            // Update inactive operations
            val inactiveOperations = store.getOperations(store.selected)
            inactiveOperations.runOps(isActive = false, client = client)

            // Update active operations
            client.update(store.copy(selected = selected))
            val activeOperations = store.getOperations(selected)
            activeOperations.runOps(isActive = true, client = client)
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

    fun List<StateMachineOperation2>.runOps(isActive: Boolean, client: Client2) {
        forEach { operation ->
            when (operation) {
                is StateMachineOperation2.NestedOperation2 -> Unit
                is StateMachineOperation2.PushOperation2 -> {
                    if (isActive) {
                        client.navigate(
                            NavigationConfig2(
                                operation = ReplaceChild2(
                                    container = operation.container,
                                    stateId = operation.stateId,
                                    addToBackStack = operation.addToBackStack,
                                )
                            )
                        )
                    }
                }

                is StateMachineOperation2.SwapOperation2 -> {
                    client.navigate(
                        NavigationConfig2(
                            operation = Swap2(
                                swap = if (isActive) operation.active else operation.inactive,
                                stateId = if (isActive) operation.active.id else operation.inactive.id,
                            )
                        )
                    )
                }
            }
        }
    }

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
    override fun execute(
        state: State2,
        client: Client2
    ) {
        var moddedState = client.get<State2>(templateId)
        replacements.forEach { moddedState = mod(it.stateId, moddedState, it, client) }
        moddedState = moddedState.c(path = Path2())

        val moddedNavigation = when (navigation) {
            is StateOperation2.InsertionOperation2.End2 -> navigation.copy(stateId = moddedState.id)
            else -> navigation //TODO to handle other branches at a later point
        }

        client.update(moddedState)

        client.navigate(NavigationConfig2(operation = moddedNavigation))

        client.emitSideEffect(
            PopulatedSideEffectMeta2(
                metaId = sideEffectId,
                states = listOf(moddedState),
                metas = metas,
            )
        )
    }

    // TODO Improve algorithm to do multiple modifications at the same time for performance
    fun mod(
        id: StateId2,
        state: State2,
        mod: ReplacementOperation5,
        client: Client2
    ): State2 { // TODO improve autogenerate IDs to be deterministic to improve modification performance
        return when {
            id == state.id -> {
                val replaceText = when (mod.replacementType) {
                    is ReplacementType4.Random -> getRandomString()
                    is ReplacementType4.Time -> createTime()
                    is ReplacementType4.Copy -> (client.get<State2>(mod.replacementType.id) as? TextModifier2<*>)?.text
                        ?: error("Text not found")
                }

                val updatedState = when (mod.target) {
                    is ReplacementTarget.Scope -> state.c(id = state.id.copy(scope = replaceText))
                    is ReplacementTarget.Text -> (state as? TextModifier2<*>)?.let { state.text(replaceText) } ?: error(
                        "Did not find text to copy"
                    )
                }
                updatedState
            }

            state is ChildrenModifier2<*> -> state.c(
                children = state.children.map { mod(id, it, mod, client) }
            )

            else -> state
        }
    }

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
@SerialName("UpdateZoneModel3")
data class UpdateZoneModel3(
    override val targetMetaId: MetaId2,
    val liveValue: LiveValue3,
    val operation: Operation2,
) : Action2(), TargetableMetaModifier2 {
    override fun execute(
        state: State2,
        client: Client2
    ) {
        //TODO("Do not use")
    }

    @Serializable
    @SerialName("Operation2")
    sealed class Operation2 {
        @Serializable
        @SerialName("Set")
        data class Set(val property: String) : Operation2()

        @Serializable
        @SerialName("Add")
        data object Add : Operation2()

        @Serializable
        @SerialName("Insert")
        data class Insert(val property: String) : Operation2()

        @Serializable
        @SerialName("Toggle")
        data class Toggle(val property: String) : Operation2()
    }

    // TODO("Need to upgrade this method to be better")
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        sduiLog("updating zone model $targetMetaId $liveValue $operation", tag = "updateZoneModel")
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
                else -> error("Not supported yet $liveValue in $operation")
            }
            is Operation2.Insert -> when (liveValue) {
                is LiveValue3.StaticLiveValue3 -> {
                    val listValue = vm.map[operation.property] as? ListValue<*> ?: error("ListValue not for $targetMetaId")
                    if (listValue.value.contains(liveValue.value)) return
                    vm.map[operation.property] = ListValue(listValue.value + liveValue.value)
                }
                else -> error("Not supported yet $liveValue in $operation")
            }
            else -> error("Not supported yet $liveValue in $operation")
        }
        client.update(vm)
        client.commit()
    }
}

@Serializable
@SerialName("UpdateZoneModel")
data class UpdateZoneModel(
    override val targetMetaId: MetaId2,
    val liveValue: LiveValue2,
    val operation: Operation2,
) : Action2(), TargetableMetaModifier2 {
    override fun execute(
        state: State2,
        client: Client2
    ) {
        val vm = client.get(targetMetaId) as? ZoneViewModel2 ?: error("ZoneViewModel not found for $targetMetaId")
        when (operation) {
            is Operation2.Toggle -> when (liveValue) {
                is LiveValue2.TextStaticValue2 -> {
                    val multiProperty = vm.map[operation.property] as? MultiProperty
                        ?: error("MultiProperty not for ${operation.property} in $targetMetaId")
                    val toggleProperty = StringProperty(value = liveValue.value)
                    if (multiProperty.value.contains(toggleProperty)) {
                        vm.map[operation.property] = MultiProperty(multiProperty.value - toggleProperty)
                    } else {
                        vm.map[operation.property] = MultiProperty(multiProperty.value + toggleProperty)
                    }
                }

                else -> Unit
            }

            else -> Unit
        }
        client.update(vm)
    }

    @Serializable
    @SerialName("Operation2")
    sealed class Operation2 {
        @Serializable
        @SerialName("Set")
        data object Set : Operation2()

        @Serializable
        @SerialName("Add")
        data object Add : Operation2()

        @Serializable
        @SerialName("Insert")
        data class Insert(val property: String) : Operation2()

        @Serializable
        @SerialName("Toggle")
        data class Toggle(val property: String) : Operation2()
    }

    // TODO("Need to upgrade this method to be better")
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        val vm = client.get(targetMetaId) as? ZoneViewModel2 ?: error("ZoneViewModel not found for $targetMetaId")
        when (operation) {
            is Operation2.Toggle -> when (liveValue) {
                is LiveValue2.TextStaticValue2 -> {
                    val multiProperty = vm.map[operation.property] as? MultiProperty
                        ?: error("MultiProperty not for ${operation.property} in $targetMetaId")
                    val toggleProperty = StringProperty(value = liveValue.value)
                    if (multiProperty.value.contains(toggleProperty)) {
                        vm.map[operation.property] = MultiProperty(multiProperty.value - toggleProperty)
                    } else {
                        vm.map[operation.property] = MultiProperty(multiProperty.value + toggleProperty)
                    }
                }
                else -> Unit
            }

            else -> Unit
        }
        client.update(vm)
    }
}

@Serializable
@SerialName("LiveAction2")
data class LiveAction2(
    override val action: Action2,
    val liveValue: List<ConditionalLiveValue2>
) : Action2(), HigherOrderModifier2 {
    override fun execute(
        state: State2,
        client: Client2
    ) {
        if (liveValue.isNotEmpty() && liveValue.map { client.computeConditionalLive(it) }.all { it }) {
            action.run(state, client) // TODO the state will be incorrect because this is not a remote action
        }
    }

    // TODO will be upgraded shortly
    override fun execute3(
        state: State2,
        client: Client3,
    ) {
        val all = liveValue.map { client.computationEngine.computeConditionalLive(it) }.all { it }
        if (liveValue.isNotEmpty() && all) {
            action.run3(state, client) // TODO the state will be incorrect because this is not a remote action
            sduiLog(all, tag = "liveAction")
        }
    }
}

data class UpdateVariant(
    override val targetStateId: StateId2,
    val liveValue: LiveValue2,
) : Action2(), TargetableStateModifier2 {
    override fun execute(
        state: State2,
        client: Client2
    ) {
        TODO("Not compatible for client2")
    }

    override fun execute3(
        state: State2,
        client: Client3
    ) {
        val targetState = client.get(targetStateId) as? VariantState2 ?: error("VariantState2 not found for $targetStateId")
        client.update(targetState)
        client.commit()
    }
}

@Serializable
@SerialName("ExecuteDeferred2")
data class ExecuteDeferred2(
    override val deferKey: String,
) : Action2(), DeferredModifier2 {
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        client.getDeferredActions(deferKey)?.let { actions ->
            actions.forEach { remoteAction ->
                remoteAction.action.action.run(
                    state = client.get(remoteAction.targetStateId),
                    client = client,
                )
            }
        }
        client.clearDeferredActions(deferKey)
    }

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
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        client.getDeferredActions(deferKey)?.let { actions ->
            actions.forEach { action ->
                action.action.cachedState?.let { client.update(it) }
            }
        }
        client.clearDeferredActions(deferKey)
    }

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
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        client.addDeferredAction(
            RemoteAction2(
                targetStateId = state.id,
                action = this,
            )
        )
    }

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
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        action.run(
            state = client.get(targetStateId),
            client = client,
        )
    }

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
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        val state = client.get<State2>(targetStateId)
        val parentState = (state as? ChildrenModifier2<out State2>)?.c(emptyList())
        (client.get(targetMetaId) as? JsonMeta2)?.let { jsonMeta ->
            val jsonString = json2.encodeToString(parentState ?: state)
            val jsonElement = json2.parseToJsonElement(jsonString)
            client.update(jsonMeta.copy(json = jsonElement))
            println(client.get(targetMetaId))
        }
    }

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
    override fun execute(
        state: State2,
        client: Client2
    ) {
        client.update(client.get<State2>(targetStateId).reset())
    }

    override fun execute3(
        state: State2,
        client: Client3
    ) {
        client.update(client.get<State2>(targetStateId).reset())
        client.commit()
    }
}

@Serializable
@SerialName("ResetChildrenState2")
data class ResetChildrenState2(
    override val targetStateId: StateId2,
) : Action2(), TargetableStateModifier2 {
    override fun execute(
        state: State2,
        client: Client2
    ) {
        TODO("Not supported in v0.2")
    }

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
    override fun execute(
        state: State2,
        client: Client2
    ) {
        TODO("Not supported in v0.2")
    }

    override fun execute3(
        state: State2,
        client: Client3
    ) {
//        sduiLog("ResetDescendantState2 $targetStateId", tag = "resetDescendantState")
        (client.get<State2>(targetStateId) as? ChildrenModifier2<*>)?.children?.forEach {
//            sduiLog("ResetDescendantState2 ${it.id}", tag = "resetDescendantState > forEach")
            client.getReactiveOrNull<State2>(it.path3)?.value?.let { child ->
                client.update(child.reset())
                ResetDescendantState2(targetStateId = child.id).run3(child, client)
            }
        }
        client.update(client.get<State2>(targetStateId).reset())
    }
}

@Serializable
@SerialName("CommitState2")
data class CommitState2(
    val stateId: StateId2 = StateId2()
) : Action2() {
    override fun execute(
        state: State2,
        client: Client2
    ) {
        TODO("Not supported in v0.2")
    }

    override fun execute3(
        state: State2,
        client: Client3
    ) {
        sduiLog(client3.reactiveStates.keys.find {
            (it as? Path3.Local)?.path?.first()?.name == "EventDetail" && it.path.size == 1
        }, tag = "CommitState2")
        client.commit()
    }
}

@Serializable
@SerialName("ClearState2")
data class ClearState2(
    override val targetStateId: StateId2,
) : Action2(), TargetableStateModifier2 {
    override fun execute(
        state: State2,
        client: Client2
    ) {
        client.update(client.get<State2>(targetStateId).reset())
    }

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
