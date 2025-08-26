package ship.f.engine.shared.utils.serverdrivenui.ext

import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.Companion.DEFAULT_SCREEN_SCOPE
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.Companion.screenId
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.Element.Companion.DEFAULT_ELEMENT_SCOPE
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.ElementId
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.MetaId
import ship.f.engine.shared.utils.serverdrivenui.action.Meta
import ship.f.engine.shared.utils.serverdrivenui.action.Meta.Companion.DEFAULT_META_SCOPE
import ship.f.engine.shared.utils.serverdrivenui.json.json

/**
 * Used to dangerously get a non-nullable value from a map that should have the value
 */
fun <K, V> Map<K, V>.fGet(key: K): V = get(key) ?: error("Key $key not found in $this")

/**
 * Weak method used to generate a random string but should be replaced with a robust guid generator if still needed
 */
fun getRandomString(length: Int = 16) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

/**
 * Used to measure the time it takes to execute a block of code
 */
fun measureInMillis(id: Any, block: () -> Unit) {
    val start = Clock.System.now()

    block()

    val end = Clock.System.now()
    val elapsed = end - start
//    println("$id: Took ${elapsed.inWholeMilliseconds} ms") // TODO temporarily disabled
}

inline fun <reified T> Meta.runIf(block: T.() -> Unit) = when(this){
    is T -> block(this)
    else -> TODO("Meta $this is not of type ${T::class}")
}

/**
 * Used to generate basic unique ids for components
 */
var count = 0
fun auto() = id(value = "${getRandomString()}-Auto-${count++}") // TODO() Super janky and still have a random chance of crashing very rarely

/**
 * Convenience method used to create an id from a string
 */
fun id(value: String, scope: String = DEFAULT_ELEMENT_SCOPE) = ElementId(id = value, scope = scope)

/**
 * Convenience method used to create a meta id from a string
 */
fun metaId(value: String, scope: String = DEFAULT_META_SCOPE) = MetaId(id = value, scope = scope)

fun autoScreenId() = screenId(value = "Auto-${count++}", scope = DEFAULT_SCREEN_SCOPE)

fun ScreenConfig.validate() = json
    .encodeToString(this)
//    .apply { println(this) }
    .let { json.decodeFromString<ScreenConfig>(it) }