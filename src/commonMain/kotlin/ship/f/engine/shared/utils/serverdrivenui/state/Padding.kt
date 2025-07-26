package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.Serializable

@Serializable
data class Padding(
    val top: Int = 0,
    val bottom: Int = 0,
    val start: Int = 0,
    val end: Int = 0,
) {
    operator fun plus(other: Padding) = padding(top + other.top, bottom + other.bottom, start + other.start, end + other.end)
}

fun padding(top: Int = 0, bottom: Int = 0, start: Int = 0, end: Int = 0) = Padding(top, bottom, start, end)
fun padding(all: Int = 0) = padding(all, all, all, all)
fun padding(horizontal: Int = 0, vertical: Int = 0) = padding(top = vertical, bottom = vertical, start = horizontal, end = horizontal)