package ship.f.engine.shared.utils.serverdrivenui.state

interface Value<T : State> {
    val value: String
    fun copyValue(v: String): T
}

interface Valid {
    val valid: Boolean
}

interface Select {
    val select: Boolean
}

interface Enable {
    val enable: Boolean
}

interface Loading {
    val loading: Boolean
}