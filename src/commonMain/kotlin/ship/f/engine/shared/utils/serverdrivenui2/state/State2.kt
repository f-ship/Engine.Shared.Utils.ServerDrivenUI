package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client3.Client3.Companion.client3
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.DeferredAction2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.ConditionalBranchLiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.Trigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.OnBuildCompleteModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.OnInitialRenderModifier2

@Serializable
sealed class State2 :
    StateIdModifier2<State2>,
    PathModifier2<State2>,
    VisibilityModifier2<State2>,
    SizeModifier2<State2>,
    WeightModifier2<State2>,
    DrawModifier2<State2>,
    MetaModifier2<State2>,
    ResetModifier2<State2>,
    OnInitialRenderModifier2,
    OnBuildCompleteModifier2 {
    override val liveDraws: List<ConditionalBranchLiveValue2>?
        get() = null
    override fun liveDraws(liveDraws: List<ConditionalBranchLiveValue2>?): State2 {
        return this
    }

    fun Trigger2.trigger(cachedState: State2? = null) {
//        val client = get<Client2>()
//        actions.forEach {
//            if (this is OnInitialRenderModifier2 && client.hasFired(it)) return@forEach
//            (cachedState?.let { cacheState ->
//                (it as? DeferredAction2<*>)?.copy(cachedState = cacheState)
//            } ?: it).run(
//                state = this@State2,
//                client = client,
//            )
//        }

        actions.forEach {
            if (this is OnInitialRenderModifier2 && client3.hasFired(it)) return@forEach
            (cachedState?.let { cacheState ->
                (it as? DeferredAction2<*>)?.copy(cachedState = cacheState)
            } ?: it).run3(
                state = this@State2,
                client = client3,
            )
        }
    }

    inline fun <reified S : State2> S.update(block: S.() -> S): State2 {
        val update = block()
//        get<Client2>().update(update)
        return update.also { client3.update(update) }
    }
}
