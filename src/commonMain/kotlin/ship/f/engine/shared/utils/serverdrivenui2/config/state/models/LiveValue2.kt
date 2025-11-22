package ship.f.engine.shared.utils.serverdrivenui2.config.state.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.Ref2.VmRef2

@Serializable
@SerialName("LiveValue2")
sealed class LiveValue2 {

    @Serializable
    @SerialName("Ref2")
    sealed class Ref2{
        @Serializable
        @SerialName("StateRef2")
        data class StateRef2(val id: StateId2): Ref2()

        @Serializable
        @SerialName("VmRef2")
        data class VmRef2(val vm: MetaId2, val property: String): Ref2()

        /**
         * For use in children operations when the ZoneViewModel is provided at runtime
         */
        @Serializable
        @SerialName("ZoneRef2")
        data class ZoneRef2(val property: String, val vm: MetaId2 = MetaId2()): Ref2()
    }

    sealed class ReferenceableLiveValue2: LiveValue2() {
        abstract val ref: Ref2
        abstract fun copyRef(ref: Ref2): ReferenceableLiveValue2
    }

    @Serializable
    @SerialName("TextLiveValue2")
    data class TextLiveValue2(override val ref: Ref2): ReferenceableLiveValue2() {
        override fun copyRef(ref: Ref2): TextLiveValue2 {
            return TextLiveValue2(ref)
        }
    }

    @Serializable
    @SerialName("TextStaticValue2")
    data class TextStaticValue2(val value: String): LiveValue2()

    @Serializable
    @SerialName("StaticDrawLiveValue2")
    data class StaticDrawLiveValue2(val value: Draw2): LiveValue2()

    @Serializable
    @SerialName("IntLiveValue2")
    data class IntLiveValue2(override val ref: Ref2): ReferenceableLiveValue2() {
        override fun copyRef(ref: Ref2): IntLiveValue2 {
            return IntLiveValue2(ref)
        }
    }

    @Serializable
    @SerialName("InstantNowLiveValue2")
    data class InstantNowLiveValue2(val refreshSeconds: Int? = null): LiveValue2()

    @Serializable
    @SerialName("StaticIntLiveValue2")
    data class StaticIntLiveValue2(val value: Int): LiveValue2()

    @Serializable
    @SerialName("BooleanLiveValue2")
    data class BooleanLiveValue2(override val ref: Ref2): ReferenceableLiveValue2() {
        override fun copyRef(ref: Ref2): BooleanLiveValue2 {
            return BooleanLiveValue2(ref)
        }
    }

    @Serializable
    @SerialName("MultiLiveValue2")
    data class MultiLiveValue2(override val ref: VmRef2): ReferenceableLiveValue2() {
        override fun copyRef(ref: Ref2): MultiLiveValue2 {
            return MultiLiveValue2(ref as VmRef2)
        }
    }

    /**
     * Returns either true or false based on the evaluation of value1 and value2 with the condition
     */
    @Serializable
    @SerialName("ConditionalLiveValue2")
    data class ConditionalLiveValue2(
        val value1: LiveValue2,
        val condition: Condition2,
        val value2: LiveValue2,
    )

    /**
     * Returns either trueBranch or falseBranch based on the evaluation of value1 and value2 with the condition
     */
    @Serializable
    @SerialName("ConditionalBranchLiveValue2")
    data class ConditionalBranchLiveValue2(
        val value1: LiveValue2,
        val condition: Condition2,
        val value2: LiveValue2,
        val trueBranch: LiveValue2,
        val falseBranch: LiveValue2,
    ): LiveValue2()

    @Serializable
    @SerialName("Condition2")
    sealed class Condition2 {
        @Serializable
        @SerialName("Eq")
        data object Eq : Condition2()

        @Serializable
        @SerialName("NotEq")
        data object NotEq : Condition2()

        @Serializable
        @SerialName("And")
        data object And : Condition2()

        @Serializable
        @SerialName("Or")
        data object Or : Condition2()

        @Serializable
        @SerialName("Not")
        data object Not : Condition2()

        @Serializable
        @SerialName("GreaterThan")
        data object GreaterThan : Condition2()

        @Serializable
        @SerialName("LessThan")
        data object LessThan : Condition2()

        @Serializable
        @SerialName("In")
        data object In : Condition2()

        @Serializable
        @SerialName("InOrEmpty")
        data object InOrEmpty : Condition2()

        @Serializable
        @SerialName("NotIn")
        data object NotIn : Condition2()

        @Serializable
        @SerialName("Mod")
        data object Mod : Condition2()
    }
}