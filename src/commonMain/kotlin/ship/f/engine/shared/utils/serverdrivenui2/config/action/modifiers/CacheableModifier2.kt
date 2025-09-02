package ship.f.engine.shared.utils.serverdrivenui2.config.action.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface CacheableModifier2 {
    val cachedState: State2?
}