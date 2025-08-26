package ship.f.engine.shared.utils.serverdrivenui2.ext

import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import ship.f.engine.shared.utils.serverdrivenui2.json.json2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

/**
 * Used to dangerously get a non-nullable value from a map that should have the value
 */
fun <K, V> Map<K, V>.g2(key: K): V = get(key) ?: error("Key $key not found in $this")

/**
 * Used to measure the time it takes to execute a block of code
 */
fun measureInMillis(id: Any, block: () -> Unit) {
    val start = Clock.System.now()

    block()

    val end = Clock.System.now()
    val elapsed = end - start
    println("$id: Took ${elapsed.inWholeMilliseconds} ms")
}

fun State2.validate2() = json2
    .encodeToString(this)
//    .apply { println(this) }
    .let { json2.decodeFromString<State2>(it) }