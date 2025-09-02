package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client.Client2
import ship.f.engine.shared.utils.serverdrivenui2.client.ClientHolder2.get
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.DeferredAction2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.Trigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.OnInitialRenderModifier2

@Serializable
sealed class State2 :
    StateIdModifier2<State2>,
    VisibilityModifier2<State2>,
    SizeModifier2<State2>,
    WeightModifier2<State2>,
    MetaModifier2<State2>,
    OnInitialRenderModifier2 {
    val triggers: MutableList<Trigger2> = mutableListOf()

    fun Trigger2.trigger(cachedState: State2? = null) {
        val client = get<Client2>()
        actions.forEach {
            if (this is OnInitialRenderModifier2 && client.hasFired(it)) return@forEach
            (cachedState?.let { cacheState ->
                (it as? DeferredAction2<*>)?.copy(cachedState = cacheState)
            } ?: it).run(
                state = this@State2,
                client = client,
            )
        }
    }

    inline fun <reified S : State2> S.update(block: S.() -> S) {
        return get<Client2>().update(block())
    }
}
