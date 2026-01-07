package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface ByteModifier2<S : State2> : StateModifier2  {
    val bytes: ByteArray?
    fun c(bytes: ByteArray): S
}