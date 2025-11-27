package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.ConditionalBranchLiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.ConditionalValue
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Draw2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface DrawModifier2<S : State2> : StateModifier2 {
    val draws: List<Draw2>
    fun cD(draws: List<Draw2>): S

    val liveDraws: List<ConditionalBranchLiveValue2>?
    fun liveDraws(liveDraws: List<ConditionalBranchLiveValue2>?): S

    val liveDraws3: List<ConditionalValue>?
    fun liveDraws3(liveDraws3: List<ConditionalValue>?): S
}