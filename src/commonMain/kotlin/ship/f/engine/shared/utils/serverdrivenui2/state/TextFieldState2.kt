package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Shapes2.CornerBasedShape2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2.DefaultSize2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.PaddingModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ShapeModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ValidModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ValidModifier2.Valid2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2.Weight2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnFieldUpdateTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnInitialRenderTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.OnFieldUpdateModifier2

@Serializable
@SerialName("TextFieldState2")
data class TextFieldState2(
    override val id: StateId2 = autoStateId2(),
    override val padding: PaddingValues2 = PaddingValues2(),
    override val visible: Visible2 = Visible2(true),
    override val size: Size2 = DefaultSize2,
    override val shape: CornerBasedShape2 = Shapes2.DefaultShapes2.small,
    override val valid: Valid2 = Valid2(true),
    override val weight: Weight2? = null,
    override val onFieldUpdateTrigger: OnFieldUpdateTrigger2 = OnFieldUpdateTrigger2(),
    override val onInitialRenderTrigger: OnInitialRenderTrigger2 = OnInitialRenderTrigger2(),
    override val metas: List<Meta2> = listOf(),
    override val counter: Int = 0,
    override val draws: List<Draw2> = listOf(),
    val leadingIcon: ImageState2? = null,
    val initialValue: String = "",
    val placeholder: String = "",
    val label: String = "",
    val text: String = "",
    val fieldType: FieldType2 = FieldType2.Text,
    val validations: List<Validation2> = listOf(),
    val restrictions: List<Restriction2> = listOf(),
    val error: String? = null,
    val hasLostFocus: Boolean = false,
    val isFocused: Boolean = false,
) : State2(),
    PaddingModifier2<TextFieldState2>,
    ShapeModifier2<TextFieldState2>,
    ValidModifier2<TextFieldState2>,
    OnFieldUpdateModifier2 {
    override fun cM(metas: List<Meta2>) = copy(metas = metas)
    override fun c(id: StateId2) = copy(id = id)
    override fun c(padding: PaddingValues2) = copy(padding = padding)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(shape: CornerBasedShape2) = copy(shape = shape)
    override fun c(valid: Valid2) = copy(valid = valid)
    override fun c(weight: Weight2) = copy(weight = weight)
    override fun cD(draws: List<Draw2>) = copy(draws = draws)
    override fun reset(counter: Int) = copy(counter = counter)

    fun isError(text: String): String? = validations.firstOrNull {
        val regexCheck = it.regex?.let { regex -> !Regex(regex).matches(text) } ?: false
        val isRequiredCheck = it.isRequired && text.isEmpty()
        regexCheck || isRequiredCheck
    }?.error

    fun isRestriction(text: String): Boolean = restrictions.any {
        Regex(it.regex).matches(text)
    }
}