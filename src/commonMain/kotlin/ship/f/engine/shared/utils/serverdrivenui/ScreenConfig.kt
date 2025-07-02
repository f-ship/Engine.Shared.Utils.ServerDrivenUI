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

//private object WidgetListSerializer : KSerializer<List<ScreenConfig.Widget<WidgetState>>> {
//    private val elementSerializer = ElementSerializer(WidgetStateSerializer)
//    private val listSerializer = ListSerializer(elementSerializer)
//
//    override val descriptor = listSerializer.descriptor
//    override fun serialize(encoder: Encoder, value: List<ScreenConfig.Widget<WidgetState>>) =
//        listSerializer.serialize(encoder, value)
//    override fun deserialize(decoder: Decoder): List<ScreenConfig.Widget<WidgetState>> =
//        listSerializer.deserialize(decoder) as List<ScreenConfig.Widget<WidgetState>>
//}
//
//class ElementSerializer<T : State>(
//    private val dataSerializer: KSerializer<T>,
//) : KSerializer<Element<T>> {
//    override val descriptor = buildClassSerialDescriptor("Element") {
//        element("id", ID.serializer().descriptor)
//        element("state", dataSerializer.descriptor)
//        element("fallback", ScreenConfig.Fallback.serializer().descriptor)
//        element("triggerActions", ListSerializer(ScreenConfig.TriggerAction.serializer()).descriptor)
//        element("listeners", ListSerializer(RemoteAction.serializer()).descriptor)
//    }
//
//
//    override fun serialize(encoder: Encoder, value: Element<T>) {
//        val composite = encoder.beginStructure(descriptor)
//
//        composite.encodeSerializableElement(descriptor, 0, ID.serializer(), value.id)
//        composite.encodeSerializableElement(descriptor, 1, dataSerializer, value.state)
//        composite.encodeSerializableElement(descriptor, 2, ScreenConfig.Fallback.serializer(), value.fallback)
//        composite.encodeSerializableElement(
//            descriptor,
//            3,
//            ListSerializer(ScreenConfig.TriggerAction.serializer()),
//            value.triggerActions
//        )
//        composite.encodeSerializableElement(
//            descriptor,
//            4,
//            ListSerializer(RemoteAction.serializer()),
//            value.listeners
//        )
//
//        composite.endStructure(descriptor)
//    }
//
//    override fun deserialize(decoder: Decoder): Element<T> {
//        val composite = decoder.beginStructure(descriptor)
//
//        val id = composite.decodeSerializableElement(descriptor, 0, ID.serializer())
//        val state = composite.decodeSerializableElement(descriptor, 1, dataSerializer)
//        val fallback = composite.decodeSerializableElement(descriptor, 2, ScreenConfig.Fallback.serializer())
//        val triggerActions = composite.decodeSerializableElement(
//            descriptor,
//            3,
//            ListSerializer(ScreenConfig.TriggerAction.serializer())
//        )
//        val listeners = composite.decodeSerializableElement(
//            descriptor,
//            4,
//            ListSerializer(RemoteAction.serializer())
//        )
//
//        composite.endStructure(descriptor)
//
//         // Since Element is sealed and has two implementations (Widget and Component),
//         // we need to determine which one to create based on the state type
//        return when (state) {
//            is WidgetState -> ScreenConfig.Widget(
//                id = id,
//                state = state as WidgetState,
//                fallback = fallback,
//                triggerActions = triggerActions,
//                listeners = listeners
//            )
//            is ComponentState -> ScreenConfig.Component(
//                id = id,
//                state = state as ComponentState,
//                fallback = fallback,
//                triggerActions = triggerActions,
//                listeners = listeners
//            )
//            else -> throw IllegalStateException("Unknown state type: ${state::class}")
//        } as Element<T>
//    }
//}

object WidgetStateSerializer : JsonContentPolymorphicSerializer<WidgetState>(WidgetState::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<WidgetState> {
        val discriminator = element.jsonObject["type"]?.jsonPrimitive?.content ?: element.jsonObject["type2"]?.jsonPrimitive?.content
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

object ComponentStateSerializer : JsonContentPolymorphicSerializer<ComponentState>(ComponentState::class) {
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

@Serializable()
@SerialName("ScreenConfig")
data class ScreenConfig(
    val id: ID = auto(),
    val lightColorScheme: ColorSchemeState? = null,
    val darkColorScheme: ColorSchemeState? = null,
    val state: List<Element<out State>> = emptyList(),
) {
    @Serializable
    @SerialName("ID")
    data class ID(val id: String, val scope: String)

    @Serializable()
    @SerialName("Element")
    sealed class Element<S : State> {
        abstract val id: ID
        abstract val state: S
        abstract val fallback: Fallback

        abstract val triggerActions: List<TriggerAction>
        abstract val listeners: List<RemoteAction>
    }

    @Serializable
    @SerialName("Meta")
    sealed class Meta {
        @Serializable
        @SerialName("None")
        data object None : Meta()
    }

    @Serializable
    @SerialName("TriggerAction")
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
    data class Widget<S : WidgetState>(
        override val id: ID = auto(),
        override val state: S,
        override val fallback: Fallback = Fallback.Hide,
        override val triggerActions: List<TriggerAction> = emptyList(),
        override val listeners: List<RemoteAction> = emptyList(),
    ) : Element<WidgetState>()

    @Serializable
    @SerialName("Component")
    data class Component<S : ComponentState>(
        override val id: ID = auto(),
        override val state: S,
        override val fallback: Fallback = Fallback.Hide,
        override val triggerActions: List<TriggerAction> = emptyList(),
        override val listeners: List<RemoteAction> = emptyList(),
    ) : Element<ComponentState>()

    @Serializable
    @SerialName("Fallback")
    sealed class Fallback {

        @Serializable
        @SerialName("UI")
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
