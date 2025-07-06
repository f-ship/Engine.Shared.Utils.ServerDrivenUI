package ship.f.engine.shared.utils.serverdrivenui.json

import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import ship.f.engine.shared.utils.serverdrivenui.state.*

val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    encodeDefaults = true
    classDiscriminatorMode = ClassDiscriminatorMode.ALL_JSON_OBJECTS

    serializersModule = SerializersModule {
        polymorphic(ComponentState::class) {
            subclass(ImageState::class)
            subclass(SpaceState::class)
            subclass(SpaceState::class)
            subclass(TextState::class)
            subclass(FieldState::class)
            subclass(ToggleState::class)
            subclass(DropDownState::class)
            subclass(RadioListState::class)
            subclass(TickListState::class)
            subclass(SearchState::class)
            subclass(MenuState::class)
            subclass(BottomRowState::class)
            subclass(ImageState::class)
            subclass(VideoState::class)
            subclass(CustomState::class)
            subclass(ButtonState::class)
            subclass(IconState::class)
            subclass(LoadingShimmerState::class)
            subclass(DialogState::class)
            subclass(SnackBarState::class)
            subclass(LoaderState::class)
            subclass(UnknownComponentState::class)
        }

        polymorphic(WidgetState::class) {
            subclass(CardState::class)
            subclass(BottomSheetState::class)
            subclass(RowState::class)
            subclass(ColumnState::class)
            subclass(FlexRowState::class)
            subclass(FlexColumnState::class)
            subclass(GridState::class)
            subclass(FlexGridState::class)
            subclass(UnknownWidgetState::class)
        }
    }
}