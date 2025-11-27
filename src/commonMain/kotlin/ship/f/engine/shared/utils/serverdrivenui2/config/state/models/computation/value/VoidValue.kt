package ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("VoidValue")
data class VoidValue(val value: Boolean) : Value