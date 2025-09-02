package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.FontWeight2.ExtraBold2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Shapes2.Companion.DefaultShapes2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Shapes2.CornerBasedShape2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2.DefaultSize2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.UIType2.Primary2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.LoadingModifier2.Loading2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ValidModifier2.Valid2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2.Weight2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnClickTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnInitialRenderTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.OnClickModifier2

@Serializable
@SerialName("ButtonState2")
data class ButtonState2(
    override val id: StateId2 = autoStateId2(),
    override val padding: PaddingValues2 = PaddingValues2(),
    override val visible: Visible2 = Visible2(true),
    override val size: Size2 = DefaultSize2,
    override val uiType: UIType2 = Primary2,
    override val loading: Loading2 = Loading2(false),
    override val shape: CornerBasedShape2 = DefaultShapes2.small,
    override val valid: Valid2 = Valid2(true),
    override val textStyle: TextStyle2 = TextStyle2.BodyMedium2,
    override val fontWeight: FontWeight2 = ExtraBold2,
    override val weight: Weight2? = null,
    override val onClickTrigger: OnClickTrigger2 = OnClickTrigger2(),
    override val onInitialRenderTrigger: OnInitialRenderTrigger2 = OnInitialRenderTrigger2(),
    override val metas: List<Meta2> = listOf(),
    override val counter: Int = 0,
    val leadingIcon: ImageState2? = null,
    val text: String? = null,
) : State2(),
    UITypeModifier2<ButtonState2>,
    PaddingModifier2<ButtonState2>,
    LoadingModifier2<ButtonState2>,
    ShapeModifier2<ButtonState2>,
    ValidModifier2<ButtonState2>,
    TextStyleModifier2<ButtonState2>,
    FontWeightModifier2<ButtonState2>,
    OnClickModifier2 {
    override fun cM(metas: List<Meta2>) = copy(metas = metas)
    override fun c(id: StateId2) = copy(id = id)
    override fun c(padding: PaddingValues2) = copy(padding = padding)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(shape: CornerBasedShape2) = copy(shape = shape)
    override fun c(valid: Valid2) = copy(valid = valid)
    override fun c(type: UIType2) = copy(uiType = type)
    override fun c(textStyle: TextStyle2) = copy(textStyle = textStyle)
    override fun c(value: FontWeight2) = copy(fontWeight = value)
    override fun c(weight: Weight2) = copy(weight = weight)
    override fun c(loading: Loading2) = copy(loading = loading)
    override fun reset(counter: Int) = copy(counter = counter)
}