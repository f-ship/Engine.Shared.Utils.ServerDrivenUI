package ship.f.engine.shared.utils.serverdrivenui.action

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig

/**
 * Target is currently only used in the capacity of targeting a single element
 * In the future; it may be used to target multiple descendant elements where appropriate
 */
@Serializable
data class Target(
    val id: ScreenConfig.ID,
    val descendants: Descendants = Descendants.None,
) {
    @Serializable
    sealed class Descendants {
        @Serializable
        @SerialName("None")
        object None : Descendants()

        @Serializable
        @SerialName("Immediate")
        object Immediate : Descendants()

        @Serializable
        @SerialName("All")
        object All : Descendants()
    }
}