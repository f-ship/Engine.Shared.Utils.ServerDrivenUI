package ship.f.engine.shared.utils.serverdrivenui2.ext

fun sduiLog(vararg message: Any?, tag: String? = "Test", condition: () -> Boolean = { true }) {
    if (SduiLogger.switch[tag] != false && condition()) {
        message.forEach {
            println("$tag: $it")
        }
    }
}

fun sduiLog(vararg message: Any?, header: String, footer: String = header, tag: String? = null) {
    if (SduiLogger.switch[tag] != false) {
        if (message.size > 1) {
            println("---------$header $tag----------")
            message.forEach {
                println("$tag: $it")
            }
            println("---------$footer $tag----------")
        } else {
            println("$tag: ${message.firstOrNull()}")
        }
    }
}

fun <T>sduiLog(list: Collection<T>, header: String = "start", footer: String = "end", tag: String? = null) {
    if (SduiLogger.switch[tag] != false) {
        if (list.size > 1) {
            println("---------$header $tag----------")
            list.forEach {
                println("$tag: $it")
            }
            println("---------$footer $tag----------")
        } else {
            println("$tag: ${list.firstOrNull()}")
        }
    }
}

object SduiLogger {
    val switch: MutableMap<String, Boolean> = mutableMapOf()
}