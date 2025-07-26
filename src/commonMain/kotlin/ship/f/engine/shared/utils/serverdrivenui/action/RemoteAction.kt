package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.Element
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.ID

@Serializable
data class RemoteAction(
    val action: Action,
    val id: ID = ID(id = "", scope = "") // Implemented at runtime
)

@Serializable
data class DeferredAction(
    val action: Action,
    val id: ID = ID(id = "", scope = ""),
    val metaID: ID = Meta.none,
    val restoredElement: Element<*>? = null,
)

@Serializable
data class RemoteMetaAction(
    val action: Action,
    val id: ID = ID(id = "", scope = ""), // TODO this still feels a bit wrong not going to lie
    val metaID: ID = Meta.none,
)