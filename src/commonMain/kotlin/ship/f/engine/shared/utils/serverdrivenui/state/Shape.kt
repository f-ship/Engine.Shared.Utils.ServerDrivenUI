package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.Serializable

@Serializable
sealed class Shape

@Serializable
data class RoundedRectangle(
    val topStart: Int = 0,
    val topEnd: Int = 0,
    val bottomStart: Int = 0,
    val bottomEnd: Int = 0,
) : Shape()

fun roundedRectangle(topStart: Int = 0, topEnd: Int = 0, bottomStart: Int = 0, bottomEnd: Int = 0) = RoundedRectangle(topStart, topEnd, bottomStart, bottomEnd)
fun roundedRectangle(all: Int) = roundedRectangle(all, all, all, all)