package org.firstinspires.ftc.teamcode.auto
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point
import com.pedropathing.util.Constants
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.intothedeep.Action
import org.baylorschool.intothedeep.ActionGroup
import org.baylorschool.intothedeep.ActionSet
import org.baylorschool.intothedeep.Global
import org.baylorschool.intothedeep.Global.PivotPIDConfig.target
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
    private val placePreloadPos = Pose(19.0, 0.0, 0.0)
    private val placePreloadPos1 = Pose(29.0, 0.0, 0.0)
    private val placePreloadPos2 = Pose(29.5, 0.0, 0.0)
    private val push0StartPos = Pose(49.5, -44.5, 0.0)
    private val push0BezierPosA = Pose(-10.5, -56.0, 0.0)//toward human player
    private val push0BezierPosB = Pose(53.0, -23.5, 0.0)
    private val push0EndPos = Pose(12.0, -46.5, 0.0)
    private val push1BezierPosA = Pose(70.0, -46.5, 0.0)
    private val push1StartPos = Pose(49.5, -56.5, 0.0)
    private val push1EndPos = Pose(12.0, -56.5, 0.0)
    private val push2BezierPosA = Pose(70.0, -52.0, 0.0)
    private val push2StartPos = Pose(49.5, -62.5, 0.0)
    private val push2EndPos = Pose(13.5, -62.5, 0.0)//47
    private val pickup0Pos = Pose(12.0, -41.0, 0.0)
    override fun runOpMode() {
        Constants.setConstants(FConstants::class.java, LConstants::class.java)
        val driver = Driver(Follower(hardwareMap), Pose(0.0, -8.0, 0.0))

        val telemetryA = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        val pivot = Pivot(hardwareMap)//up down
        val slides = Slides(hardwareMap)//forward back
        val depo = Depo(hardwareMap)//THE CLAW
        telemetryA.addLine("drivepid: " + FollowerConstants.drivePIDFCoefficients.toString())
        telemetry.addData("pivot target", 0.0)
        telemetry.addData("pivot distance", 0.0)
        telemetry.addData("pivot follower T", 0.0)
        telemetryA.update()
        driver.update()
        pivot.offset = 1100
        pivot.specDeposit()
        depo.diffyInit()

        while (!isStarted()) {
            driver.update()
            pivot.update()
            if (gamepad1.left_bumper) {
                depo.claw.position = 0.9
            } else if (gamepad1.right_bumper) {
                depo.claw.position = 0.55
            }
        }
        ActionGroup(ActionSet(
            genPlacement(driver, pivot, slides, depo, usecloserpoint = true),

            ActionGroup(genPush(driver), Global.PivotPresets.WALL_PICKUP_AUTO.action(pivot)),

            //usecurrentpos is true so it drives to push2EndPos
            genPickup(true, driver, pivot, slides, depo, telemetryA),//DONT USE INTERPOLATION HERE
            genPlacement(driver, pivot, slides, depo, true),

            genPickup(false, driver, pivot, slides, depo, telemetryA),
            genPlacement(driver, pivot, slides, depo),

            genPickup(false, driver, pivot, slides, depo, telemetryA),
            genPlacement(driver, pivot, slides, depo),

            genPickup(false, driver, pivot, slides, depo, telemetryA),
            genPlacement(driver, pivot, slides, depo),
        ), pivot.action(telemetryA), slides.action(telemetryA), driverAction(driver), object : Action {
            override fun init() {}

            override fun update(): Boolean {
                telemetryA.update()
                return false
            }
        }).execute { isStopRequested }

        while (!isStopRequested && opModeIsActive()) {
            driver.update()
            driver.telemetryDebug(telemetryA)
        }
    }
    private fun genPickup(useCurrentPos: Boolean, driver: Driver, pivot: Pivot, slides: Slides, depo: Depo, telemetry: MultipleTelemetry): ActionSet {
        return ActionSet(
            ActionGroup(
                if (!useCurrentPos) driver.runToAction(pickup0Pos) else driver.runToAction(push2EndPos),
                Global.PivotPresets.WALL_PICKUP_AUTO.action(pivot, driver.follower, telemetry),
                ensureMinTime(Global.DiffyPosition.DiffySpecIntake.diffyPos.setAction(depo),1000),
                ensureMinTime(depo.setClaw(true), 1000),
                Global.SlidePresets.FWINTAKE_ALMOST.action(slides),
            ),
            ensureMinTime(Global.SlidePresets.FWINTAKE.action(slides), 300),
            ensureMinTime(depo.setClaw(false), 1000),
            Global.SlidePresets.RESET.action(slides)
        )
    }
    private fun genPlacement(driver: Driver, pivot: Pivot, slides: Slides, depo: Depo, usebezier: Boolean = false, usecloserpoint: Boolean = false) : ActionSet {
        val ppp = if (usecloserpoint) placePreloadPos2 else placePreloadPos1
        //2 is closer
        return ActionSet(//x5
            ensureMinTime(ActionGroup(
                if (usebezier) driver.runToAction(ppp, placePreloadPos) else driver.runToAction(ppp),
                Global.DiffyPosition.DiffySpecDepo.diffyPos.setAction(depo),
                Global.PivotPresets.SPEC_DEPOSIT.action(pivot),
                Global.SlidePresets.HIGH_CHAMBER.action(slides))
                , 2250),
            //driver.runToAction(placePreloadPos1),
            ensureMinTime(Global.SlidePresets.HIGH_CHAMBER_DROP_AUTO.action(slides), 250),
            ActionGroup (
                //Global.PivotPresets.SPEC_DEPOSIT_DROP.action(pivot),
                Global.SlidePresets.RESET.action(slides),
                depo.setClaw(true),
            ),
        )
    }
    private fun genPush(driver: Driver) : Action {
        return ActionSet(
            //driver.runToAction(PathChain(
                driver.runToAction(push0StartPos, push0BezierPosA, push0BezierPosB),
                driver.runToAction(push0EndPos),
                driver.runToAction(push1StartPos, push1BezierPosA),
                driver.runToAction(push1EndPos),
                driver.runToAction(push2StartPos, push2BezierPosA),
            //))
        )
    }
    private fun driverAction(driver: Driver) : Action {
        driver.update()
        return object : Action {
            override fun init() {}

            override fun update(): Boolean {
                driver.update()
                return false
            }
        }
    }
    private fun nullAction(): Action = object : Action {
        override fun init() {}
        override fun update(): Boolean {
            return true
        }
    }
}