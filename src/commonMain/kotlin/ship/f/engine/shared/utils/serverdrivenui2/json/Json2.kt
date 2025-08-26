package ship.f.engine.shared.utils.serverdrivenui2.json

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import ship.f.engine.shared.utils.serverdrivenui2.state.ScreenState2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

@OptIn(InternalSerializationApi::class)
val json2 = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    encodeDefaults = true
    allowStructuredMapKeys = true
    classDiscriminatorMode = ClassDiscriminatorMode.ALL_JSON_OBJECTS

    serializersModule = SerializersModule {
        polymorphic(
            baseClass = State2::class,
        ) {
            subclass(ScreenState2::class)
            defaultDeserializer { ScreenState2.serializer() }
        }
    }
}