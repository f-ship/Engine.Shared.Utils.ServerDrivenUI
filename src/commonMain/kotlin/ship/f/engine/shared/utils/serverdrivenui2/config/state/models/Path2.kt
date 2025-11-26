package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2

@Serializable
@SerialName("Path2")
data class Path2(val path: List<StateId2> = listOf()) {
    operator fun plus(other: Path2): Path2 {
        return Path2(path + other.path)
    }
}

@Serializable
@SerialName("Path25")
sealed class Path25 {
    data class Root(val id: StateId2) : Path25()
    data class Rooted(val ids: List<StateId2>) : Path25()
    data class Stranded(val ids: List<StateId2>) : Path25()
    data object Void : Path25()
}