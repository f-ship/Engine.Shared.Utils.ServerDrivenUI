package ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("BooleanValue")
data class BooleanValue(val value: Boolean) : Value