package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import org.baylorschool.intothedeep.Action
import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.Path
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point
import com.pedropathing.util.Drawing

class Driver(val follower: Follower, startPose: Pose) {
    init {
        follower.setStartingPose(startPose)
    }
    fun runToAction(pathChain: PathChain): Action {
        return object : Action {
            override fun init() {
                follower.followPath(pathChain, true)
            }
            override fun update(): Boolean {
                follower.update()
                Drawing.drawDebug(follower)
                return !isRunning()//if it's still running we're not done!
            }
        }
    }
    fun runToAction(path: Path): Action {
        return object : Action {
            override fun init() {
                follower.followPath(path, true)
            }
            override fun update(): Boolean {
                follower.update()
                Drawing.drawDebug(follower)
                return !isRunning()//if it's still running we're not done!
            }
        }
    }
    fun genPath(pose: Pose, vararg bezier: Pose? = arrayOf()): Path {
        val nul = if (bezier == null || bezier.isEmpty()) {
            Path(BezierLine(Point(follower.pose.x, follower.pose.y, Point.CARTESIAN), Point(pose.x, pose.y, Point.CARTESIAN)))
        } else {
            Path(BezierCurve(Point(follower.pose.x, follower.pose.y), *bezier.map { Point(it!!.x, it.y) }.toTypedArray(), Point(pose.x, pose.y)))
        }
        nul.setConstantHeadingInterpolation(pose.heading)
        return nul
    }
    fun runToAction(pose: Pose, vararg bezier: Pose? = arrayOf()): Action {
        return object : Action {
            override fun init() {
                val nul = if (bezier == null) {
                    Path(BezierLine(Point(follower.pose.x, follower.pose.y, Point.CARTESIAN), Point(pose.x, pose.y, Point.CARTESIAN)))
                } else {
                    Path(BezierCurve(Point(follower.pose.x, follower.pose.y), *bezier.map { Point(it!!.x, it.y) }.toTypedArray(), Point(pose.x, pose.y)))
                }
                nul.setConstantHeadingInterpolation(pose.heading)
                follower.followPath(nul, true)
            }
            override fun update(): Boolean {
                follower.update()
                Drawing.drawDebug(follower)
                return !isRunning()//if it's still running we're not done!
            }
        }
    }

    fun runTo(pose: Pose, async: Boolean = true) {
        if (!async) {
            followToEnd()
        }
    }
    fun runTo(pose: Pose, bezier: Point, async: Boolean = true) {
        follower.breakFollowing()
        val nul = Path(BezierCurve(Point(follower.pose.x, follower.pose.y), bezier, bezier, Point(pose.x, pose.y)))
        nul.setConstantHeadingInterpolation(pose.heading)
        follower.followPath(nul, false)
        if (!async) {
            followToEnd()
        }
    }
    fun followToEnd(tm: MultipleTelemetry? = null) {
        while (isRunning()) {
            //is this bad?
            update()
            if (tm != null) {
                telemetryDebug(tm)
            }
        }
    }
    fun isRunning(): Boolean {
        return follower.isBusy
    }
    fun update() {
        follower.update()
    }

    fun telemetryDebug(telemetryA: MultipleTelemetry) {
        follower.telemetryDebug(telemetryA)
    }

    fun holdPoint(ready0Pos: Pose) {
        follower.holdPoint(Point(ready0Pos.x, ready0Pos.y), ready0Pos.heading)
    }
    fun holdAction(): Action {
        return object : Action {
            override fun init() {}

            override fun update(): Boolean {
                follower.update()
                return false
            }

        }
    }
}