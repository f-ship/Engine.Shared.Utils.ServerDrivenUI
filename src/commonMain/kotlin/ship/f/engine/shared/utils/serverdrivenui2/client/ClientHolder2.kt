package ship.f.engine.shared.utils.serverdrivenui2.client

/**
 * Used as a shared library analogy to the CompositionLocalProvider
 */
object ClientHolder2 {
    val clients = mutableSetOf<Client2>()
    inline fun <reified T : Client2> add(projectName: String? = null, builder: (String?) -> T): T =
        clients.filterIsInstance<T>().find { it.projectName == projectName } ?: builder(projectName).also {
            clients.add(it)
        }

    inline fun <reified T : Client2> get(projectName: String? = null) = clients.filterIsInstance<T>().run {
        if (projectName == null) last() else checkNotNull(find { it.projectName == projectName }) { "Client with project name $projectName not found" }
    }
}