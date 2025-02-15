package org.baylorschool.intothedeep

import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import kotlin.jvm.Throws

/**
 * This is a simple action framework.
 * ActionGroups execute things concurrently until everything in the group is done.
 * ActionSets execute things one at a time and the "execute()" function is blocking.
 * init() and update() should be non-blocking
 * update() returns whether the action is done or not
 */
interface Action {
    fun init()
    /**
     * non-blocking.
     * @returns whether the action is done or not
     */
    fun update() : Boolean
    fun execute() {
        init()
        while (!update()) {/**/}
    }
}
class MultithreadedActionGroup(vararg actions0: Action) : Action {
    val running = AtomicInteger(actions0.size)
    var threads = actions0.map {
        Thread {
            it.execute()
            running.decrementAndGet()
        }
    }
    override fun init() {
        for (t in threads) {
            t.start()
        }
    }
    override fun update(): Boolean {
        return running.get() == 0
    }
}
class ActionGroup(vararg actions0: Action) : Action {
    private var actions = actions0.toList()
    override fun init() {
        for (i in actions) {
            i.init()
        }
    }
    override fun update(): Boolean {
        actions = actions.filterNot { it.update() }
        return actions.isEmpty()
    }
    /**
     * this is blocking, obviously.
     * @param close a function. Have it return true to kill the function.
     */
    fun execute(close: () -> (Boolean)) {
        init()
        while (!update() && !close()) {/**/}
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

    /**
     * this is blocking, obviously.
     * @param close a function. Have it return true to kill the function.
     */
    fun execute(close: () -> (Boolean)) {
        for (i in actions) {
            i.init()
            while (!i.update() && !close()) {
                // do nothing
            }
            if (close()) {
                return
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
fun wait(durms: Long): Action {
    return object : Action {
        var time = -1L
        override fun init() {
            time = System.currentTimeMillis()
        }

        override fun update(): Boolean {
            Thread.sleep(1)
            return System.currentTimeMillis() > time + durms
        }

    }
}
fun ensureMinTime(action: Action, durms: Long, hold: Boolean = false): Action {
    return object : Action {
        var start = 0L
        override fun init() {
            start = System.currentTimeMillis()
            action.init()
        }
        var done = false
        override fun update(): Boolean {
            if (!done || hold) {
                if (action.update()) {
                    done = true
                }
            }
            return done && System.currentTimeMillis() > start + durms
        }
    }
}
fun startWithDelay(action: Action, delay: Long): Action {
    return object : Action {
        var time = -1L
        override fun init() {
            time = System.currentTimeMillis()
        }
        //make x and y abs more for pickup 1
        var inited = false
        override fun update(): Boolean {
            if (time + delay < System.currentTimeMillis()) {
                if (!inited) {
                    inited = true
                    action.init()
                } else {
                    return action.update()
                }
            }
            return false
        }

    }
}