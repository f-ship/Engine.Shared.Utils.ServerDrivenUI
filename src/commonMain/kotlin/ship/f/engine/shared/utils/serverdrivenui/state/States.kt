package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ComponentStateSerializer
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.Element
import ship.f.engine.shared.utils.serverdrivenui.WidgetStateSerializer

@Serializable
@SerialName("State")
sealed class State

@Serializable(with = WidgetStateSerializer::class)
@SerialName("WidgetState")
sealed class WidgetState : State() {
    abstract val children: List<Element>
}

@Serializable(with = ComponentStateSerializer::class)
@SerialName("ComponentState")
sealed class ComponentState : State()

@Serializable
@SerialName("SpaceState")
data class SpaceState(
    val value: Int = 16,
    val type: String = "SpaceState",
) : ComponentState()

@Serializable
@SerialName("TextState")
data class TextState(
    override val value: String,
    val style: Style = Style.BodyMedium,
    val type: String = "TextState",
) : ComponentState(), Value<TextState> {
    override fun copyValue(v: String) = this.copy(value = v)

    @Serializable
    sealed class Style {
        @Serializable
        data object TitleLarge : Style()
        @Serializable
        data object TitleMedium : Style()
        @Serializable
        data object TitleSmall : Style()
        @Serializable
        data object HeadlineLarge : Style()
        @Serializable
        data object HeadlineMedium : Style()
        @Serializable
        data object HeadlineSmall : Style()
        @Serializable
        data object DisplayLarge : Style()
        @Serializable
        data object DisplayMedium : Style()
        @Serializable
        data object DisplaySmall : Style()
        @Serializable
        data object BodyLarge : Style()
        @Serializable
        data object BodyMedium : Style()
        @Serializable
        data object BodySmall : Style()
        @Serializable
        data object LabelLarge : Style()
        @Serializable
        data object LabelMedium : Style()
        @Serializable
        data object LabelSmall : Style()
    }
}

@Serializable
@SerialName("FieldState")
data class FieldState(
    val initialValue: String = "",
    val placeholder: String = "",
    val label: String = "",
    override val value: String = "",
    override val valid: Boolean? = null,
    val fieldType: FieldType = FieldType.Text,
    val validations: List<Validation> = listOf(),
    val restrictions: List<Restriction> = listOf(),
    val localState: LocalState = LocalState(),
    val type: String = "FieldState",
) : ComponentState(), Value<FieldState>, Valid<FieldState> {
    override fun copyValue(v: String) = this.copy(value = v)
    override fun copyValid(v: Boolean) = this.copy(valid = v)
    @Serializable
    data class LocalState(
        val error: String? = null,
        val hasLostFocus: Boolean = false,
    )

    @Serializable
    data class Validation(
        val regex: String = "",
        val error: String = "Something went wrong",
        val isRequired: Boolean = false,
    )

    @Serializable
    data class Restriction(
        val regex: String = ""
    )

    @Serializable
    sealed class FieldType {
        @Serializable
        data object Text : FieldType()
        @Serializable
        data object Number : FieldType()
        @Serializable
        data object Password : FieldType()
    }

    fun isValid(value: String): String? = validations.firstOrNull {
        !Regex(it.regex).matches(value) || !(it.isRequired && value.isNotEmpty() || !it.isRequired)
    }?.error

    fun isRestriction(value: String): Boolean = restrictions.any {
        Regex(it.regex).matches(value)
    }
}

@Serializable
@SerialName("ThemeState")
data class ColorSchemeState(
    val primary: Long,
    val onPrimary: Long,
    val onSecondaryContainer: Long,
    val secondaryContainer: Long,
    val background: Long,
    val onBackground: Long,
    val surface: Long,
    val onSurface: Long,
    val surfaceVariant: Long,
    val onSurfaceVariant: Long,
    val outline: Long,
)

@Serializable
@SerialName("ToggleState")
data class ToggleState(
    val value: Boolean = false,
    val initialState: Boolean? = null,
    val type: String = "ToggleState",
    override val valid: Boolean? = null,
) : ComponentState(), Valid<ToggleState> {
    override fun copyValid(v: Boolean) = copy(valid = v)
}

@Serializable
@SerialName("DropDownState")
data class DropDownState(
    val value: String,
    val type: String = "DropDownState",
) : ComponentState()

@Serializable
@SerialName("RadioListState")
data class RadioListState(
    val value: String,
    val type: String = "RadioListState",
) : ComponentState()

@Serializable
@SerialName("TickListState")
data class TickListState(
    val value: String,
    val type: String = "TickListState",
) : ComponentState()

@Serializable
@SerialName("SearchState")
data class SearchState(
    val value: String,
    val type: String = "SearchState",
) : ComponentState()

@Serializable
@SerialName("MenuState")
data class MenuState(
    val value: String,
    val type: String = "MenuState",
) : ComponentState()

@Serializable
@SerialName("BottomRowState")
data class BottomRowState(
    val value: String,
    val type: String = "BottomRowState",
) : ComponentState()

@Serializable
@SerialName("ImageState")
data class ImageState(
    val src: Source,
    val accessibilityLabel: String? = null,
    val type: String = "ImageState",
) : ComponentState() {
    @Serializable
    sealed class Source {

        @Serializable
        data class Url(
            val url: String
        ) : Source()

        @Serializable
        data class Resource(
            val resource: String
        ) : Source()
    }
}

@Serializable
@SerialName("VideoState")
data class VideoState(
    val value: String,
    val type: String = "VideoState",
) : ComponentState()

@Serializable
@SerialName("CustomState")
data class CustomState(
    val value: String,
    val type: String = "CustomState",
) : ComponentState()

@Serializable
@SerialName("ButtonState")
data class ButtonState(
    val value: String,
    val buttonType: ButtonType = ButtonType.Primary,
    override val valid: Boolean? = null,
    val type: String = "ButtonState",
) : ComponentState(), Valid<ButtonState> {
    override fun copyValid(v: Boolean) = copy(valid = v)
    @Serializable
    sealed class ButtonType {
        @Serializable
        data object Primary : ButtonType()
        @Serializable
        data object Secondary : ButtonType()
        @Serializable
        data object Tertiary : ButtonType()
    }
}

@Serializable
@SerialName("IconState")
data class IconState(
    val value: String,
    val type: String = "IconState",
) : ComponentState()

@Serializable
@SerialName("LoadingShimmerState")
data class LoadingShimmerState(
    val value: String,
    val type: String = "LoadingShimmerState",
) : ComponentState()

@Serializable
@SerialName("DialogState")
data class DialogState(
    val value: String,
    val type: String = "DialogState",
) : ComponentState()

@Serializable
@SerialName("SnackBarState")
data class SnackBarState(
    val value: String,
    val type: String = "SnackBarState",
) : ComponentState()

@Serializable
@SerialName("LoaderState")
data class LoaderState(
    val value: String,
    val type: String = "LoaderState",
) : ComponentState()

@Serializable
@SerialName("UnknownComponentState")
data class UnknownComponentState(
    val value: String,
    val type: String = "UnknownComponentState",
) : ComponentState()

@Serializable
@SerialName("CardState")
data class CardState(
    override val children: List<Element> = listOf(),
    val type: String = "CardState",
) : WidgetState()

@Serializable
@SerialName("BottomSheetState")
data class BottomSheetState(
    override val children: List<Element> = listOf(),
    val type: String = "BottomSheetState",
) : WidgetState()

@Serializable
@SerialName("RowState")
data class RowState(
    override val children: List<Element> = listOf(),
    val arrangement: Arrangement,
    val type: String = "RowState",
) : WidgetState()

@Serializable
@SerialName("ColumnState")
data class ColumnState(
    override val children: List<Element> = listOf(),
    val arrangement: Arrangement = Arrangement.Center,
    val type: String = "ColumnState",
) : WidgetState()

@Serializable
@SerialName("FlexRowState")
data class FlexRowState(
    override val children: List<Element> = listOf(),
    val type: String = "FlexRowState",
) : WidgetState()

@Serializable
@SerialName("FlexColumnState")
data class FlexColumnState(
    override val children: List<Element> = listOf(),
    val type: String = "FlexColumnState",
) : WidgetState()

@Serializable
@SerialName("GridState")
data class GridState(
    override val children: List<Element> = listOf(),
    val type: String = "GridState",
) : WidgetState()

@Serializable
@SerialName("FlexGridState")
data class FlexGridState(
    override val children: List<Element> = listOf(),
    val type: String = "FlexGridState",
) : WidgetState()

@Serializable
@SerialName("UnknownWidgetState")
data class UnknownWidgetState(
    override val children: List<Element> = listOf(),
    val type: String = "UnknownWidgetState",
) : WidgetState()
