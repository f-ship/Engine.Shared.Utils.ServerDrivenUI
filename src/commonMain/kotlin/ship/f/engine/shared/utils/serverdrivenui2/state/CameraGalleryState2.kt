package ship.f.engine.shared.utils.serverdrivenui2.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client3.Path3
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.Meta2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.ContentScale2.None2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2.Companion.autoStateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Size2.DefaultSize2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Color2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Color2.Unspecified
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.ConditionalValue
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.Draw2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ByteModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ColorModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.PaddingModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ShapeModifier2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.VisibilityModifier2.Visible2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.WeightModifier2.Weight2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnBuildCompleteTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnClickTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.models.OnInitialRenderTrigger2
import ship.f.engine.shared.utils.serverdrivenui2.config.trigger.modifiers.OnClickModifier2

@Serializable
@SerialName("CameraGalleryState2")
data class CameraGalleryState2(
    override val id: StateId2 = autoStateId2(),
    override val padding: PaddingValues2 = PaddingValues2(),
    override val visible: Visible2 = Visible2(true),
    override val size: Size2 = DefaultSize2,
    override val color: Color2 = Unspecified,
    override val liveColor: ConditionalValue? = null,
    override val weight: Weight2? = null,
    override val onInitialRenderTrigger: OnInitialRenderTrigger2 = OnInitialRenderTrigger2(),
    override val onClickTrigger: OnClickTrigger2 = OnClickTrigger2(),
    override val metas: List<Meta2> = listOf(),
    override val counter: Int = 0,
    val defaultImage: ImageState2? = null,
    val contentDescription: String? = null,
    val contentScale: ContentScale2 = None2,
    override val bytes: ByteArray = byteArrayOf(),
    override val onBuildCompleteTrigger2: OnBuildCompleteTrigger2 = OnBuildCompleteTrigger2(),
    override val path3: Path3 = Path3.Init,
    override val path: Path2 = Path2(),
    override val draws: List<Draw2> = listOf(),
    override val shape: Shapes2.CornerBasedShape2 = Shapes2.DefaultShapes2.none,
) : State2(),
    PaddingModifier2<CameraGalleryState2>,
    ColorModifier2<CameraGalleryState2>,
    ShapeModifier2<CameraGalleryState2>,
    ByteModifier2<CameraGalleryState2>,
    OnClickModifier2 {
    override fun cM(metas: List<Meta2>) = copy(metas = metas)
    override fun c(id: StateId2) = copy(id = id)
    override fun c(padding: PaddingValues2) = copy(padding = padding)
    override fun c(visible: Visible2) = copy(visible = visible)
    override fun c(size: Size2) = copy(size = size)
    override fun c(color: Color2) = copy(color = color)
    override fun c(weight: Weight2) = copy(weight = weight)
    override fun cD(draws: List<Draw2>) = copy(draws = draws)
    override fun c(path3: Path3) = copy(path3 = path3)
    override fun c(path: Path2) = copy(path = path)
    override fun reset(counter: Int) = copy(counter = counter)
    override fun c(shape: Shapes2.CornerBasedShape2) = copy(shape = shape)
    override fun c(bytes: ByteArray) = copy(bytes = bytes)
    override fun liveColor(liveColor: ConditionalValue) = copy(liveColor = liveColor)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CameraGalleryState2

        if (counter != other.counter) return false
        if (id != other.id) return false
        if (padding != other.padding) return false
        if (visible != other.visible) return false
        if (size != other.size) return false
        if (color != other.color) return false
        if (weight != other.weight) return false
        if (onInitialRenderTrigger != other.onInitialRenderTrigger) return false
        if (onClickTrigger != other.onClickTrigger) return false
        if (metas != other.metas) return false
        if (defaultImage != other.defaultImage) return false
        if (contentDescription != other.contentDescription) return false
        if (contentScale != other.contentScale) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (onBuildCompleteTrigger2 != other.onBuildCompleteTrigger2) return false
        if (path3 != other.path3) return false
        if (path != other.path) return false
        if (draws != other.draws) return false
        if (shape != other.shape) return false

        return true
    }

    override fun hashCode(): Int {
        var result = counter
        result = 31 * result + id.hashCode()
        result = 31 * result + padding.hashCode()
        result = 31 * result + visible.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + (weight?.hashCode() ?: 0)
        result = 31 * result + onInitialRenderTrigger.hashCode()
        result = 31 * result + onClickTrigger.hashCode()
        result = 31 * result + metas.hashCode()
        result = 31 * result + (defaultImage?.hashCode() ?: 0)
        result = 31 * result + (contentDescription?.hashCode() ?: 0)
        result = 31 * result + contentScale.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + onBuildCompleteTrigger2.hashCode()
        result = 31 * result + path3.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + draws.hashCode()
        result = 31 * result + shape.hashCode()
        return result
    }
}