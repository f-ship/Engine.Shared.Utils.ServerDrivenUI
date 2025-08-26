package ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers

import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

interface MetaModifier2<S : State2> {
    val metas: Set<Meta2>
    fun c(metas: Set<Meta2>): S
}