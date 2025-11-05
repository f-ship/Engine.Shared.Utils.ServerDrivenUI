package ship.f.engine.shared.utils.serverdrivenui2.config.meta.models

import kotlinx.serialization.Serializable
import ship.f.engine.shared.utils.serverdrivenui2.client.Client2
import ship.f.engine.shared.utils.serverdrivenui2.config.action.models.Navigate2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2
import ship.f.engine.shared.utils.serverdrivenui2.config.state.models.Id2.MetaId2.Companion.autoMetaId2
import ship.f.engine.shared.utils.serverdrivenui2.state.State2

@Serializable
data class DataMeta2(
    override val metaId: Id2.MetaId2 = autoMetaId2(),
    val data: Map<String, DataMetaType2>,
) : Meta2() {
    fun toPopulatedDataMeta2(client: Client2): PopulatedDataMeta2 {
        return PopulatedDataMeta2(
            metaId = metaId,
            data = data.mapValues { (_, value) ->
                when (value) {
                    is DataMetaType2.StringData -> value.value
                    is DataMetaType2.RandomData -> value.pre + getRandomString()
                    is DataMetaType2.RandomIdStateData -> (value.pre + getRandomString()).also { id ->
                        val updatedOp = when(val op = value.navigation) {
                            is NavigationConfig2.StateOperation2.InsertionOperation2.End2 -> op.copy(
                                stateId = Id2.StateId2(name = id)
                            ).also { client.update(value.state.c(Id2.StateId2(id))) }
                            else -> error("Unsupported operation: $op")
                        }
                        Navigate2(
                            config = NavigationConfig2(
                                operation = updatedOp
                            )
                        ).run {
                            run(state = value.state, client = client)
                        }
                    }
                }
            }
        )
    }

    fun getString(key: String) = (data[key] as DataMetaType2.StringData).value

    @Serializable
    sealed class DataMetaType2 {
        @Serializable
        data class StringData(val value: String) : DataMetaType2()

        @Serializable
        data class RandomData(val pre: String = "") : DataMetaType2()

        // TODO used for making temporary client side UI before the backend confirms it and swaps it out
        @Serializable
        data class RandomIdStateData(
            val pre: String = "",
            val state: State2,
            val navigation: NavigationConfig2.StateOperation2,
        ) : DataMetaType2()
    }
}

@Serializable
data class PopulatedDataMeta2(
    override val metaId: Id2.MetaId2 = autoMetaId2(),
    val data: Map<String, String>,
) : Meta2()

fun getRandomString(length: Int = 16): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}