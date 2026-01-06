package ship.f.engine.shared.utils.serverdrivenui2.ext

import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import ship.f.engine.shared.utils.serverdrivenui2.json.json2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2


/**
 *
 */
fun <V, T> List<V>.zipMap(other: T, transform: (V) -> T): List<T> {
    val list = mutableListOf<T>()
    for (i in indices){
        list.add(transform(this[i]))
        if (i < lastIndex) list.add(other)
    }
    return list
}

fun <T> List<T>.wrapWith(element: T): List<T> {
    val list = mutableListOf<T>()
    list.add(element)
    forEach {
        list.add(it)
        list.add(element)
    }
    return list
}


/**
 * Used to dangerously get a non-nullable value from a map that should have the value
 */
//fun <K, V> Map<K, V>.g2(key: K): V = get(key) ?: error("Key $key not found in ${toString().let{ if (size < 100) it else it.substring(0, 100)}}...")
fun <K, V> Map<K, V>.g2(key: K): V = get(key) ?: error("Key $key not found in Map with keys: $keys")

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

/**
 * Validates that a State2 object can be serialized/deserialized through the complete Firestore flow:
 * kotlin object -> json string -> hashmap -> json string -> kotlin object
 */
fun State2.validate2(): State2 {
    // Step 1: kotlin object -> json string
    val jsonString1 = json2.encodeToString(this)
//    .apply { println("Step 1 - JSON: $this") }

    // Step 2: json string -> JsonElement (which can be converted to HashMap)
    val jsonElement = json2.parseToJsonElement(jsonString1)
//        .apply { println("Step 2 - JsonElement: $this") }

    // Step 3: JsonElement -> HashMap (simulating Firestore storage)
    val hashMap = jsonElementToHashMap(jsonElement)
//    .apply { println("Step 3 - HashMap: $this") }

    // Step 4: HashMap -> JsonElement
    val jsonElementFromHashMap = hashMapToJsonElement(hashMap)

    // Step 5: JsonElement -> json string
    val jsonString2 = json2.encodeToString(jsonElementFromHashMap)
//    .apply { println("Step 5 - JSON from HashMap: $this") }

    // Step 6: json string -> kotlin object
    return json2.decodeFromString<State2>(jsonString2)
}

fun State2.encode2(): Any? {
    val jsonString1 = json2.encodeToString(this)
//    .apply { println("Step 1 - JSON: $this") }

    // Step 2: json string -> JsonElement (which can be converted to HashMap)
    val jsonElement = json2.parseToJsonElement(jsonString1)
//        .apply { println("Step 2 - JsonElement: $this") }

    // Step 3: JsonElement -> HashMap (simulating Firestore storage)
    return jsonElementToHashMap(jsonElement)
//        .apply { println("Step 3 - HashMap: $this") }
}

fun Any?.decode(): State2? {
    // Step 4: HashMap -> JsonElement
    val jsonElementFromHashMap = hashMapToJsonElement(this)

    // Step 5: JsonElement -> json string
    val jsonString2 = json2.encodeToString(jsonElementFromHashMap)
//        .apply { println("Step 5 - JSON from HashMap: $this") }

    // Step 6: json string -> kotlin object
    return json2.decodeFromString<State2>(jsonString2)
}

/**
 * Converts a JsonElement to a HashMap, simulating Firestore's data structure
 */
private fun jsonElementToHashMap(element: JsonElement): Any? {
    return run {
        val map = mutableMapOf<String, Any?>()
        element.jsonObject.forEach { (key, value) ->
            map[key] = when (value) {
                is JsonArray -> jsonArrayToHashMap(value)
                is JsonObject -> jsonElementToHashMap(value)
                is JsonPrimitive -> if (value.content == "null") null else value.content
                is JsonNull -> null
            }
        }
        map
    }
}

private fun jsonArrayToHashMap(element: JsonArray): Any? {
    return run {
        val array = mutableListOf<Any?>()
        element.jsonArray.forEach { value ->
            array.add(when (value) {
                is JsonArray -> jsonArrayToHashMap(value)
                is JsonObject -> jsonElementToHashMap(value)
                is JsonPrimitive -> value.content
                is JsonNull -> null
            })
        }
        array
    }
}

/**
 * Converts a HashMap back to JsonElement, simulating retrieval from Firestore
 */
private fun hashMapToJsonElement(value: Any?): JsonElement {
    return when (value) {
        null -> JsonNull
        is String -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is Map<*, *> -> {
            val jsonObjectMap = mutableMapOf<String, JsonElement>()
            value.forEach { (key, mapValue) ->
                if (key is String) {
                    jsonObjectMap[key] = hashMapToJsonElement(mapValue)
                }
            }
            JsonObject(jsonObjectMap)
        }

        is List<*> -> {
            val jsonArray = value.map { hashMapToJsonElement(it) }
            JsonArray(jsonArray)
        }

        else -> JsonPrimitive(value.toString())
    }
}

fun getRandomString(length: Int = 16): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}