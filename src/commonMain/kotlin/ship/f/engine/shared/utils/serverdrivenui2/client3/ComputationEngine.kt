package ship.f.engine.shared.utils.serverdrivenui2.client3

import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.ZoneViewModel2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.ZoneViewModel2.Property.StringProperty
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.ZoneViewModel3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.StateId2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.Ref2.VmRef2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.Ref2.ZoneRef2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.Condition3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.LiveValue3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.Ref3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.Ref3.VmRef3
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.computation.value.*
import ship.f.engine.shared.utils.serverdrivenui2.config.state.modifiers.ChildrenModifier2
import ship.f.engine.shared.utils.serverdrivenui2.ext.sduiLog
import ship.f.engine.shared.utils.serverdrivenui2.state.State2
import ship.f.engine.shared.utils.serverdrivenui2.state.TextState2

class ComputationEngine(val client: Client3) {

    data class ZoneWrapper(
        val viewModel: ZoneViewModel3,
        val state: State2,
    )

    data class ChildOrderWrapper(
        val order: IntValue,
        val state: State2,
    )

    data class FilterWrapper(
        val filter: BooleanValue,
        val state: State2,
    )

    data class JumpToWrapper(
        val jumpTo: BooleanValue,
        val state: State2,
        val viewModel: ZoneViewModel3,
    )

    data class JobWrapper(val stateId: StateId2, val refreshMillis: Int)

    data class Timer(val client: Client3) {
        val map = mutableMapOf<JobWrapper, Job>()
        fun add(state: State2, refreshMillis: Int) {
            val jobWrapper = JobWrapper(state.id, refreshMillis)
            if (map.containsKey(jobWrapper)) return
            client.update(state)
            map[jobWrapper] = createTimer(refreshMillis) {
                sduiLog("timer $jobWrapper", tag = "timer")
                val updatedState = client.get<State2>(state.id)
                (updatedState as? ChildrenModifier2<*>)?.children?.forEach { child ->
                    client.update(client.get<State2>(child.id).reset())
                }
                client.update(updatedState.reset())
                client.commit()
                true
            }
        }

        fun createTimer(intervalMillis: Int, func: () -> Boolean): Job {
            val job = CoroutineScope(Dispatchers.Default).launch {
                while (true) {
                    delay(intervalMillis.toLong())
                    if (!func()) {
                        cancel()
                    }
                }
            }
            return job
        }
    }

    val start = Clock.System.now().epochSeconds.toInt()
    val debugSpeed = 10
    fun getNow(): Int = (Clock.System.now().epochSeconds.toInt() - start) * debugSpeed + Clock.System.now().epochSeconds.toInt()

    val timer = Timer(client)

    inline fun <reified T : LiveValue2> computeConditionalBranchLive(liveValue2: LiveValue2.ConditionalBranchLiveValue2): T {
        return when (liveValue2.value1) {
            is LiveValue2.IntLiveValue2 -> when (liveValue2.value2) {
                is LiveValue2.IntLiveValue2 -> {
                    val prop1 = when (liveValue2.value1.ref) {
                        is ZoneRef2 -> (client.viewModels[liveValue2.value1.ref.vm] as? ZoneViewModel2)?.let {
                            it.map[liveValue2.value1.ref.property]
                                ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for ${liveValue2.value1.ref.property}")
                        }
                            ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for ${liveValue2.value1.ref.property}")

                        else -> TODO()
                    }

                    val prop2 = when (liveValue2.value2.ref) {
                        is ZoneRef2 -> (client.viewModels[liveValue2.value2.ref.vm] as? ZoneViewModel2)?.let {
                            it.map[liveValue2.value2.ref.property]
                                ?: error("No value found for ref: ${liveValue2.value2.ref} in ${liveValue2.value2.ref.vm} for1 ${liveValue2.value2.ref.property}")
                        }
                            ?: error("No value found for ref: ${liveValue2.value2.ref} in ${liveValue2.value2.ref.vm} for ${liveValue2.value2.ref.property}")

                        else -> TODO()
                    }

                    val bool = when (liveValue2.condition) {
                        LiveValue2.Condition2.Eq -> prop1 == prop2
                        else -> TODO()
                    }

                    if (bool) {
                        liveValue2.trueBranch as T
                    } else {
                        liveValue2.falseBranch as T
                    }
                }

                is LiveValue2.StaticIntLiveValue2 -> {
                    val prop1 = when (liveValue2.value1.ref) {
                        is ZoneRef2 -> (client.viewModels[liveValue2.value1.ref.vm] as? ZoneViewModel2)?.let {
                            it.map[liveValue2.value1.ref.property]
                                ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for2 ${liveValue2.value1.ref.property}")
                        }
                            ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for3 ${liveValue2.value1.ref.property}")

                        else -> TODO()
                    }

                    val bool = when (liveValue2.condition) {
                        LiveValue2.Condition2.Eq -> when (prop1) {
                            is ZoneViewModel2.Property.IntProperty -> prop1.value == liveValue2.value2.value
                            else -> false
                        }

                        LiveValue2.Condition2.Mod -> when (prop1) {
                            is ZoneViewModel2.Property.IntProperty -> (prop1.value % liveValue2.value2.value) == 0
                            else -> false
                        }

                        else -> TODO()
                    }

                    if (bool) {
                        liveValue2.trueBranch as T
                    } else {
                        liveValue2.falseBranch as T
                    }
                }

                else -> TODO()
            }

            else -> TODO()
        }
    }

    fun computeConditionalLive(liveValue2: LiveValue2.ConditionalLiveValue2): Boolean {
        return when (liveValue2.value1) {
            is LiveValue2.TextLiveValue2 -> when (liveValue2.value2) {
                is LiveValue2.MultiLiveValue2 -> {
                    val vm = client.get(liveValue2.value2.ref.vm) as? ZoneViewModel2
                        ?: error("No vm found for id: ${liveValue2.value2.ref.vm}")
                    val multiProperty = vm.map[liveValue2.value2.ref.property] as? ZoneViewModel2.Property.MultiProperty
                        ?: error("No multi property found for ref: ${liveValue2.value2.ref} in ${liveValue2.value2.ref.vm}")
                    val prop = when (liveValue2.value1.ref) {
                        is ZoneRef2 -> StringProperty(
                            value = (client.viewModels[liveValue2.value1.ref.vm] as? ZoneViewModel2)
                                ?.let { it.map[liveValue2.value1.ref.property] as? StringProperty }
                                ?.value
                                ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for ${liveValue2.value1.ref.property}"),
                        )

                        else -> TODO()
                    }
                    when (liveValue2.condition) {
                        is LiveValue2.Condition2.InOrEmpty -> multiProperty.value.isEmpty() || multiProperty.value.contains(
                            prop
                        )

                        else -> TODO()
                    }
                }

                else -> TODO("Only handling TextLiveValue2 > MultiLiveValue2 for now")
            }

            is LiveValue2.IntLiveValue2 -> when (liveValue2.value2) {
                is LiveValue2.IntLiveValue2 -> {
                    val value1 = when (val ref = liveValue2.value1.ref) {
                        is ZoneRef2 -> ((client.get(ref.vm) as? ZoneViewModel2 ?: error("no vm ${ref.vm}"))
                            .map[ref.property] as? ZoneViewModel2.Property.IntProperty)?.value

                        else -> TODO()
                    }

                    val value2 = when (val ref = liveValue2.value2.ref) {
                        is ZoneRef2 -> ((client.get(ref.vm) as? ZoneViewModel2 ?: error("no vm ${ref.vm}"))
                            .map[ref.property] as? ZoneViewModel2.Property.IntProperty)?.value.also {
                            sduiLog(
                                client.get(
                                    ref.vm
                                ), ref, tag = "wtf"
                            )
                        }

                        else -> TODO()
                    }

                    when (liveValue2.condition) {
                        is LiveValue2.Condition2.GreaterThan -> value1!! > value2!!
                        is LiveValue2.Condition2.LessThan -> value1!! < value2!!
                        else -> TODO()
                    }
                }

                is LiveValue2.InstantNowLiveValue2 -> {
                    val value1 = when (val ref = liveValue2.value1.ref) {
                        is ZoneRef2 -> ((client.get(ref.vm) as? ZoneViewModel2 ?: error("no vm ${ref.vm}"))
                            .map[ref.property] as? ZoneViewModel2.Property.IntProperty)?.value

                        else -> TODO()
                    }

                    val value2 = getNow() // TODO not converting to Int causing issues? Not doesn't seem to be the issue

                    when (liveValue2.condition) {
                        is LiveValue2.Condition2.GreaterThan -> value1!! > value2
                        is LiveValue2.Condition2.LessThan -> value1!! < value2
                        else -> TODO()
//                    }.also { sduiLog(value1, liveValue2.condition, value2, it, tag = " filtered index > Items > Timer > Condition > Calculation", header = "start", footer = "end") }
                    }
                }

                else -> TODO()
            }

            else -> TODO()
        }
    }

    fun computeLiveText(liveValue: LiveValue2.TextLiveValue2) = when (liveValue.ref) {
        is LiveValue2.Ref2.StateRef2 -> {
            val paths = client.idPaths[liveValue.ref.id] ?: error("No paths found for id: ${liveValue.ref.id}")
            val path = paths.firstOrNull() ?: error("Paths were empty for id: ${liveValue.ref.id}")
            val state = client.get<State2>(path)
            (state as? TextState2)?.text ?: error("Not a text state ${liveValue.ref.id}")
        }

        is VmRef2 -> {
            val vm = client.viewModels[liveValue.ref.vm] as? ZoneViewModel2
                ?: error("No vm found for id: ${liveValue.ref.vm}")
            (vm.map[liveValue.ref.property] as? StringProperty)?.value
                ?: error("No value found for ref: ${liveValue.ref} in ${liveValue.ref.vm} for ${liveValue.ref.property}")
        }

        else -> TODO()
    }


    fun sort(liveValue2: LiveValue3, parent: ChildrenModifier2<*>): ChildrenModifier2<*> {
        if (liveValue2 !is LiveValue3.ReferenceableLiveValue3) {
            sduiLog("sort > expected ReferenceableLiveValue3 but got $liveValue2")
            return parent
        }

        val zoneWrappers = parent.children.mapNotNull { child ->
            child.metas.filterIsInstance<ZoneViewModel3>().firstOrNull()?.let { zvm -> ZoneWrapper(zvm, child) }
        }

        val values = zoneWrappers.map { zW ->
            when (liveValue2.ref) {
                is VmRef3 -> ChildOrderWrapper(order = zW.viewModel.map[liveValue2.ref.property] as IntValue, state = zW.state)
                is Ref3.StateRef3 -> {
                    sduiLog("sort > expected StateRef3 but got ${liveValue2.ref}")
                    return parent
                }
            }
        }.sortedBy { it.order.value }

        val updatedChildren = values.map { it.state }
        val updatedParent = parent.c(children = updatedChildren)
        client.update(updatedParent)
        return updatedParent as ChildrenModifier2<*>
    }

    fun filter(value: ConditionalValue, parent: ChildrenModifier2<*>): ChildrenModifier2<*> {
        val zoneWrappers = parent.children.mapNotNull { child ->
            child.metas.filterIsInstance<ZoneViewModel3>().firstOrNull()?.let { zvm -> ZoneWrapper(zvm, child) }
        }

        val values = zoneWrappers.map { zW ->
            val value = computeConditionalValue(value, zW.viewModel, parent as? State2)
            if (value !is BooleanValue) error("Expected boolean value but got $value")
            FilterWrapper(value, zW.state)
        }

        val updatedChildren = values.filter { it.filter.value }.map { it.state }
        val updatedParent = parent.modifiedChildren(modifiedChildren = updatedChildren)
        client.update(updatedParent)
        return updatedParent as ChildrenModifier2<*>
    }

    fun index(parent: ChildrenModifier2<*>) {
        val children = parent.filteredChildren ?: parent.children
        children.forEachIndexed { index, child ->
            child.metas.filterIsInstance<ZoneViewModel3>().firstOrNull()?.let {
                if (it.map["!index"] == IntValue(index)) return@forEachIndexed
                it.map["!index"] = IntValue(index)
            }
        }
    }

    fun focus(parent: ChildrenModifier2<*>) {
        parent.children.forEach { child ->
            child.metas.filterIsInstance<ZoneViewModel3>().firstOrNull()?.let {
                parent.focus?.let { focus ->
                    sduiLog("focus > parent focus: $focus", tag = "timer")
                    if (it.map["!focus"] == focus) return@forEach
                    it.map["!focus"] = focus
                }
            }
        }
    }

    fun jumpTo(liveValue: ConditionalValue, parent: ChildrenModifier2<*>): ChildrenModifier2<*> {
        val zoneWrappers = parent.children.mapNotNull { child ->
            child.metas.filterIsInstance<ZoneViewModel3>().firstOrNull()?.let { zvm -> ZoneWrapper(zvm, child) }
        }

        val values = zoneWrappers.map { zW ->
            val value = computeConditionalValue(liveValue, zW.viewModel, parent as? State2)
            if (value !is BooleanValue) error("Expected boolean value but got $value")
            JumpToWrapper(value, zW.state, zW.viewModel)
        }

        val value = values.firstOrNull { it.jumpTo.value }
        val map = value?.viewModel?.map
        val newFocus = map?.get("!index") as? IntValue
        sduiLog("jumpTo > newFocus: $value", "jumpTo > newFocus: $map" , "jumpTo > newFocus: $newFocus" , tag = "timer")
        val updatedParent = parent.focus(newFocus)
        client.update(updatedParent)
        return updatedParent as ChildrenModifier2<*>
    }

    fun getValue(liveValue: LiveValue3, vm: ZoneViewModel3? = null, state2: State2? = null): Value {
        return when (liveValue) {
            is LiveValue3.ReferenceableLiveValue3 -> when (liveValue.ref) {
                is Ref3.StateRef3 -> {
                    val paths = client.idPaths[liveValue.ref.id] ?: error("No paths found for id: ${liveValue.ref.id}")
                    val path = paths.firstOrNull() ?: error("Paths were empty for id: ${liveValue.ref.id}")
                    val state = client.get<State2>(path)
                    (state as? TextState2)?.text?.let { StringValue(it) }
                        ?: error("Not a text state ${liveValue.ref.id}")
                }

                is VmRef3 -> {
                    val updatedRef = if (liveValue.ref.vm == MetaId2() && vm != null) {
                        liveValue.copyRef(VmRef3(vm = vm.metaId, property = liveValue.ref.property)).ref
                    } else {
                        liveValue.ref
                    } as VmRef3
                    client.get<ZoneViewModel3>(updatedRef.vm).map[updatedRef.property]
                        ?: error("No value found for ref > in getValue: $updatedRef in ${updatedRef.vm} for ${updatedRef.property}")
                }
            }
            // TODO Could make it it's own type in future
            is LiveValue3.InstantNowLiveValue3 -> IntValue(getNow()).also {
                if (state2 != null && liveValue.refreshMillis != null) {
                    timer.add(state2, liveValue.refreshMillis / debugSpeed)
                }
            }
            is LiveValue3.StaticLiveValue3 -> liveValue.value
        }
    }

    fun reduceValue(value: Value, vm: ZoneViewModel3? = null, state2: State2? = null): Value {
        return when (value) {
            is ConditionalValue -> computeConditionalValue(value, vm, state2)
            else -> value
        }
    }

    fun computeConditionalValue(value: ConditionalValue, vm: ZoneViewModel3? = null, state2: State2? = null): Value {
        val value1 = getValue(value.value1, vm, state2).run { reduceValue(this, vm, state2) }
        val value2 = getValue(value.value2, vm, state2).run { reduceValue(this, vm, state2) }

        val result = when (value.condition) {
            Condition3.All -> {
                if (value1 !is ListValue<*>) value.notCurrentlySupported()
                BooleanValue(value1.value.all { it == value2 })
            }

            Condition3.Eq -> BooleanValue(value1 == value2)
            Condition3.GreaterThan -> {
                when {
                    value1 is IntValue && value2 is IntValue -> BooleanValue(value1.value > value2.value)
                    else -> value.notCurrentlySupported()
                }
            }

            Condition3.In -> {
                if (value2 !is ListValue<*>) value.notCurrentlySupported()
                BooleanValue(value1 in value2.value)
            }

            Condition3.InOrEmpty -> {
                if (value2 !is ListValue<*>) value.notCurrentlySupported()
                BooleanValue(value1 in value2.value || value2.value.isEmpty())
            }

            Condition3.LessThan -> {
                when {
                    value1 is IntValue && value2 is IntValue -> BooleanValue(value1.value < value2.value)
                    else -> value.notCurrentlySupported()
                }
            }

            Condition3.Mod -> {
                when {
                    value1 is IntValue && value2 is IntValue -> BooleanValue((value1.value % value2.value) == 0)
                    else -> value.notCurrentlySupported()
                }
            }

            Condition3.And -> {
                when {
                    value1 is BooleanValue && value2 is BooleanValue -> BooleanValue(value1.value && value2.value)
                    else -> value.notCurrentlySupported()
                }
            }

            else -> value.notCurrentlySupported()
        }

        return if (result.value) {
            getValue(value.trueBranch, vm)
        } else {
            getValue(value.falseBranch, vm)
        }
    }

    fun ConditionalValue.notCurrentlySupported(): Nothing =
        error("Not currently supported > $value1 $condition $value2")
}