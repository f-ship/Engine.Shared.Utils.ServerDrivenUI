package ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2

@Serializable
@SerialName("ListLimitValue")
data class ListLimitValue<T : Value>(
    val value: List<T>,
    val min: Int,
    val max: Int,
    val limitMax: Boolean = true,
    val limitMin: Boolean = false,
    val stateListeners: List<StateId2> = emptyList(),
) : Value