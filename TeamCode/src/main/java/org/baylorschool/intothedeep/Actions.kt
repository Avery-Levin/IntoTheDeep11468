package org.baylorschool.intothedeep

import java.time.Duration

/**
 * This is a simple action framework.
 * ActionGroups execute things concurrently until everything in the group is done.
 * ActionSets execute things one at a time and the "execute()" function is blocking.
 * init() and update() should be non-blocking
 */
interface Action {
    fun init()
    /**
     * @returns whether the action is done or not
     */
    fun update() : Boolean
    fun execute() {
        init()
        while (!update()) {}
    }
}
class ActionGroup(vararg actions0: Action) : Action {
    private val actions = actions0.toMutableList()
    override fun init() {
        for (i in actions) {
            i.init()
        }
    }
    override fun update(): Boolean {
        for (i in actions) {
            actions.removeIf { i.update() }
        }
        return actions.isEmpty()
    }
}
class ActionSet(vararg actions0: Action) : Action {
    private val actions = actions0.toMutableList()
    /**
     * this is blocking, obviously.
     */
    override fun execute() {
        for (i in actions) {
            i.init()
            while (!i.update()) {
                // do nothing
            }
        }
    }

    override fun init() {

    }
    var hasinited = false

    /**
     * so you can run this like an action, so this will work: ActionGroup {alwaysHappening, ActionSet {first, second, third} }
     */
    override fun update(): Boolean {
        if (actions.isEmpty()) {
            return true
        }
        if (!hasinited) {
            hasinited = true
            actions.first().init()
        } else {
            if (actions.first().update()) {
                actions.removeAt(0)
                hasinited = false
            }
        }
        return actions.isEmpty()
    }
}
fun initAction(id: () -> Unit): Action {
    return object : Action {
        override fun init() {
            id()
        }
        override fun update(): Boolean = true
    }
}
fun ensureMinTime(action: Action, durms: Long): Action {
    return ActionGroup(object : Action {
        val time = System.currentTimeMillis()
        override fun init() {}
        override fun update(): Boolean = System.currentTimeMillis() > time + durms
    }, action)
}