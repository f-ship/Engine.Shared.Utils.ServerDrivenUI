package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.Serializable

@Serializable
sealed class Size

@Serializable
data class Fixed(
    val height: Int,
    val width: Int,
) : Size() {
    companion object {
        val IconSmall = Fixed(12, 12)
        val IconStandard = Fixed(16, 16)
        val IconLarge = Fixed(24, 24)
        val IconExtraLarge = Fixed(32, 32)
    }
}

@Serializable
data class Fill(
    val horizontalFill: Float? = null,
    val verticalFill: Float? = null,
) : Size()

@Serializable
data class Weight(
    val horizontalWeight: Float? = null,
    val verticalWeight: Float? = null,
) : Size()

@Serializable
data object Window : Size()

@Serializable
data object DefaultSize : Size()

/**
 * A size that matches the parent's height without influencing the parent's height calculation.
 * Only other components in the parent will determine the parent's height.
 * 
 * Example usage:
 * ```
 * // Create a component that matches parent height without affecting parent sizing
 * val myComponent = ComponentState(
 *     size = MatchParent,
 *     // other properties...
 * )
 * ```
 * 
 * This is useful for components that need to fill the available height in a container
 * without forcing the container to expand to their size.
 */
@Serializable
data object MatchParent : Size()
