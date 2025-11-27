package ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Condition3")
sealed class Condition3 {
    @Serializable
    @SerialName("Eq")
    data object Eq : Condition3()

    @Serializable
    @SerialName("NotEq")
    data object NotEq : Condition3()

    @Serializable
    @SerialName("And")
    data object And : Condition3()

    @Serializable
    @SerialName("Or")
    data object Or : Condition3()

    @Serializable
    @SerialName("Not")
    data object Not : Condition3()

    @Serializable
    @SerialName("GreaterThan")
    data object GreaterThan : Condition3()

    @Serializable
    @SerialName("LessThan")
    data object LessThan : Condition3()

    @Serializable
    @SerialName("In")
    data object In : Condition3()

    @Serializable
    @SerialName("InOrEmpty")
    data object InOrEmpty : Condition3()

    @Serializable
    @SerialName("NotIn")
    data object NotIn : Condition3()

    @Serializable
    @SerialName("Mod")
    data object Mod : Condition3()

    @Serializable
    @SerialName("Any")
    data object Any : Condition3()

    @Serializable
    @SerialName("None")
    data object None : Condition3()

    @Serializable
    @SerialName("All")
    data object All : Condition3()
}