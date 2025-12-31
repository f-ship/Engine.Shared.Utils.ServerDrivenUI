package ship.f.engine.shared.utils.serverdrivenui2.config.action.models

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client.Client2
import ship.f.engine.shared.utils.serverdrivenui2.client3.Client3
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.ActionIdModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.PopulatedSideEffectMeta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.ActionId2.Companion.autoActionId2
import ship.f.engine.shared.utils.serverdrivenui2.ext.sduiLog
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

@Serializable
sealed class Action2 : ActionIdModifier2 {
    override val id = autoActionId2() // This is to enable unique identification of actions
    protected abstract fun execute(
        state: State2,
        client: Client2,
    )

    protected abstract fun execute3(
        state: State2,
        client: Client3,
    )

    fun run(
        state: State2,
        client: Client2,
    ) {
        client.addFired(this) // TODO wrong order led to infinite depth recursion when not careful
        execute(state, client)
    }

    fun run3(
        state: State2,
        client: Client3,
    ) {
        try {
            client.addFired(this)
            execute3(state, client)
        } catch (e: Exception) {
            sduiLog("Error while executing action $id", tag = "EngineX > run3")
            client.emitSideEffect(
                PopulatedSideEffectMeta2(
                    metaId = Id2.MetaId2("%SDUIError%", "action"),
                )
            )
        }
    }
}
