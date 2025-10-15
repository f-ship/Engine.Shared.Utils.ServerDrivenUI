package ship.f.engine.shared.utils.serverdrivenui2.json

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Action2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.StateMachineSelect2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.ToggleFilter2
import ship.f.engine.shared.utils.serverdrivenui2.config.meta.models.StateMachineMeta2

@OptIn(InternalSerializationApi::class)
val json2 = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    encodeDefaults = true
    allowStructuredMapKeys = true
    classDiscriminatorMode = ClassDiscriminatorMode.ALL_JSON_OBJECTS

    serializersModule = SerializersModule {
//        polymorphic(
//            baseClass = State2::class,
//        ) {
//            subclass(ScreenState2::class)
//            defaultDeserializer { ScreenState2.serializer() }
//        }

        polymorphic(
            baseClass = Action2::class,
        ) {
            // TODO add all actions
            subclass(ToggleFilter2::class)
            subclass(StateMachineSelect2::class)
        }

        polymorphic(
            baseClass = StateMachineMeta2.StateMachineOperation2::class
        ) {
            subclass(StateMachineMeta2.StateMachineOperation2.SwapOperation2::class)
        }
    }
}