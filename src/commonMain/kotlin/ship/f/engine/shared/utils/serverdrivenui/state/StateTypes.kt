package ship.f.engine.shared.utils.serverdrivenui.state

interface Value<T : State> {
    val value: String
    fun copyValue(v: String): T
}

interface Valid<T : State> {
    val valid: Boolean?
    fun copyValid(v: Boolean): T
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