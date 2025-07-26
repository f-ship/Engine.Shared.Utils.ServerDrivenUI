package ship.f.engine.shared.utils.serverdrivenui.state

interface StateExtension

interface Value<T : State> : StateExtension {
    val value: String
    fun copyValue(v: String): T
}

interface Valid<T : State> : StateExtension {
    val valid: Boolean?
    fun copyValid(v: Boolean): T
}

interface Select : StateExtension {
    val select: Boolean
}

interface Enable : StateExtension {
    val enable: Boolean
}

interface Loading : StateExtension {
    val loading: Boolean
}

interface Visibility<T : State> : StateExtension {
    val visible: Boolean

    fun copyVisibility(v: Boolean): T
}