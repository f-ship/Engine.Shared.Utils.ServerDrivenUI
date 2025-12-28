package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.state.State2
import kotlin.jvm.JvmInline

interface ValidModifier2<T : State2> : StateModifier2 {
    val valid: Valid2
    fun c(valid: Valid2): T

    @Serializable
    data class Valid2(val value: Boolean)
}