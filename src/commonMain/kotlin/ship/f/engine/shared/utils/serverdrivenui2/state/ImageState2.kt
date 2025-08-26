package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.ColorScheme2.Color2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.ColorScheme2.Color2.Unspecified
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.ContentScale2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.ContentScale2.None2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.PaddingValues2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2.DefaultSize2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Source2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ColorModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.PaddingModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2.Weight2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnClickTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnInitialRenderTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.OnClickModifier2

@Serializable
@SerialName("ImageState2")
data class ImageState2(
    override val id: StateId2 = autoStateId2(),
    override val padding: PaddingValues2 = PaddingValues2(),
    override val visible: Visible2 = Visible2(true),
    override val size: Size2 = DefaultSize2,
    override val color: Color2 = Unspecified,
    override val weight: Weight2? = null,
    override val onInitialRenderTrigger: OnInitialRenderTrigger2 = OnInitialRenderTrigger2(),
    override val onClickTrigger: OnClickTrigger2 = OnClickTrigger2(),
    override val metas: Set<Meta2> = setOf(),
    val src: Source2,
    val contentDescription: String? = null,
    val contentScale: ContentScale2 = None2,
) : State2(),
    PaddingModifier2<ImageState2>,
    ColorModifier2<ImageState2>,
    OnClickModifier2 {
    override fun c(metas: Set<Meta2>) = copy(metas = metas)
    override fun c(id: StateId2) = copy(id = id)
    override fun c(padding: PaddingValues2) = copy(padding = padding)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(color: Color2) = copy(color = color)
    override fun c(weight: Weight2) = copy(weight = weight)
}