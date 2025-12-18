package ship.f.engine.shared.utils.serverdrivenui2.ext

fun <K, V> MutableMap<K, V>.defaultIfNull(key: K, value: V, execute: MutableMap<K, V>.(V) -> V) {
    if (this[key] == null) {
        this[key] = value
    }
    this[key] = execute(this[key]!!)
}

fun <K, V> MutableMap<K, V>.ifNull(key: K, execute: MutableMap<K, V>.() -> V) {
    if (this[key] == null) this[key] = execute(this)
}