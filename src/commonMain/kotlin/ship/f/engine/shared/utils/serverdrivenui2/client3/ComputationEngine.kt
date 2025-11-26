package ship.f.engine.shared.utils.serverdrivenui2.client3

import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2

class ComputationEngine(val client: Client3) {
    inline fun <reified T : LiveValue2> computeConditionalBranchLive(liveValue2: LiveValue2.ConditionalBranchLiveValue2): T {
        TODO()
    }

    fun computeConditionalLive(liveValue2: LiveValue2.ConditionalLiveValue2): Boolean = TODO()
    fun computeLiveText(liveValue: LiveValue2.TextLiveValue2): String = TODO()

}