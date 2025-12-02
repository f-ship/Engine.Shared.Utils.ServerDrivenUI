package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client3.Path3
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.ColorScheme2.Color2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.ColorScheme2.Color2.Unspecified
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.FontWeight2.Normal2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2.DefaultSize2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.TextStyle2.BodyMedium2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Draw2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2.Weight2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnBuildCompleteTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnInitialRenderTrigger2

@Serializable
@SerialName("TextState2")
data class TextState2(
    override val id: StateId2 = autoStateId2(),
    override val padding: PaddingValues2 = PaddingValues2(),
    override val visible: Visible2 = Visible2(true),
    override val size: Size2 = DefaultSize2,
    override val textStyle: TextStyle2 = BodyMedium2,
    override val color: Color2 = Unspecified,
    override val fontWeight: FontWeight2 = Normal2,
    override val weight: Weight2? = null,
    override val onInitialRenderTrigger: OnInitialRenderTrigger2 = OnInitialRenderTrigger2(),
    override val metas: List<Meta2> = listOf(),
    override val counter: Int = 0,
    override val draws: List<Draw2> = listOf(),
    override val path3: Path3 = Path3.Init,
    override val path: Path2 = Path2(),
    override val onBuildCompleteTrigger2: OnBuildCompleteTrigger2 = OnBuildCompleteTrigger2(),
    val textAlign: TextAlign2 = TextAlign2.Start2,
    val underline: Boolean = false,
    override val text: String,
    override val liveText: LiveValue2.TextLiveValue2? = null,
) : State2(),
    TextStyleModifier2<TextState2>,
    TextModifier2<TextState2>,
    ColorModifier2<TextState2>,
    PaddingModifier2<TextState2>,
    FontWeightModifier2<TextState2> {
    override fun cM(metas: List<Meta2>) = copy(metas = metas)
    override fun c(id: StateId2) = copy(id = id)
    override fun c(padding: PaddingValues2) = copy(padding = padding)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(textStyle: TextStyle2) = copy(textStyle = textStyle)
    override fun c(color: Color2) = copy(color = color)
    override fun c(value: FontWeight2) = copy(fontWeight = value)
    override fun c(weight: Weight2) = copy(weight = weight)
    override fun c(path3: Path3) = copy(path3 = path3)
    override fun c(path: Path2) = copy(path = path)
    override fun cD(draws: List<Draw2>) = copy(draws = draws)
    override fun reset(counter: Int) = copy(counter = counter)
    override fun text(text: String) = copy(text = text)
    override fun liveText(liveText: LiveValue2.TextLiveValue2) = copy(liveText = liveText)
}