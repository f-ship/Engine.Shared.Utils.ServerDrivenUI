package ship.f.engine.shared.utils.serverdrivenui2.client3

import kotlinx.datetime.Clock
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.ZoneViewModel2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.ZoneViewModel2.Property.StringProperty
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.Ref2.VmRef2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.LiveValue2.Ref2.ZoneRef2
import ship.f.engine.shared.utils.serverdrivenui2.ext.sduiLog
import ship.f.engine.shared.utils.serverdrivenui2.state.State2
import ship.f.engine.shared.utils.serverdrivenui2.state.TextState2

class ComputationEngine(val client: Client3) {
    inline fun <reified T : LiveValue2> computeConditionalBranchLive(liveValue2: LiveValue2.ConditionalBranchLiveValue2): T {
        return when(liveValue2.value1){
            is LiveValue2.IntLiveValue2 -> when(liveValue2.value2){
                is LiveValue2.IntLiveValue2 -> {
                    val prop1 = when(liveValue2.value1.ref) {
                        is ZoneRef2 -> (client.viewModels[liveValue2.value1.ref.vm] as? ZoneViewModel2)?.let {
                            it.map[liveValue2.value1.ref.property] ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for ${liveValue2.value1.ref.property}")
                        } ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for ${liveValue2.value1.ref.property}")
                        else -> TODO()
                    }

                    val prop2 = when(liveValue2.value2.ref) {
                        is ZoneRef2 -> (client.viewModels[liveValue2.value2.ref.vm] as? ZoneViewModel2)?.let {
                            it.map[liveValue2.value2.ref.property] ?: error("No value found for ref: ${liveValue2.value2.ref} in ${liveValue2.value2.ref.vm} for1 ${liveValue2.value2.ref.property}")
                        } ?: error("No value found for ref: ${liveValue2.value2.ref} in ${liveValue2.value2.ref.vm} for ${liveValue2.value2.ref.property}")
                        else -> TODO()
                    }

                    val bool = when(liveValue2.condition){
                        LiveValue2.Condition2.Eq -> prop1 == prop2
                        else -> TODO()
                    }

                    if (bool){
                        liveValue2.trueBranch as T
                    } else {
                        liveValue2.falseBranch as T
                    }
                }
                is LiveValue2.StaticIntLiveValue2 -> {
                    val prop1 = when(liveValue2.value1.ref) {
                        is ZoneRef2 -> (client.viewModels[liveValue2.value1.ref.vm] as? ZoneViewModel2)?.let {
                            it.map[liveValue2.value1.ref.property] ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for2 ${liveValue2.value1.ref.property}")
                        } ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for3 ${liveValue2.value1.ref.property}")
                        else -> TODO()
                    }

                    val bool = when(liveValue2.condition){
                        LiveValue2.Condition2.Eq -> when(prop1){
                            is ZoneViewModel2.Property.IntProperty -> prop1.value == liveValue2.value2.value
                            else -> false
                        }

                        LiveValue2.Condition2.Mod -> when(prop1){
                            is ZoneViewModel2.Property.IntProperty -> (prop1.value % liveValue2.value2.value) == 0
                            else -> false
                        }
                        else -> TODO()
                    }

                    if (bool){
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
        return when(liveValue2.value1){
            is LiveValue2.TextLiveValue2 -> when(liveValue2.value2){
                is LiveValue2.MultiLiveValue2 -> {
                    val vm = client.get(liveValue2.value2.ref.vm) as? ZoneViewModel2 ?: error("No vm found for id: ${liveValue2.value2.ref.vm}")
                    val multiProperty = vm.map[liveValue2.value2.ref.property] as? ZoneViewModel2.Property.MultiProperty ?: error("No multi property found for ref: ${liveValue2.value2.ref} in ${liveValue2.value2.ref.vm}")
                    val prop = when(liveValue2.value1.ref) {
                        is ZoneRef2 -> StringProperty(
                            value = (client.viewModels[liveValue2.value1.ref.vm] as? ZoneViewModel2)
                                ?.let { it.map[liveValue2.value1.ref.property] as? StringProperty }
                                ?.value ?: error("No value found for ref: ${liveValue2.value1.ref} in ${liveValue2.value1.ref.vm} for ${liveValue2.value1.ref.property}"),
                        )
                        else -> TODO()
                    }
                    when(liveValue2.condition){
                        is LiveValue2.Condition2.InOrEmpty -> multiProperty.value.isEmpty() || multiProperty.value.contains(prop)
                        else -> TODO()
                    }
                }
                else -> TODO("Only handling TextLiveValue2 > MultiLiveValue2 for now")
            }
            is LiveValue2.IntLiveValue2 -> when(liveValue2.value2){
                is LiveValue2.IntLiveValue2 -> {
                    val value1 = when(val ref = liveValue2.value1.ref) {
                        is ZoneRef2 -> ((client.get(ref.vm) as? ZoneViewModel2 ?: error("no vm ${ref.vm}"))
                            .map[ref.property] as? ZoneViewModel2.Property.IntProperty)?.value
                        else -> TODO()
                    }

                    val value2 = when(val ref = liveValue2.value2.ref) {
                        is ZoneRef2 -> ((client.get(ref.vm) as? ZoneViewModel2 ?: error("no vm ${ref.vm}"))
                            .map[ref.property] as? ZoneViewModel2.Property.IntProperty)?.value.also { sduiLog(client.get(ref.vm), ref, tag = "wtf") }
                        else -> TODO()
                    }

                    when(liveValue2.condition){
                        is LiveValue2.Condition2.GreaterThan -> value1!! > value2!!
                        is LiveValue2.Condition2.LessThan -> value1!! < value2!!
                        else -> TODO()
                    }
                }
                is LiveValue2.InstantNowLiveValue2 -> {
                    val value1 = when(val ref = liveValue2.value1.ref) {
                        is ZoneRef2 -> ((client.get(ref.vm) as? ZoneViewModel2 ?: error("no vm ${ref.vm}"))
                            .map[ref.property] as? ZoneViewModel2.Property.IntProperty)?.value
                        else -> TODO()
                    }

                    val value2 = Clock.System.now().epochSeconds.toInt() // TODO not converting to Int causing issues? Not doesn't seem to be the issue

                    when(liveValue2.condition) {
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
            val vm = client.viewModels[liveValue.ref.vm] as? ZoneViewModel2 ?: error("No vm found for id: ${liveValue.ref.vm}")
            (vm.map[liveValue.ref.property] as? StringProperty)?.value
                ?: error("No value found for ref: ${liveValue.ref} in ${liveValue.ref.vm} for ${liveValue.ref.property}")
        }
        else -> TODO()
    }
}