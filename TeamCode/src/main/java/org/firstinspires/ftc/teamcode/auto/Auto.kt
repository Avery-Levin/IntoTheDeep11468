package org.firstinspires.ftc.teamcode.auto
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
class Auto : LinearOpMode() {
    //private val startPos = Pose(-32.0, -5.0*12.0, Math.toRadians(90.0))
    private val placePreloadPos = Pose(24.0, 0.0, 0.0)
    private val placePreloadPos1 = Pose(29.5, 0.0, 0.0)
    private val push0StartPos = Pose(49.5, -44.5, 0.0)
    private val push0BezierPosA = Pose(-10.5, -56.0, 0.0)//toward human player
    private val push0BezierPosB = Pose(53.0, -23.5, 0.0)
    private val push0EndPos = Pose(8.5, -46.5, 0.0)
    private val push1BezierPosA = Pose(75.0, -46.5, 0.0)
    private val push1StartPos = Pose(49.5, -56.5, 0.0)
    private val push1EndPos = Pose(8.5, -56.5, 0.0)
    private val push2BezierPosA = Pose(75.0, -52.0, 0.0)
    private val push2StartPos = Pose(49.5, -62.5, 0.0)
    private val push2EndPos = Pose(8.5, -62.5, 0.0)//47
    private val pickup0Pos = Pose(12.0, -41.0, 0.0)
    override fun runOpMode() {
        Constants.setConstants(FConstants::class.java, LConstants::class.java)
        val driver = Driver(Follower(hardwareMap), Pose(0.0, -8.0, 0.0))

        val telemetryA = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        val pivot = Pivot(hardwareMap)//up down
        val slides = Slides(hardwareMap)//forward back
        val depo = Depo(hardwareMap)//THE CLAW
        telemetryA.update()
        driver.update()
        pivot.offset = 1100
        depo.closeClaw()
        pivot.deposit()

        waitForStart()
        ActionGroup(ActionSet(
            ensureMinTime(ActionGroup(
                driver.runToAction(placePreloadPos),
                Global.DiffyPosition.DiffySpecDepo.diffyPos.setAction(depo),
                Global.PivotPresets.SPEC_DEPOSIT.action(pivot),
                Global.SlidePresets.HIGH_CHAMBER.action(slides))
            , 3250),
            ensureMinTime(driver.runToAction(placePreloadPos1), 750),
            ensureMinTime(Global.SlidePresets.HIGH_CHAMBER_DROP_AUTO.action(slides), 1000),
            ActionGroup (
                Global.SlidePresets.RESET.action(slides),
                ensureMinTime(depo.setClaw(true), 2000),
            ),

            driver.runToAction(push0StartPos, push0BezierPosA, push0BezierPosB),
            driver.runToAction(push0EndPos),
            driver.runToAction(push1StartPos, push1BezierPosA),
            driver.runToAction(push1EndPos),
            driver.runToAction(push2StartPos, push2BezierPosA),
            driver.runToAction(push2EndPos),

            ensureMinTime(driver.runToAction(pickup0Pos), 2000, true),
            Global.PivotPresets.WALL_PICKUP_AUTO.action(pivot),
            ensureMinTime(Global.DiffyPosition.DiffySpecIntake.diffyPos.setAction(depo),1000),
            ensureMinTime(depo.setClaw(true), 1000),
            Global.SlidePresets.FWINTAKE.action(slides),
            ensureMinTime(depo.setClaw(false), 1000),
            Global.SlidePresets.RESET.action(slides),
            ensureMinTime(ActionGroup(
                driver.runToAction(placePreloadPos),
                Global.DiffyPosition.DiffySpecDepo.diffyPos.setAction(depo),
                Global.PivotPresets.SPEC_DEPOSIT.action(pivot),
                Global.SlidePresets.HIGH_CHAMBER.action(slides))
                , 3250),
            ensureMinTime(depo.setClaw(true), 1000),

            ensureMinTime(driver.runToAction(pickup0Pos), 2000, true),
            Global.PivotPresets.WALL_PICKUP_AUTO.action(pivot),
            ensureMinTime(Global.DiffyPosition.DiffySpecIntake.diffyPos.setAction(depo),1000),
            ensureMinTime(depo.setClaw(true), 1000),
            Global.SlidePresets.FWINTAKE.action(slides),
            ensureMinTime(depo.setClaw(false), 1000),
            Global.SlidePresets.RESET.action(slides),
            ensureMinTime(ActionGroup(
                driver.runToAction(placePreloadPos),
                Global.DiffyPosition.DiffySpecDepo.diffyPos.setAction(depo),
                Global.PivotPresets.SPEC_DEPOSIT.action(pivot),
                Global.SlidePresets.HIGH_CHAMBER.action(slides))
                , 3250),
            ensureMinTime(depo.setClaw(true), 1000),

            ensureMinTime(driver.runToAction(pickup0Pos), 2000, true),
            Global.PivotPresets.WALL_PICKUP_AUTO.action(pivot),
            ensureMinTime(Global.DiffyPosition.DiffySpecIntake.diffyPos.setAction(depo),1000),
            ensureMinTime(depo.setClaw(true), 1000),
            Global.SlidePresets.FWINTAKE.action(slides),
            ensureMinTime(depo.setClaw(false), 1000),
            Global.SlidePresets.RESET.action(slides),
            ensureMinTime(ActionGroup(
                driver.runToAction(placePreloadPos),
                Global.DiffyPosition.DiffySpecDepo.diffyPos.setAction(depo),
                Global.PivotPresets.SPEC_DEPOSIT.action(pivot),
                Global.SlidePresets.HIGH_CHAMBER.action(slides))
                , 3250),
            ensureMinTime(depo.setClaw(true), 1000),/*
            Global.PivotPresets.WALL_PICKUP_UP_AUTO.action(pivot),
            ActionGroup (
                Global.PivotPresets.SPEC_DEPOSIT_AUTO.action(pivot),
                ensureMinTime(Global.DiffyPosition.DiffySpecDepo.diffyPos.setAction(depo), 1000),
            ),
            Global.SlidePresets.HIGH_CHAMBER_AUTO.action(slides),
            driver.runToAction(placePreloadPos1),
            Global.SlidePresets.HIGH_CHAMBER_DROP_AUTO.action(slides),
            ActionGroup (
                Global.SlidePresets.RESET.action(slides),
                depo.setClaw(true)
            )*/
        ), pivot.action(telemetryA), slides.action(telemetryA), driverAction(driver), object : Action {
            override fun init() {}

            override fun update(): Boolean {
                telemetryA.update()
                return false
            }
        }).execute()
        driver.holdPoint(push2EndPos)

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