package ship.f.engine.shared.utils.serverdrivenui.client

import ship.f.engine.shared.utils.serverdrivenui.ElementOperation
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.*
import ship.f.engine.shared.utils.serverdrivenui.action.*
import ship.f.engine.shared.utils.serverdrivenui.action.Trigger.OnInitialRenderTrigger
import ship.f.engine.shared.utils.serverdrivenui.ext.fGet
import ship.f.engine.shared.utils.serverdrivenui.ext.measureInMillis
import ship.f.engine.shared.utils.serverdrivenui.state.State
import ship.f.engine.shared.utils.serverdrivenui.state.WidgetState

abstract class Client {
    /**
     * The map of elements that the client keeps of track off
     */
    private val elementMap: MutableMap<String, MutableMap<String, Element<out State>>> = mutableMapOf()

    /**
     * Map of screenConfigs that have been properly initialized
     */
    val screenConfigMap: MutableMap<ScreenId, ScreenConfig> = mutableMapOf()

    /**
     * Backstack of screenConfigs that have been visited
     */
    var backstack: MutableList<ScreenConfig> = mutableListOf()

    /**
     * Map used as session storage for context that may be required by the server but not important for the client
     */
    val metaMap: MutableMap<MetaId, Meta> = mutableMapOf()

    /**
     * Map used to store listeners for meta-updates, this is pretty ugly for now, should probably fo
     */
    val metaListenerMap: MutableMap<MetaId, MutableList<RemoteAction>> = mutableMapOf()

    /**
     * Deferred Actions can be executed or canceled at a later time, this map cache said actions
     * I probably should at some point replace all these maps and things with a Meta Implementation
     */
    val deferredActions: MutableMap<MetaId, MutableList<DeferredAction>> = mutableMapOf()

    /**
     * When the server sends the client a component, and it can't render, the component will be rendered with fallback behavior.
     * Typically, a component will hide by default but may also show an optional update required banner.
     * In some cases if a feature is critical, a mandatory update required banner may be shown.
     */
    var banner: Fallback? = null

    /**
     * Sometimes the backend may return more than one screen,
     * in such a case the first screen is usually rendered while the others should be stored and referenced
     */
    fun store(config: ScreenConfig) {
        config.children.forEach {
            setState(it)
            setTriggers(it)
            initialTriggers(it)
        }
        screenConfigMap[config.id] = config
    }

    /**
     * Sometimes the backend may not need to return with any UI, but may return with a meta that contains information
     */
    fun store(config: Meta) {

    }

    /**
     * Push a new screenConfig onto the backstack and set the state of the screenConfig.
     * If the last screen in the backstack has the same ID as the new screenConfig,
     * then the screenConfig will be removed from the backstack as it will be replaced by the new screenConfig.
     */
    fun navigate(config: ScreenConfig) {
        // TODO acts an informal refresh which resets the current state of the screen with the oncoming screen config
        if (backstack.lastOrNull()?.id == config.id) {
            backstack.removeLast()
            screenConfigMap.remove(config.id)
        }

        // TODO
        if (screenConfigMap[config.id] == null) {
            config.children.forEach {
                setState(it)
                setTriggers(it)
                initialTriggers(it)
            }
            screenConfigMap[config.id] = config
        }

        addConfig(config)
        postScreenConfig()
    }

    /**
     * Add elements to a screenConfig
     */
    fun navigate(config: ElementOperation) { // TODO such a disgusting method

        val elementsToUpdate = config.elements.filter { safeGetElement(it.id) == null }

        when (config) {
            is ElementOperation.Replace -> {
                val replaceElement = config.elements.first()
                val originalElement = gElement(config.replace).updateElement(activeScope = replaceElement.id.scope)
                setElement(replaceElement.id, replaceElement)
                setElement(originalElement.id, originalElement)
                postReactiveUpdate(replaceElement)
                postReactiveUpdate(originalElement)
            }

            is ElementOperation.InsideAndEnd -> {
                (gElement(config.inside) as? Widget<out WidgetState>)?.let {
                    val updateElement =
                        it.updateElement(it.state.copyChildren(children = it.state.children + config.elements))
                    setElement(config.inside, updateElement)
                    postReactiveUpdate(updateElement)
                }
            }

            is ElementOperation.InsideAndAfter -> {
                (gElement(config.inside) as? Widget<out WidgetState>)?.let {
                    val updateElement = it.updateElement(
                        it.state.copyChildren(
                            children = combineChildren(it.state.children, config.after, config.elements)
                        )
                    )
                    setElement(config.inside, updateElement)
                    postReactiveUpdate(updateElement)
                }
            }

            is ElementOperation.InsideAndBefore -> {
                (gElement(config.inside) as? Widget<out WidgetState>)?.let {
                    val updateElement = it.updateElement(
                        it.state.copyChildren(
                            children =
                                combineChildren(it.state.children, config.before, config.elements, -1)
                        )
                    )
                    setElement(config.inside, updateElement)
                    postReactiveUpdate(updateElement)
                }
            }

            is ElementOperation.InsideAndStart -> {
                (gElement(config.inside) as? Widget<out WidgetState>)?.let {
                    val updateElement =
                        it.updateElement(it.state.copyChildren(children = config.elements + it.state.children))
                    setElement(config.inside, updateElement)
                    postReactiveUpdate(updateElement)
                }
            }

            is ElementOperation.After -> {
                gScreenConfig(config.inside).let {
                    val updateElement = it.copy(children = combineChildren(it.children, config.after, config.elements))
                    setScreenConfig(config.inside, updateElement)
                    postScreenConfig()
                }
            }

            is ElementOperation.Before -> {
                gScreenConfig(config.inside).let {
                    val updateElement = it.copy(children = combineChildren(it.children, config.before, config.elements))
                    setScreenConfig(config.inside, updateElement)
                    postScreenConfig()
                }
            }

            is ElementOperation.End -> {
                gScreenConfig(config.inside).let {
                    val updateElement = it.copy(children = it.children + config.elements)
                    setScreenConfig(config.inside, updateElement)
                    postScreenConfig()
                }
            }

            is ElementOperation.Start -> {
                gScreenConfig(config.inside).let {
                    val updateElement = it.copy(children = config.elements + it.children)
                    setScreenConfig(config.inside, updateElement)
                    postScreenConfig()
                }
            }
        }

        /**
         * Used to make all new elements that are being added to become reactive
         */
        elementsToUpdate.forEach {
            setState(it)
            setTriggers(it)
            initialTriggers(it)
        }
    }

    /**
     * Initially generates the element map for the client
     */
    fun setState(element: Element<out State>) {
        // TODO this is so stupid, need a cleaner way to detect when an element is duplicate or not. Should we really be setting state on elements that already exist?
        if (safeGetElement(element.id) != null && safeGetElement(element.id) != element && safeGetElement(element.id)?.activeScope == element.activeScope) {
            // TODO need a smarter way of handling the detection of duplicate IDs, I think this is where composite IDs should be useful
            error("Duplicate ID: ${element.id}, if you need to replace an element make sure id + scope is unique")
        }
        setElement(element.id, element)
        element.metas.forEach { metaMap[it.key] = it.value } // TODO Metas will eventually need to be scoped
        createReactiveUpdate(element)

        when (element) {
            is Component<*> -> Unit
            is Widget<*> -> element.state.children.forEach { setState(it) }
        }
    }

    /**
     * Used to recursively add triggers to the client's element map
     */
    fun setTriggers(element: Element<out State>) {
        element.triggers.filterIsInstance<Trigger.OnStateUpdateTrigger>().forEach {
            when (it.action) {
                is ElementPublisherAction -> setTrigger(element, it.action.publisherId, it.action)
                is MultiElementPublisherAction -> it.action.publisherIds.forEach { target ->
                    setTrigger(
                        element,
                        target,
                        it.action
                    )
                }

                else -> Unit
            }
        }

        element.triggers.filterIsInstance<Trigger.OnMetaUpdateTrigger>().forEach {
            when (it.action) {
                is MetaPublisherAction -> setMetaTrigger(element, it.action.publisherId, it.action, it.metaID)
                is MultiMetaPublisherAction -> it.action.publisherIds.forEach { target ->
                    setMetaTrigger(
                        element,
                        target,
                        it.action,
                        it.metaID
                    )
                }

                else -> Unit
            }
        }

        when (element) {
            is Component<*> -> Unit
            is Widget<*> -> element.state.children.forEach { setTriggers(it) }
        }
    }

    private fun setTrigger(element: Element<out State>, target: ElementId, action: Action) {
        val targetElement = gElement(target)
        val updatedElement = targetElement.updateElement(
            listeners = targetElement.listeners + RemoteAction(
                action = action,
                id = element.id,
            )
        )

        setElement(target, updatedElement)
        createReactiveUpdate(updatedElement)
    }

    private fun setMetaTrigger(element: Element<out State>, target: MetaId, action: Action, metaId: MetaId) {
        if (metaListenerMap[target] == null) {
            metaListenerMap[target] = mutableListOf()
        }

        metaListenerMap[target]!!.add(
            RemoteAction(
                action = action,
                id = element.id,
                metaID = metaId,
            )
        )
    }

    fun initialTriggers(element: Element<out State>) {
        element.triggers.filterIsInstance<OnInitialRenderTrigger>().forEach {
            it.action.execute(
                element = element,
                client = this,
                meta = metaMap[it.metaID] ?: Meta.None(),
            )
        }

        when (element) {
            is Component<*> -> Unit
            is Widget<*> -> element.state.children.forEach { initialTriggers(it) }
        }
    }

    // TODO to be used in the action that toggles the existence of a value in a MetaList
    fun updateMeta(meta: Meta) {
        metaMap[meta.id] = meta

        // TODO when a meta updates we want to run the correct action with the correct element
        metaListenerMap[meta.id]?.forEach {
            it.action.execute(
                element = gElement(it.id),
                client = this,
                meta = metaMap[it.metaID]
                    ?: Meta.None(), // TODO a little dangerous just adding a default value without propper logging, benchmarking and logging will come soon
            )
        }
    }

    /**
     * Responsible for first updating the state of the element and then propagating the state to all listeners
     */
    fun updateElement(element: Element<out State>) = measureInMillis("updateState: ${element.id}") {
        setElement(element.id, element)
        propagateState(element)
        postReactiveUpdate(element)
    }

    fun gElement(id: ElementId) = elementMap.fGet(id.id).fGet(id.scope).let {
        elementMap.fGet(id.id).fGet(it.activeScope)
    }

    fun safeGetElement(id: ElementId) = elementMap[id.id]?.get(id.scope)?.let {
        elementMap[id.id]?.get(it.activeScope)
    }

    fun setElement(id: ElementId, element: Element<out State>) {
        if (elementMap[id.id] == null) {
            elementMap[id.id] = mutableMapOf()
        }
        elementMap[id.id]!![id.scope] = element
    }

    fun gScreenConfig(id: ScreenId) = screenConfigMap[id]!!
    fun setScreenConfig(id: ScreenId, screenConfig: ScreenConfig) {
        screenConfigMap[id] = screenConfig
    }

    private fun combineChildren(
        children: List<Element<out State>>,
        config: ElementId,
        elements: List<Element<out State>>,
        offset: Int = 0
    ) = children
        .indexOfFirst { it.id == config }
        .let {
            if (it == -1) {
                children + elements
            } else {
                children.subList(
                    0,
                    it + 1 + offset
                ) + elements + children.subList(
                    it + 1 + offset,
                    children.size
                )
            }
        }


    /**
     * Add a screenConfig to the backstack
     */
    private fun addConfig(config: ScreenConfig) {
        backstack.add(config)
        postScreenConfig()
    }

    /**
     * Each listener depending on the action should be able to read the state of the element that propagated it's state
     * The reason the state is not passed directly is that the listener may be interested in multiple elements.
     * Even if one of the targets triggered the action, it may still need to decide on how to handle the state based on all targets
     */
    private fun propagateState(element: Element<out State>) {
        element.listeners.forEach { listener ->
            listener.action.execute(gElement(listener.id), this)
        }
    }

    /**
     * This hook transforms the element map into a map of reactive elements which clients can bind to.
     * This is done so that the shared library does not need to depend on composed runtime directly, which should reduce its bundle size.
     * Since the shared library will be included in backend environments, it's imperative to keep the bundle lean to make cold starts fast.
     */
    abstract fun postReactiveUpdate(element: Element<out State>)

    /**
     * Used to enable Client to handle setting state while enabling downstream clients to listen to state changes.
     */
    abstract fun createReactiveUpdate(element: Element<out State>)

    /**
     * Refreshes the last screen in the backstack
     */
    abstract fun postScreenConfig()
}