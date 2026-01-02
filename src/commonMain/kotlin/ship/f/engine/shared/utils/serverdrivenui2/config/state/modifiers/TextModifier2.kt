package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.LiveValue3
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface TextModifier2<S : State2> : StateModifier2 {
    val text: String
    val liveText3: LiveValue3?
    fun text(text: String): S //TODO to avoid platform crash
    fun liveText3(liveText3: LiveValue3): S

}