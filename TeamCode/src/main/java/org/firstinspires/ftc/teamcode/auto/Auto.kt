package org.firstinspires.ftc.teamcode.auto
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.localization.Pose
import com.pedropathing.util.Constants
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.baylorschool.intothedeep.Action
import org.baylorschool.intothedeep.ActionGroup
import org.baylorschool.intothedeep.ActionSet
import org.baylorschool.intothedeep.Global
import org.baylorschool.intothedeep.ensureMinTime
import org.baylorschool.intothedeep.lib.Depo
import org.baylorschool.intothedeep.lib.Pivot
import org.baylorschool.intothedeep.wait
import org.firstinspires.ftc.teamcode.Driver
import org.firstinspires.ftc.teamcode.lib.Slides
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants

@Autonomous
class Auto : LinearOpMode() {
    //private val startPos = Pose(-32.0, -5.0*12.0, Math.toRadians(90.0))
    //note: the A and B points are beziers
    private val placePreloadPosA = Pose(19.0, 0.0, 0.0)
    private val placePreloadPos1 = Pose(31.5, 0.0, 0.0)
    private val placePreloadPos2 = Pose(31.5, 0.0, 0.0)
    private val push0StartPos = Pose(49.5, -44.5, 0.0)
    private val push0BezierPosA = Pose(-10.5, -56.0, 0.0)//toward human player
    private val push0BezierPosB = Pose(53.0, -23.5, 0.0)
    private val push0EndPos = Pose(14.0, -46.5, 0.0)
    private val push1BezierPosA = Pose(70.0, -46.5, 0.0)
    private val push1StartPos = Pose(49.5, -56.5, 0.0)
    private val push1EndPos = Pose(14.0, -56.5, 0.0)
    private val push2BezierPosA = Pose(70.0, -52.0, 0.0)
    private val push2StartPos = Pose(49.5, -62.5, 0.0)
    private val push2EndPos = Pose(15.0, -62.5, 0.0)//47
    private val pickup0Pos = Pose(13.3, -35.0, 0.0)
    //
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
                depo.claw.position = 0.95
            } else if (gamepad1.right_bumper) {//11, 40.5
                depo.claw.position = 0.55
            }
        }
        ActionGroup(ActionSet(
            genPlacement(driver, pivot, slides, depo, telemetryA, usecloserpoint = true),

            genPushAlt(driver, pivot, slides, depo, telemetryA)
            /*ActionGroup(
                genPush(driver),
                Global.DiffyPosition.DiffySpecIntake.diffyPos.setAction(depo),
                Global.PivotPresets.WALL_PICKUP_AUTO.action(pivot, driver.follower, telemetryA),
                depo.setClaw(true),
                Global.SlidePresets.FWINTAKE_ALMOST_ALMOST.action(slides),
            ),

            genPickup(driver, pivot, slides, depo, telemetryA, true),
            genPlacement(driver, pivot, slides, depo, telemetryA, true),

            genPickup(driver, pivot, slides, depo, telemetryA),
            genPlacement(driver, pivot, slides, depo, telemetryA),

            genPickup(driver, pivot, slides, depo, telemetryA),
            genPlacement(driver, pivot, slides, depo, telemetryA),

            genPickup(driver, pivot, slides, depo, telemetryA),
            genPlacement(driver, pivot, slides, depo, telemetryA),*/
        ), pivot.action(telemetryA), slides.action(telemetryA), driverAction(driver), telemetryAction(telemetryA)).execute { isStopRequested }

        while (!isStopRequested && opModeIsActive()) {
            driver.update()
            driver.telemetryDebug(telemetryA)
        }
    }
    private fun genPickup(driver: Driver, pivot: Pivot, slides: Slides, depo: Depo, telemetry: MultipleTelemetry, useFarPickupPos: Boolean = false): ActionSet {
        return ActionSet(
            ActionGroup(
                if (!useFarPickupPos) driver.runToAction(pickup0Pos) else driver.runToAction(push2EndPos),
                Global.PivotPresets.WALL_PICKUP_AUTO.action(pivot, driver.follower, telemetry),
                Global.DiffyPosition.DiffySpecIntake.diffyPos.setAction(depo),
                depo.setClaw(true),
                Global.SlidePresets.FWINTAKE_ALMOST.action(slides),
            ),
            ensureMinTime(Global.SlidePresets.FWINTAKE.action(slides), 300),
            ensureMinTime(depo.setClaw(false), 1000),
            Global.PivotPresets.WALL_PICKUP_UP_AUTO.action(pivot),
            //Global.SlidePresets.RESET.action(slides)
        )
    }
    private fun genPlacement(driver: Driver, pivot: Pivot, slides: Slides, depo: Depo, tele: MultipleTelemetry, usebezier: Boolean = false, usecloserpoint: Boolean = false) : ActionSet {
        val ppp = if (usecloserpoint) placePreloadPos2 else placePreloadPos1
        return ActionSet(
            ActionGroup(
                if (usecloserpoint) ensureMinTime(depo.setClaw(false), 1500) else depo.setClaw(false),
                if (usebezier) driver.runToAction(ppp, placePreloadPosA) else driver.runToAction(ppp),
                ensureMinTime(Global.DiffyPosition.DiffySpecDepo.diffyPos.setAction(depo), 1000),
                Global.PivotPresets.SPEC_DEPOSIT.action(pivot, driver.follower, tele),
                Global.SlidePresets.HIGH_CHAMBER.action(slides)
            ),
            ensureMinTime(Global.SlidePresets.HIGH_CHAMBER_DROP_AUTO.action(slides), 100),
            ActionGroup (
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
    val testing = Pose(24.0, -3.0, Math.toRadians(135.0))
    val testingS = Pose(17.0, -38.0, Math.toRadians(45.0))
    val testing2 = Pose(24.0, -48.0, Math.toRadians(135.0))
    val testing2S = Pose(17.0, -43.0, Math.toRadians(45.0))
    val testing3 = Pose(24.0, -58.0, Math.toRadians(135.0))
    val testing3S = Pose(17.0, -38.0, Math.toRadians(45.0))
    private fun genPushAlt(driver: Driver, pivot: Pivot, slides: Slides, depo: Depo, tele: MultipleTelemetry) : Action {
        return ActionSet(
            //30
            driver.runToAction(Pose(24.5, 0.0, 0.0)),
            ActionGroup(
                driver.runToAction(testing, Pose(0.0, 10.0), Pose(20.0, -41.5)),
                Global.DiffyPosition.Diffy45.diffyPos.setAction(depo),
                Global.SlidePresets.SPEC_INTAKE.action(slides),
                Global.PivotPresets.RESET.action(pivot, driver.follower, tele),
            ),
            wait(500),
            depo.setClaw(false),
            ActionGroup(
                driver.runToAction(testingS),
                Global.SlidePresets.INTAKE.action(slides),
            ),
            depo.setClaw(true),
            ActionGroup(
                driver.runToAction(testing2, Pose(0.0, 10.0), Pose(20.0, -41.5)),
                Global.DiffyPosition.Diffy45.diffyPos.setAction(depo),
                Global.SlidePresets.SPEC_INTAKE.action(slides),
                Global.PivotPresets.RESET.action(pivot, driver.follower, tele),
            ),
            wait(500),
            depo.setClaw(false),
            ActionGroup(
                driver.runToAction(testing2S),
                Global.SlidePresets.INTAKE.action(slides),
            ),
            depo.setClaw(true),

            ActionGroup(
                driver.runToAction(testing3, Pose(0.0, 10.0), Pose(20.0, -41.5)),
                Global.DiffyPosition.Diffy45.diffyPos.setAction(depo),
                Global.SlidePresets.SPEC_INTAKE.action(slides),
                Global.PivotPresets.RESET.action(pivot, driver.follower, tele),
            ),
            wait(500),
            depo.setClaw(false),

            ActionGroup(
                driver.runToAction(testing3S),
                Global.SlidePresets.INTAKE.action(slides),
            ),
            depo.setClaw(true),
            wait(1000),
            ActionGroup(
                Global.SlidePresets.SPEC_INTAKE.action(slides),
                Global.PivotPresets.WALL_PICKUP_AUTO.action(pivot, driver.follower, tele)
            ),
        )
    }
    private fun telemetryAction(tele: MultipleTelemetry) : Action = object : Action {
            override fun init() {}

            override fun update(): Boolean {
                //tele.update()
                return false
            }
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