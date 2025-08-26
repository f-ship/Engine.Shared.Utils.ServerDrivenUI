package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.state.State2
import kotlin.jvm.JvmInline

interface LoadingModifier2<T : State2> : StateModifier2 {
    val loading: Loading2
    fun c(loading: Loading2): T

    @Serializable
    @JvmInline
    value class Loading2(val value: Boolean)
}

