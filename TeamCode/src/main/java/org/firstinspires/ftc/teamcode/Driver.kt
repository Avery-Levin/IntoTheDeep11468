package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import org.baylorschool.intothedeep.Action
import org.firstinspires.ftc.teamcode.pedroPathing.follower.Follower
import org.firstinspires.ftc.teamcode.pedroPathing.localization.Pose
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.BezierCurve
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.BezierLine
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.Path
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.Point

class Driver(val follower: Follower, startPose: Pose) {
    init {
        follower.setStartingPose(startPose)
    }
    fun runToAction(pose: Pose, bezier: Point? = null): Action {
        return object : Action {
            override fun init() {
                val nul = if (bezier == null) {
                    Path(BezierLine(Point(follower.pose.x, follower.pose.y, Point.CARTESIAN), Point(pose.x, pose.y, Point.CARTESIAN)))
                } else {
                    Path(BezierCurve(Point(follower.pose.x, follower.pose.y), bezier, bezier, Point(pose.x, pose.y)))
                }
                nul.setConstantHeadingInterpolation(pose.heading)
                follower.followPath(nul, true)
            }
            override fun update(): Boolean {
                follower.update()
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
}