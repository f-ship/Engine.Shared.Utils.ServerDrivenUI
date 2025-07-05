package ship.f.engine.shared.utils.serverdrivenui.json

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ship.f.engine.shared.utils.serverdrivenui.state.*

/**
 * Custom serializer for the WidgetState class makes it possible to gracefully handle unknown types.
 */
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

/**
 * Custom serializer for the ComponentState class makes it possible to gracefully handle unknown types.
 */
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
