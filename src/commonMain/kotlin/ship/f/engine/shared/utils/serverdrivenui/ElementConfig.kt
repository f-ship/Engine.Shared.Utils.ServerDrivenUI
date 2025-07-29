package ship.f.engine.shared.utils.serverdrivenui

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.Element
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.ElementId
import ship.f.engine.shared.utils.serverdrivenui.state.State

@Serializable
data class ElementConfig (
    /**
     * Decides whether the element should be placed inside another element or if null on the current root screen config
     */
    val inside: ElementId,
    /**
     * Decides whether the element should be placed after another element or if null on the end of the children, surely there should be before aswell
     */
    val after: ElementId? = null,
    /**
     *  If true, the elements will take the place of another
     */
    val replace: ElementId? = null,
    /**
     * The element to be placed on the screen
     */
    val elements: List<Element<out State>>,
)