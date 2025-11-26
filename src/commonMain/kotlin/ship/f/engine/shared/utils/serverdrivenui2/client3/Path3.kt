package ship.f.engine.shared.utils.serverdrivenui2.client3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2

@Serializable
@SerialName("Path3")
sealed class Path3 {
    abstract fun toRenderChain(): List<StateId2>
    @Serializable
    @SerialName("Init")
    data object Init : Path3() {
        override fun toRenderChain(): List<StateId2> = error("Trying to call toRenderChain on Init")
    }
    @Serializable
    @SerialName("Anon")
    data object Anon : Path3() {
        override fun toRenderChain(): List<StateId2> = error("Trying to call toRenderChain on Anon")
    }
    @Serializable
    @SerialName("Local")
    data class Local(val path: List<StateId2>) : Path3() {
        override fun toRenderChain(): List<StateId2> = path
    }
    @Serializable
    @SerialName("Global")
    data class Global(val path: StateId2) : Path3() {
        override fun toRenderChain(): List<StateId2> = listOf(path)
    }
}