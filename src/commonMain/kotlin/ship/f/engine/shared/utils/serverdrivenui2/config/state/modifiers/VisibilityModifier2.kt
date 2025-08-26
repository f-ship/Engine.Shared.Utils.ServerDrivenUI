package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.state.State2
import kotlin.jvm.JvmInline

interface VisibilityModifier2<S : State2> : StateModifier2 {
    val visible: Visible2
    fun c(visible: Visible2): S

    @Serializable
    @JvmInline
    value class Visible2(val value: Boolean)
}