package ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Action2

@Serializable
@SerialName("OnStateUpdateTrigger2")
data class OnStateUpdateTrigger2(
    override val actions: List<Action2> = listOf(),
) : Trigger2() {
    constructor(vararg actions: Action2) : this(actions.toList())
}