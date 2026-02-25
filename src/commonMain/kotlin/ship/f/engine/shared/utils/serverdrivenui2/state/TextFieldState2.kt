package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client3.Path3
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Shapes2.CornerBasedShape2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2.DefaultSize2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.LiveValue3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Color2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Draw2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.PaddingModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ShapeModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.TextModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ValidModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ValidModifier2.Valid2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2.Weight2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnBuildCompleteTrigger2
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
    override val path3: Path3 = Path3.Init,
    override val path: Path2 = Path2(),
    override val draws: List<Draw2> = listOf(),
    override val onBuildCompleteTrigger2: OnBuildCompleteTrigger2 = OnBuildCompleteTrigger2(),
    val focusedTextColor: Color2 = Color2.Unspecified,
    val textColor: Color2 = Color2.Unspecified,
    val borderColor: Color2 = Color2.Unspecified,
    val containerColor: Color2 = Color2.Unspecified,
    val textStyle: TextStyle2 = TextStyle2.BodyMedium2,
    val placeholderColor: Color2 = Color2.Unspecified,
    val placeholderTextStyle: TextStyle2 = TextStyle2.BodyMedium2,
    val imeType: IMEType2 = IMEType2.Default2,
    val leadingIcon: ImageState2? = null,
    val trailingIcon: ImageState2? = null,
    val initialValue: String = "",
    val placeholder: String = "",
    val label: String = "",
    val labelTextStyle: TextStyle2 = TextStyle2.BodyMedium2,
    val labelColor: Color2 = Color2.Unspecified,
    override val text: String = "",
    val fieldType: FieldType2 = FieldType2.Text,
    val validations: List<Validation2> = listOf(), // TODO need to uplift this as it's not looking so good
    val restrictions: List<Restriction2> = listOf(),
    val error: String? = null,
    val hasLostFocus: Boolean = false,
    val isFocused: Boolean = false,
    override val liveText3: LiveValue3? = null,
) : State2(),
    PaddingModifier2<TextFieldState2>,
    ShapeModifier2<TextFieldState2>,
    ValidModifier2<TextFieldState2>,
    TextModifier2<TextFieldState2>,
    OnFieldUpdateModifier2 {
    override fun cM(metas: List<Meta2>) = copy(metas = metas)
    override fun c(id: StateId2) = copy(id = id)
    override fun c(padding: PaddingValues2) = copy(padding = padding)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(shape: CornerBasedShape2) = copy(shape = shape)
    override fun c(valid: Valid2) = copy(valid = valid)
    override fun c(weight: Weight2) = copy(weight = weight)
    override fun c(path3: Path3) = copy(path3 = path3)
    override fun c(path: Path2) = copy(path = path)
    override fun cD(draws: List<Draw2>) = copy(draws = draws)
    override fun reset(counter: Int) = copy(counter = counter)
    override fun text(text: String) = copy(text = text)
    override fun liveText3(liveText3: LiveValue3) = copy(liveText3 = liveText3)
    fun isError(text: String): String? = validations.firstOrNull {
        val regexCheck = it.regex?.let { regex -> !Regex(regex).matches(text) } ?: false
        val isRequiredCheck = it.isRequired && text.isEmpty()
        regexCheck || isRequiredCheck
    }?.error

    fun isRestriction(text: String): Boolean = restrictions.any {
        Regex(it.regex).matches(text)
    }


}