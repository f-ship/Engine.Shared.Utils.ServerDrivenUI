package ship.f.engine.shared.utils.serverdrivenui2.config.action.models

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client.Client2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.StatePublisherActionModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.LoadingModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ValidModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

@Serializable
data class Navigate2(
    override val deferKey: String? = null,
    override val cachedState: State2? = null,
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
data class Loading2(
    override val deferKey: String? = null,
    override val cachedState: State2? = null,
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
data class ToggleFilter2(
    override val deferKey: String? = null,
    override val cachedState: State2? = null,
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
data class FilterVisibility2(
    override val deferKey: String? = null,
    override val cachedState: State2? = null,
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
data class MatchValid2(
    override val deferKey: String? = null,
    override val cachedState: State2? = null,
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
data class EmitSideEffect2(
    override val deferKey: String? = null,
    override val cachedState: State2? = null,
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
data class ToggleVisibility2(
    override val deferKey: String? = null,
    override val cachedState: State2? = null,
    val targetId: StateId2,
) : Action2() {
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        val targetState = client.get<State2>(targetId)
        (targetState as? VisibilityModifier2<*>)?.c(visible = Visible2(!targetState.visible.value))?.let {
            client.update(it)
        }
    }
}

@Serializable
data class ExecuteDeferred2(
    override val deferKey: String? = null,
    override val cachedState: State2?,
) : Action2() {
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        client.getDeferredActions(deferKey)?.let { actions ->
            actions.forEach { action ->
                action.second.runDeferred(state = client.get(action.first), client = client)
            }
        }
        client.clearDeferredActions(deferKey)
    }
}

@Serializable
data class ClearDeferred2(
    override val deferKey: String?,
    override val cachedState: State2?,
) : Action2() {
    override fun execute(
        state: State2,
        client: Client2,
    ) {
        client.getDeferredActions(deferKey)?.let { actions ->
            actions.forEach { action ->
                action.third?.let{ client.update(it) }
            }
        }
        client.clearDeferredActions(deferKey)
    }
}
