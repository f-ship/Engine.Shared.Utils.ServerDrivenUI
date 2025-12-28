package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface WeightModifier2<S : State2> : StateModifier2 {
    val weight: Weight2?
    fun c(weight: Weight2): S

    @Serializable
    @SerialName("Weight2") // TODO JvmInline was here and it only caused issues for chat?
    data class Weight2(val value: Float)
}