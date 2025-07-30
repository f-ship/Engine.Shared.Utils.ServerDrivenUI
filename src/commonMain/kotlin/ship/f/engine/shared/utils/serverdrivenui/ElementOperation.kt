package ship.f.engine.shared.utils.serverdrivenui

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.*
import ship.f.engine.shared.utils.serverdrivenui.state.State

@Serializable
sealed class ElementOperation {
    abstract val elements: List<Element<out State>>

    @Serializable
    data class Start(
        val inside: ScreenId,
        override val elements: List<Element<out State>>
    ) : ElementOperation()

    @Serializable
    data class End(
        val inside: ScreenId,
        override val elements: List<Element<out State>>
    ) : ElementOperation()

    @Serializable
    data class Before(
        val inside: ScreenId, val before: ElementId,
        override val elements: List<Element<out State>>
    ) : ElementOperation()

    @Serializable
    data class After(
        val inside: ScreenId, val after: ElementId,
        override val elements: List<Element<out State>>
    ) : ElementOperation()
    @Serializable
    data class InsideAndStart(
        val inside: ElementId,
        override val elements: List<Element<out State>>
    ) : ElementOperation()

    @Serializable
    data class InsideAndEnd(
        val inside: ElementId,
        override val elements: List<Element<out State>>
    ) : ElementOperation()

    @Serializable
    data class InsideAndBefore(
        val inside: ElementId, val before: ElementId,
        override val elements: List<Element<out State>>
    ) : ElementOperation()

    @Serializable
    data class InsideAndAfter(
        val inside: ElementId, val after: ElementId,
        override val elements: List<Element<out State>>
    ) : ElementOperation()

    @Serializable
    data class Replace(
        val replace: ElementId,
        override val elements: List<Element<out State>>
    ) : ElementOperation()
}
