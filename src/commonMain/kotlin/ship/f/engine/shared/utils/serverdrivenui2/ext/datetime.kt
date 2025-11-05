package ship.f.engine.shared.utils.serverdrivenui2.ext

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

fun createTime(): String {
    val now = Clock.System.now()
    val timeFormat = LocalDateTime.Format {
        hour()
        char(':')
        minute()
        char(' ')
        amPmMarker("am", "pm")
    }
    return timeFormat.format(now.toLocalDateTime(TimeZone.currentSystemDefault()))
}