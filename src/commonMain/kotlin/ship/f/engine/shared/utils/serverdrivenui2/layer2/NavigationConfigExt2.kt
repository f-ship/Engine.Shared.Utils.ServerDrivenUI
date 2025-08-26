package ship.f.engine.shared.utils.serverdrivenui2.layer2

import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.NavigationConfig2.StateOperation2.Push2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2

fun String.toPush() = listOf(
    NavigationConfig2(
        operation = Push2(
            stateId = StateId2(this)
        )
    )
)