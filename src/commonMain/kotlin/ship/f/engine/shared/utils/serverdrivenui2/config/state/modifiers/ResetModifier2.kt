package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface ResetModifier2 <T : State2> : StateModifier2 {
    val counter: Int
    fun reset(counter: Int = this.counter + 1): T
}