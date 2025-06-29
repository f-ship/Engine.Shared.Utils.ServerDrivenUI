package ship.f.engine.shared.utils.serverdrivenui

import kotlinx.datetime.Clock.System.now
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.ID
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.Meta.None
import ship.f.engine.shared.utils.serverdrivenui.action.Action
import ship.f.engine.shared.utils.serverdrivenui.action.RemoteAction
import ship.f.engine.shared.utils.serverdrivenui.state.*

fun measureInMillis(id: Any, block: () -> Unit) {
    val start = now()

    block()

    val end = now()
    val elapsed = end - start
    println("$id: Took ${elapsed.inWholeMilliseconds} ms")
}

object WidgetStateSerializer : JsonContentPolymorphicSerializer<WidgetState>(WidgetState::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<WidgetState> {
        val discriminator = element.jsonObject["type"]?.jsonPrimitive?.content
        return when (discriminator) {
            CardState::class.simpleName -> CardState.serializer()
            BottomSheetState::class.simpleName -> BottomSheetState.serializer()
            RowState::class.simpleName -> RowState.serializer()
            ColumnState::class.simpleName -> ColumnState.serializer()
            FlexRowState::class.simpleName -> FlexRowState.serializer()
            FlexColumnState::class.simpleName -> FlexColumnState.serializer()
            GridState::class.simpleName -> GridState.serializer()
            FlexGridState::class.simpleName -> FlexGridState.serializer()
            else -> UnknownWidgetState.serializer()
        }
    }
}

object ComponentStateSerializer :
    JsonContentPolymorphicSerializer<ComponentState>(ComponentState::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ComponentState> {
        val discriminator = element.jsonObject["type"]?.jsonPrimitive?.content
        return when (discriminator) {
            SpaceState::class.simpleName -> SpaceState.serializer()
            TextState::class.simpleName -> TextState.serializer()
            FieldState::class.simpleName -> FieldState.serializer()
            ToggleState::class.simpleName -> ToggleState.serializer()
            DropDownState::class.simpleName -> DropDownState.serializer()
            RadioListState::class.simpleName -> RadioListState.serializer()
            TickListState::class.simpleName -> TickListState.serializer()
            SearchState::class.simpleName -> SearchState.serializer()
            MenuState::class.simpleName -> MenuState.serializer()
            BottomRowState::class.simpleName -> BottomRowState.serializer()
            ImageState::class.simpleName -> ImageState.serializer()
            VideoState::class.simpleName -> VideoState.serializer()
            CustomState::class.simpleName -> CustomState.serializer()
            ButtonState::class.simpleName -> ButtonState.serializer()
            IconState::class.simpleName -> IconState.serializer()
            LoadingShimmerState::class.simpleName -> LoadingShimmerState.serializer()
            DialogState::class.simpleName -> DialogState.serializer()
            SnackBarState::class.simpleName -> SnackBarState.serializer()
            LoaderState::class.simpleName -> LoaderState.serializer()
            else -> UnknownComponentState.serializer()
        }
    }
}

var count = 0
fun auto() = ID(id = "Auto-${count++}", scope = "")
fun id(value: String) = ID(id = value, scope = "")

@Serializable
@SerialName("SConfig")
data class ScreenConfig(
    val id: ID = auto(),
    val lightColorScheme: ColorSchemeState? = null,
    val darkColorScheme: ColorSchemeState? = null,
    val state: List<Widget> = emptyList(),
) {
    @Serializable
    @SerialName("ID")
    data class ID(val id: String, val scope: String)

    @Serializable
    @SerialName("Element")
    sealed class Element {
        abstract val id: ID
        abstract val state: State
        abstract val fallback: Fallback

        abstract val triggerActions: List<TriggerAction>
        abstract val listeners: List<RemoteAction>
    }

    @Serializable
    sealed class Meta {
        @Serializable
        @SerialName("None")
        data object None : Meta()
    }

    @Serializable
    sealed class TriggerAction {
        abstract val meta: Meta
        abstract val action: Action

        @Serializable
        @SerialName("OnClickTrigger")
        data class OnClickTrigger(
            override val meta: Meta = None,
            override val action: Action,
        ) : TriggerAction()

        @Serializable
        @SerialName("OnHoldTrigger")
        data class OnHoldTrigger(
            override val meta: Meta = None,
            override val action: Action,
        ) : TriggerAction()

        @Serializable
        @SerialName("OnPressTrigger") // TODO what is the difference between this and OnClickTrigger
        data class OnPressTrigger(
            override val meta: Meta = None,
            override val action: Action,
        ) : TriggerAction()

        @Serializable
        @SerialName("OnFieldUpdate")
        data class OnFieldUpdateTrigger(
            override val meta: Meta = None,
            override val action: Action,
        ) : TriggerAction()

        @Serializable
        @SerialName("OnToggleUpdateTrigger")
        data class OnToggleUpdateTrigger(
            override val meta: Meta = None,
            override val action: Action,
        ) : TriggerAction()

        @Serializable
        @SerialName("OnStateUpdate")
        data class OnStateUpdateTrigger(
            override val meta: Meta = None,
            override val action: Action,
        ) : TriggerAction()
    }

    @Serializable
    @SerialName("Widget")
    data class Widget(
        override val id: ID = auto(),
        override val state: WidgetState,
        override val fallback: Fallback = Fallback.Hide,
        override val triggerActions: List<TriggerAction> = emptyList(),
        override val listeners: List<RemoteAction> = emptyList(),
    ) : Element()

    @Serializable
    @SerialName("Component")
    data class Component(
        override val id: ID = auto(),
        override val state: ComponentState,
        override val fallback: Fallback = Fallback.Hide,
        override val triggerActions: List<TriggerAction> = emptyList(),
        override val listeners: List<RemoteAction> = emptyList(),
    ) : Element()

    @Serializable
    @SerialName("Fallback")
    sealed class Fallback {
        data class UI(
            val id: ID,
            val state: State,
        ) : Fallback()

        @Serializable
        @SerialName("Hide")
        data object Hide : Fallback()

        @Serializable
        @SerialName("OptionalUpdate")
        data object OptionalUpdate : Fallback()

        @Serializable
        @SerialName("RequireUpdate")
        data object RequireUpdate : Fallback()
    }
}
