package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig

@Serializable
data class RemoteAction(
    val action: Action,
    val id: ScreenConfig.ID = ScreenConfig.ID(id = "", scope = "") // Implemented at runtime
)