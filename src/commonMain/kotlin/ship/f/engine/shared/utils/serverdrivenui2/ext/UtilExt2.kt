package ship.f.engine.shared.utils.serverdrivenui2.ext

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

fun getRandomString(length: Int = 128): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}