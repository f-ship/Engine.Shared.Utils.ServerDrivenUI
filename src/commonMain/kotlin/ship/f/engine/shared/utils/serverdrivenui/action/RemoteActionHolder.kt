package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.*

@Serializable
data class RemoteActionHolder(
    val action: Action,
    val id: ElementId = Element.none, // Implemented at runtime
    val metaID: MetaId = Meta.none,
)

@Serializable
data class DeferredActionHolder(
    val action: Action,
    val id: ElementId = Element.none,
    val metaID: MetaId = Meta.none,
    val restoredElement: Element<*>? = null,
)