package ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Action2

@Serializable
@SerialName("Trigger2")
sealed class Trigger2 {
    abstract val actions: List<Action2>
}
