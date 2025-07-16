package ship.f.engine.shared.utils.serverdrivenui.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.Element
import ship.f.engine.shared.utils.serverdrivenui.ext.getRandomString
import ship.f.engine.shared.utils.serverdrivenui.json.ComponentStateSerializer
import ship.f.engine.shared.utils.serverdrivenui.json.WidgetStateSerializer

@Serializable
@SerialName("State")
sealed class State {
    val id: String = getRandomString() // Has the change to collide so need to do something smarter
}

@Serializable(with = WidgetStateSerializer::class)
@SerialName("WidgetState")
sealed class WidgetState : State() {
    abstract val children: List<Element<out State>>
    abstract fun copyChildren(children: List<Element<out State>>): WidgetState

    fun spaceState(
        value: Int = 16,
    ) = SpaceState(
        value = value,
    )

    fun textState(
        value: String,
        style: TextState.Style = TextState.Style.BodyMedium,
    ) = TextState(
        value = value,
        style = style,
    )

    fun fieldState(
        initialValue: String = "",
        placeholder: String = "",
        label: String = "",
        value: String = "",
        valid: Boolean? = null,
        fieldType: FieldState.FieldType = FieldState.FieldType.Text,
        validations: List<FieldState.Validation> = listOf(),
        restrictions: List<FieldState.Restriction> = listOf(),
        localState: FieldState.LocalState = FieldState.LocalState(),
    ) = FieldState(
        initialValue = initialValue,
        placeholder = placeholder,
        label = label,
        value = value,
        valid = valid,
        fieldType = fieldType,
        validations = validations,
        restrictions = restrictions,
        localState = localState,
    )

    fun colorSchemeState(
        primary: Long,
        onPrimary: Long,
        onSecondaryContainer: Long,
        secondaryContainer: Long,
        background: Long,
        onBackground: Long,
        surface: Long,
        onSurface: Long,
        surfaceVariant: Long,
        onSurfaceVariant: Long,
        outline: Long,
    ) = ColorSchemeState(
        primary = primary,
        onPrimary = onPrimary,
        onSecondaryContainer = onSecondaryContainer,
        secondaryContainer = secondaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline,
    )

    fun toggleState(
        value: Boolean = false,
        initialState: Boolean? = null,
        valid: Boolean? = null,
    ) = ToggleState(
        value = value,
        initialState = initialState,
        valid = valid,
    )

    fun dropDownState(
        value: String,
    ) = DropDownState(
        value = value,
    )

    fun radioListState(
        value: String = "",
    ) = RadioListState(
        value = value,
    )

    fun tickState(
        value: Boolean = false,
    ) = CheckboxState(
        value = value,
    )

    fun searchState(
        value: String = ""
    ) = SearchState(
        value = value,
    )

    fun menuState(
        value: String = "",
    ) = MenuState(
        value = value,
    )

    fun bottomRowState(
        value: String = "",
    ) = BottomRowState(
        value = value,
    )

    fun imageState(
        src: ImageState.Source,
        accessibilityLabel: String? = null,
    ) = ImageState(
        src = src,
        accessibilityLabel = accessibilityLabel,
    )

    fun videoState(
        value: String,
    ) = VideoState(
        value = value,
    )

    fun customState(
        value: String,
    ) = CustomState(
        value = value,
    )

    fun buttonState(
        value: String,
        buttonType: ButtonState.ButtonType = ButtonState.ButtonType.Primary,
        valid: Boolean? = null,
    ) = ButtonState(
        value = value,
        buttonType = buttonType,
        valid = valid,
    )

    fun iconState(
        value: String,
    ) = IconState(
        value = value,
    )

    fun loadingShimmerState(
        value: String,
    ) = LoadingShimmerState(
        value = value,
    )

    fun dialogState(
        value: String,
    ) = DialogState(
        value = value,
    )

    fun snackBarState(
        value: String,
    ) = SnackBarState(
        value = value,
    )

    fun loaderState(
        value: String,
    ) = LoaderState(
        value = value,
    )

    fun unknownComponentState(
        value: String,
    ) = UnknownComponentState(
        value = value,
    )

    fun cardState(
        children: List<Element<State>> = listOf(),
    ) = CardState(
        children = children,
    )

    fun bottomSheetState(
        children: List<Element<State>> = listOf(),
    ) = BottomSheetState(
        children = children,
    )

    fun rowState(
        arrangement: Arrange = Arrange.Center,
        children: List<Element<State>> = listOf(),
    ) = RowState(
        arrangement = arrangement,
        children = children,
    )

    fun columnState(
        arrangement: Arrange = Arrange.Center,
        children: List<Element<State>> = listOf(),
    ) = ColumnState(
        arrangement = arrangement,
        children = children,
    )

    fun flexRowState(
        children: List<Element<State>> = listOf(),
    ) = FlexRowState(
        children = children,
    )

    fun flexColumnState(
        children: List<Element<State>> = listOf(),
    ) = FlexColumnState(
        children = children,
    )

    fun gridState(
        children: List<Element<State>> = listOf(),
    ) = GridState(
        children = children,
    )

    fun flexGridState(
        children: List<Element<State>> = listOf(),
    ) = FlexGridState(
        children = children,
    )

    fun unknownWidgetState(
        children: List<Element<State>> = listOf(),
    ) = UnknownWidgetState(
        children = children,
    )
}

@Serializable(with = ComponentStateSerializer::class)
@SerialName("ComponentState")
sealed class ComponentState : State()

@Serializable
@SerialName("SpaceState")
data class SpaceState(
    val value: Int = 16,
) : ComponentState()

@Serializable
@SerialName("TextState")
data class TextState(
    override val value: String,
    val style: Style = Style.BodyMedium,
    val color: Long? = null,
    val textAlign: STextAlign = STextAlign.Start,
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

    fun isError(value: String): String? = validations.firstOrNull {
        val regexCheck = !Regex(it.regex).matches(value)
        val isRequiredCheck = it.isRequired && value.isEmpty()
        regexCheck || isRequiredCheck
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
    override val valid: Boolean? = null,
) : ComponentState(), Valid<ToggleState> {
    override fun copyValid(v: Boolean) = copy(valid = v)
}

@Serializable
@SerialName("DropDownState")
data class DropDownState(
    val value: String,
) : ComponentState()

@Serializable
@SerialName("RadioListState")
data class RadioListState(
    val value: String,
) : ComponentState()

@Serializable
@SerialName("TickState")
data class CheckboxState(
    val value: Boolean = false,
    val initialState: Boolean? = null,
    val manualPadding: Boolean = false,
    override val valid: Boolean? = null,
) : ComponentState(), Valid<CheckboxState> {
    override fun copyValid(v: Boolean) = copy(valid = v)
}

@Serializable
@SerialName("SearchState")
data class SearchState(
    val value: String,
) : ComponentState()

@Serializable
@SerialName("MenuState")
data class MenuState(
    val value: String,
) : ComponentState()

@Serializable
@SerialName("BottomRowState")
data class BottomRowState(
    val value: String,
) : ComponentState()

@Serializable
@SerialName("ImageState")
data class ImageState(
    val src: Source,
    val accessibilityLabel: String? = null,
    val size: Size = DefaultSize,
) : ComponentState() {
    @Serializable
    sealed class Source {

        @Serializable
        data class Url(
            val url: String
        ) : Source()

        @Serializable
        data class Local(
            val file: String
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
) : ComponentState()

@Serializable
@SerialName("CustomState")
data class CustomState(
    val value: String,
) : ComponentState()

@Serializable
@SerialName("ButtonState")
data class ButtonState(
    val value: String,
    val buttonType: ButtonType = ButtonType.Primary,
    val leadingIcon: ImageState.Source? = null,
    val size: Size = DefaultSize,
    override val valid: Boolean? = null,
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
) : ComponentState()

@Serializable
@SerialName("LoadingShimmerState")
data class LoadingShimmerState(
    val value: String,
) : ComponentState()

@Serializable
@SerialName("DialogState")
data class DialogState(
    val value: String,
) : ComponentState()

@Serializable
@SerialName("SnackBarState")
data class SnackBarState(
    val value: String,
) : ComponentState()

@Serializable
@SerialName("LoaderState")
data class LoaderState(
    val value: String,
) : ComponentState()

@Serializable
@SerialName("UnknownComponentState")
data class UnknownComponentState(
    val value: String,
) : ComponentState()

@Serializable
@SerialName("CardState")
data class CardState(
    override val children: List<Element<out State>> = listOf(),
) : WidgetState() {
    override fun copyChildren(children: List<Element<out State>>) = copy(children = children)
}

@Serializable
@SerialName("BottomSheetState")
data class BottomSheetState(
    override val children: List<Element<out State>> = listOf(),
) : WidgetState() {
    override fun copyChildren(children: List<Element<out State>>) = copy(children = children)
}

@Serializable
@SerialName("StackState")
data class StackState(
    override val children: List<Element<out State>> = listOf(),
    val alignment: Align = Align.Center,
    val size: Size = DefaultSize,
    val background: Long? = null
) : WidgetState() {
    override fun copyChildren(children: List<Element<out State>>) = copy(children = children)
}

@Serializable
@SerialName("RowState")
data class RowState(
    override val children: List<Element<out State>> = listOf(),
    val arrangement: Arrange = Arrange.Center,
    val size: Size = DefaultSize,
) : WidgetState() {
    override fun copyChildren(children: List<Element<out State>>) = copy(children = children)
}

@Serializable
@SerialName("ColumnState")
data class ColumnState(
    override val children: List<Element<out State>> = listOf(),
    val arrangement: Arrange = Arrange.Center,
) : WidgetState() {
    override fun copyChildren(children: List<Element<out State>>) = copy(children = children)
}

@Serializable
@SerialName("FlexRowState")
data class FlexRowState(
    override val children: List<Element<out State>> = listOf(),
) : WidgetState() {
    override fun copyChildren(children: List<Element<out State>>) = copy(children = children)
}

@Serializable
@SerialName("FlexColumnState")
data class FlexColumnState(
    override val children: List<Element<out State>> = listOf(),
    val arrangement: Arrange = Arrange.Center,
) : WidgetState() {
    override fun copyChildren(children: List<Element<out State>>) = copy(children = children)
}

@Serializable
@SerialName("GridState")
data class GridState(
    override val children: List<Element<out State>> = listOf(),
) : WidgetState() {
    override fun copyChildren(children: List<Element<out State>>) = copy(children = children)
}

@Serializable
@SerialName("FlexGridState")
data class FlexGridState(
    override val children: List<Element<out State>> = listOf(),
) : WidgetState() {
    override fun copyChildren(children: List<Element<out State>>) = copy(children = children)
}

@Serializable
@SerialName("UnknownWidgetState")
data class UnknownWidgetState(
    override val children: List<Element<out State>> = listOf(),
) : WidgetState() {
    override fun copyChildren(children: List<Element<out State>>) = copy(children = children)
}
