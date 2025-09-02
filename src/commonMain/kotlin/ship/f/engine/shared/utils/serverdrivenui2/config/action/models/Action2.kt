package ship.f.engine.shared.utils.serverdrivenui2.config.action.models

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client.Client2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers.ActionIdModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.ActionId2.Companion.autoActionId2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

@Serializable
sealed class Action2 : ActionIdModifier2 {
    override val id = autoActionId2() // This is to enable unique identification of actions
    protected abstract fun execute(
        state: State2,
        client: Client2,
    )

    fun run(
        state: State2,
        client: Client2,
    ) {
        execute(state, client)
        client.addFired(this)
    }
}
