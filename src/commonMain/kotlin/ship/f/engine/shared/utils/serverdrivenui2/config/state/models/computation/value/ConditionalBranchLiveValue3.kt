package ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.Condition3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.LiveValue3

@Serializable
@SerialName("ConditionalValue")
data class ConditionalValue(
    val value1: LiveValue3,
    val condition: Condition3,
    val value2: LiveValue3,
    val trueBranch: LiveValue3 = LiveValue3.StaticLiveValue3(BooleanValue(true)),
    val falseBranch: LiveValue3 = LiveValue3.StaticLiveValue3(BooleanValue(false)),
) : Value