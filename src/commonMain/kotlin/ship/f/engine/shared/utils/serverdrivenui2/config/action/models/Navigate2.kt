package ship.f.engine.shared.utils.serverdrivenui2.config.action.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import ship.f.engine.shared.utils.serverdrivenui2.client.Client2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.LoadingModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ValidModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.json.json2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

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
        (client.get(filter.targetGroup) as? FilterStoreMeta2)?.let {
            if (it.metas.contains(filter)) {
                it.metas.remove(filter)
            } else {
                it.metas.add(filter)
            }
            client.update(it)
        }
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
}
