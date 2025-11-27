package ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Value

@Serializable
@SerialName("LiveValue3")
sealed class LiveValue3 {
    @Serializable
    @SerialName("ReferenceableLiveValue3")
    data class ReferenceableLiveValue3(val ref: Ref3): LiveValue3() {
        fun copyRef(ref: Ref3): ReferenceableLiveValue3 {
            return when(ref) {
                is Ref3.VmRef3 -> ReferenceableLiveValue3(Ref3.VmRef3(ref.vm, ref.property))
                is Ref3.StateRef3 -> ReferenceableLiveValue3(Ref3.StateRef3(ref.id))
            }
        }
    }

    @Serializable
    @SerialName("StaticLiveValue3")
    data class StaticLiveValue3(val value: Value): LiveValue3()

    @Serializable
    @SerialName("InstantNowLiveValue3")
    data class InstantNowLiveValue3(val refreshSeconds: Int? = null): LiveValue3()
}