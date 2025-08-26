package ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface DeferredModifier2 {
    val deferKey: String?
    val cachedState: State2?
}