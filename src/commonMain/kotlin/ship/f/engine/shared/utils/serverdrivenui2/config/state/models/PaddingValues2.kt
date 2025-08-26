package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.Serializable

@Serializable
data class PaddingValues2(
    val top: Int = 0,
    val bottom: Int = 0,
    val start: Int = 0,
    val end: Int = 0,
) {
    operator fun plus(
        other: PaddingValues2
    ) = padding(
        top = top + other.top,
        bottom = bottom + other.bottom,
        start = start + other.start,
        end = end + other.end
    )

    companion object {
        fun padding(all: Int = 0) = padding(all, all, all, all)
        fun padding(horizontal: Int = 0, vertical: Int = 0) = padding(vertical, vertical, horizontal, horizontal)
        fun padding(top: Int = 0, bottom: Int = 0, start: Int = 0, end: Int = 0) = PaddingValues2(top, bottom, start, end)
    }
}

