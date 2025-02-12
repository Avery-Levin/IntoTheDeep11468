package org.firstinspires.ftc.teamcode.auto
import android.telephony.gsm.GsmCellLocation
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.Point
import com.pedropathing.util.Constants
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.intothedeep.Action
import org.baylorschool.intothedeep.ActionGroup
import org.baylorschool.intothedeep.ActionSet
import org.baylorschool.intothedeep.Global
import org.baylorschool.intothedeep.ensureMinTime
import org.baylorschool.intothedeep.lib.Depo
import org.baylorschool.intothedeep.lib.Pivot
import org.firstinspires.ftc.teamcode.Driver
import org.firstinspires.ftc.teamcode.lib.Slides
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants

@Autonomous
class ParkAuto : LinearOpMode() {
    private val placePreloadPos = Pose(10.0, 0.0, 0.0)
    private val placePreloadPos1 = Pose(25.0, 0.0, 0.0)
    private val push0StartPos = Pose(49.5, -46.5, 0.0)
    private val push0BezierPosA = Pose(-7.5, -53.0, 0.0)//toward human player
    private val push0BezierPosB = Pose(53.0, -23.5, 0.0)
    private val push0EndPos = Pose(5.5, -46.5, 0.0)
    private val push1BezierPosA = Pose(75.0, -46.5, 0.0)
    private val push1StartPos = Pose(49.5, -56.5, 0.0)
    private val push1EndPos = Pose(5.5, -56.5, 0.0)
    private val push2BezierPosA = Pose(75.0, -52.0, 0.0)
    private val push2StartPos = Pose(49.5, -62.0, 0.0)
    private val push2EndPos = Pose(5.5, -62.0, 0.0)//47
    private val pickup0Pos = Pose(12.0, -45.5, 0.0)
    override fun runOpMode() {
        Constants.setConstants(FConstants::class.java, LConstants::class.java)
        val driver = Driver(Follower(hardwareMap), Pose(-2.0, 0.0, 0.0))
        val telemetryA = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        val pivot = Pivot(hardwareMap)//up down
        val slides = Slides(hardwareMap)//forward back
        val depo = Depo(hardwareMap)//THE CLAW
        telemetryA.update()
        driver.update()
        depo.set(Global.diffy180)

        waitForStart()
        ActionGroup(ActionSet(
            Global.DiffyPosition.DiffySpecDepo.diffyPos.setAction(depo),
            driver.runToAction(placePreloadPos),
            Global.PivotPresets.RESET.action(pivot, driver.follower, telemetryA),
            driver.runToAction(push0StartPos, push0BezierPosA, push0BezierPosB),
            driver.runToAction(push0EndPos),
            driver.runToAction(push1StartPos, push1BezierPosA),
            driver.runToAction(push1EndPos),
            driver.runToAction(push2StartPos, push2BezierPosA),
            driver.runToAction(push2EndPos),
            //ensureMinTime(driver.runToAction(pickup0Pos), 2000, true),
            //driver.runToAction(Pose(1.0,12.0), Pose(10.0, 0.0)),
            Global.PivotPresets.DEPO.action(pivot, driver.follower, telemetryA),
            //Global.SlidePresets.
            object : Action {
                override fun init() {}
                override fun update(): Boolean = false
            }
        ), pivot.action(telemetryA), slides.action(telemetryA), driverAction(driver)).execute()
        //driver.holdPoint(Pose(2.0,12.0))

        while (!isStopRequested && opModeIsActive()) {
            driver.update()
            driver.telemetryDebug(telemetryA)
        }
    }
    fun driverAction(driver: Driver) : Action {
        driver.update()
        return object : Action {
            override fun init() {}

            override fun update(): Boolean {
                driver.update()
                return false
            }
        }
    }
}