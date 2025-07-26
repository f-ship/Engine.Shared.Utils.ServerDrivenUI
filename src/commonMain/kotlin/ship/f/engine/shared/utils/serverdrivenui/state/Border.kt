package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.Serializable

@Serializable
data class Border(
    val width: Int,
    val color: Long,
)