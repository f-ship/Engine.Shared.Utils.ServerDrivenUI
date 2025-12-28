package ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2

@Serializable
@SerialName("Ref3")
sealed class Ref3 {
    @Serializable
    @SerialName("StateRef2")
    data class StateRef3(val id: StateId2) : Ref3()

    @Serializable
    @SerialName("VmRef2")
    data class VmRef3(val vm: MetaId2, val property: String) : Ref3()
}