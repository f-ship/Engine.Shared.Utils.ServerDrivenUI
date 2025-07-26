package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.Serializable

@Serializable
sealed class ScaleType

@Serializable
data object Default : ScaleType()
@Serializable
data object FillWidth : ScaleType()
@Serializable
data class FillBounds(
    val limit: Limit? = null,
) : ScaleType() {
    @Serializable
    sealed class Limit {
        abstract val value: Int
        @Serializable
        data class Width(override val value: Int) : Limit()
        @Serializable
        data class Height(override val value: Int) : Limit()
    }
}