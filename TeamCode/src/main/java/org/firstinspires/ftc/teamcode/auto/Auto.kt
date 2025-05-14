package org.firstinspires.ftc.teamcode.auto
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.localization.Pose
import com.pedropathing.util.Constants
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.baylorschool.intothedeep.Action
import org.baylorschool.intothedeep.ActionGroup
import org.baylorschool.intothedeep.ActionSet
import org.baylorschool.intothedeep.Global
import org.baylorschool.intothedeep.ensureMinTime
import org.baylorschool.intothedeep.lib.Depo
import org.baylorschool.intothedeep.lib.Pivot
import org.baylorschool.intothedeep.startWithDelay
import org.baylorschool.intothedeep.switchAfterTrue
import org.baylorschool.intothedeep.wait
import org.firstinspires.ftc.teamcode.Driver
import org.firstinspires.ftc.teamcode.lib.Slides
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants

@Autonomous
class Auto : LinearOpMode() {
    //private val startPos = Pose(-32.0, -5.0*12.0, Math.toRadians(90.0))
    //note: the A and B points are beziers
    private val placePreloadPosA = Pose(15.0, -7.0, 0.0)
    private val placePreloadPos1 = Pose(30.5, -5.0, 0.0)//normal
    private val placePreloadPos2 = Pose(30.5, -5.0, 0.0)//pro
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
    private val pickup0Pos = Pose(13.0, -37.0, 0.0)
    private val pickup1Pos = Pose(13.0, -37.0, 0.0)

    override fun runOpMode() {
        Constants.setConstants(FConstants::class.java, LConstants::class.java)
        Global.PivotPIDConfig.useTeleopPID = false

        /*val allHubs = hardwareMap.getAll(LynxModule::class.java)
        for (hub in allHubs) {
            hub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
        }*/
        val driver = Driver(Follower(hardwareMap, FConstants::class.java, LConstants::class.java), Pose(-1.55, -8.0, 0.0))
        //driver.follower.t
        val telemetryA = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        val pivot = Pivot(hardwareMap)//up down
        val slides = Slides(hardwareMap)//forward back
        val depo = Depo(hardwareMap)//THE CLAW
        telemetryA.addLine("drivepid: " + FollowerConstants.drivePIDFCoefficients.toString())
        telemetry.addData("pivot target", 0.0)
        telemetry.addData("pivot distance", 0.0)
        telemetry.addData("pivot follower T", 0.0)
        telemetryA.update()
        //driver.update()
        pivot.offset = 0
        pivot.specDeposit()
        depo.diffySpec()
        depo.claw.position = 0.40
        //Global.PivotPresets.SPEC_DEPOSIT
        while (!isStarted()) {
            driver.follower.pose = Pose(-1.55, -8.0, 0.0)
            pivot.update()
            depo.claw.position = 0.92
            //driver.update()
        }
        //while (start + 5000 > System.currentTimeMillis()) {
        //    pivot.update()
        //}
        waitForStart()
        val start = System.currentTimeMillis()
        driver.follower.pose = Pose(-1.55, -8.0, 0.0)
        ActionGroup(ActionSet(
            genPlacement(driver, pivot, slides, depo, telemetryA, usecloserpoint = true),

            genPushAlt(driver, pivot, slides, depo, telemetryA),
            /*ActionGroup(
                genPush(driver),
                Global.DiffyPosition.DiffySpecIntake.diffyPos.setAction(depo),
                Global.PivotPresets.WALL_PICKUP_AUTO.action(pivot, driver.follower, telemetryA),
                depo.setClaw(true),
                Global.SlidePresets.FWINTAKE_ALMOST_ALMOST.action(slides),
            ),
             */

            genPickup(driver, pivot, slides, depo, telemetryA, useFarPickupPos = true),//true

            genPlacement(driver, pivot, slides, depo, telemetryA, usebezier = true),

            genPickup(driver, pivot, slides, depo, telemetryA),
            genPlacement(driver, pivot, slides, depo, telemetryA, usebezier = true),

            genPickup(driver, pivot, slides, depo, telemetryA),
            genPlacement(driver, pivot, slides, depo, telemetryA, usebezier = true),

            //genPickup(driver, pivot, slides, depo, telemetryA),
            //genPlacement(driver, pivot, slides, depo, telemetryA, usebezier = true),

            ActionGroup(
                driver.runToAction(Pose(4.0, -45.0, 0.0), Pose(5.0, -35.0, 0.0)),
                Global.PivotPresets.RESET.action(pivot)
            ),

            object : Action {
                override fun init() {
                    telemetryA.addLine("Total time: ${System.currentTimeMillis()-start}")
                }
                override fun update(): Boolean = true
            }
        ), pivot.action(telemetryA), slides.action(telemetryA), driverAction(driver, telemetryA), looptimeAction(telemetryA),
            //cacheClearAction(allHubs),
            telemetryAction(telemetryA)).execute { isStopRequested }

        while (!isStopRequested && opModeIsActive()) {
            driver.update()
            driver.telemetryDebug(telemetryA)
        }
    }

    private fun genPickup(driver: Driver, pivot: Pivot, slides: Slides, depo: Depo, telemetry: MultipleTelemetry, useFarPickupPos: Boolean = false): ActionSet {
        val spline = Pose(15.0, -37.0, 0.0)
        return ActionSet(
            ActionGroup(
                if (useFarPickupPos) driver.runToAction(pickup1Pos, spline) else driver.runToAction(pickup0Pos, spline),
                Global.PivotPresets.WALL_PICKUP_AUTO.action(pivot, driver.follower, telemetry, multi = 1.6),
                Global.DiffyPosition.DiffySpecIntake.diffyPos.setAction(depo),
                depo.setClaw(true),
                startWithDelay(Global.SlidePresets.FWINTAKE_ALMOST.action(slides), 500),
            ),
            ensureMinTime(startWithDelay(Global.SlidePresets.FWINTAKE.action(slides), 200), 450),
            ensureMinTime(depo.setClaw(false), 200),
            Global.PivotPresets.WALL_PICKUP_UP_AUTO.action(pivot),
            //Global.SlidePresets.RESET.action(slides)
        )
    }
    private fun genPlacement(driver: Driver, pivot: Pivot, slides: Slides, depo: Depo, tele: MultipleTelemetry, usebezier: Boolean = false, usecloserpoint: Boolean = false) : ActionSet {
        val ppp = if (usecloserpoint) placePreloadPos2 else placePreloadPos1
        return ActionSet(
            ActionGroup(
                //if (usecloserpoint) ensureMinTime(depo.setClaw(false), 1) else depo.setClaw(false),
                depo.setClaw(false),
                if (usebezier) driver.runToAction(ppp, placePreloadPosA) else driver.runToAction(ppp),
                Global.DiffyPosition.DiffySpecDepo.diffyPos.setAction(depo),
                Global.PivotPresets.SPEC_DEPOSIT.action(pivot, driver.follower, tele),
                Global.SlidePresets.HIGH_CHAMBER.action(slides)
            ),
            ensureMinTime(startWithDelay(Global.SlidePresets.HIGH_CHAMBER_DROP_AUTO.action(slides), 350), 550),
            ActionGroup (
                Global.SlidePresets.RESET.action(slides),
                depo.setClaw(true),
            ),
            object : Action {
                override fun init() {
                    placePreloadPos2.y += 0.25
                    placePreloadPos1.y += 0.25
                }
                override fun update(): Boolean = true
            }
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
    val testing = Pose(25.25, -39.0, Math.toRadians(135.0))
    val testingS = Pose(20.5, -40.0, Math.toRadians(45.0))
    val testing2 = Pose(24.75, -49.0, Math.toRadians(135.0))
    val testing2S = Pose(20.0, -45.0, Math.toRadians(45.0))
    val testing3 = Pose(26.0, -59.0, Math.toRadians(135.0))
    //val testing3S = Pose(17.0, -38.0, Math.toRadians(45.0))
    val testing3S = Pose(12.0, -51.0, 0.0)
    private fun genPushAlt(driver: Driver, pivot: Pivot, slides: Slides, depo: Depo, tele: MultipleTelemetry) : Action {
        return ActionSet(
            //30
            //driver.runToAction(Pose(24.5, 0.0, 0.0)),
            ActionGroup(
                driver.runToAction(testing, Pose(20.0, -5.0)),
                Global.DiffyPosition.DiffyAutoPickup45.diffyPos.setAction(depo),
                startWithDelay(Global.SlidePresets.SPEC_INTAKE.action(slides), 150),
                Global.PivotPresets.RESET.action(pivot, driver.follower, tele),
            ),
            depo.setClaw(false),

            ActionGroup(
                driver.runToAction(testingS),
                Global.SlidePresets.INTAKE.action(slides),
            ),
            depo.setClaw(true),

            ActionGroup(
                driver.runToAction(testing2, ),//Pose(0.0, 10.0), Pose(20.0, -41.5)),
                Global.DiffyPosition.DiffyAutoPickup45.diffyPos.setAction(depo),
                Global.SlidePresets.SPEC_INTAKE.action(slides),
                Global.PivotPresets.RESET.action(pivot, driver.follower, tele),
            ),
            depo.setClaw(false),
            startWithDelay(object : Action {
                override fun init() {slides.reset()}
                override fun update(): Boolean = true
            }, 100),
            ActionGroup(
                driver.runToAction(testing2S),//testing2S
                startWithDelay(Global.SlidePresets.INTAKE.action(slides), 50),
            ),
            //Global.SlidePresets.INTAKE.action(slides),
            depo.setClaw(true),

            ActionGroup(
                driver.runToAction(testing3, ),//Pose(0.0, 10.0), Pose(20.0, -41.5)),
                Global.DiffyPosition.DiffyAutoPickup45.diffyPos.setAction(depo),
                Global.SlidePresets.SPEC_INTAKE.action(slides),
                Global.PivotPresets.RESET.action(pivot, driver.follower, tele),
            ),
            depo.setClaw(false),
            ActionGroup(
                driver.runToAction(testing3S),
                Global.SlidePresets.RESET.action(slides),
            ),
            depo.setClaw(true),

            /*wait(1000),
            ActionGroup(
                driver.runToAction(pickup0Pos),
                Global.SlidePresets.SPEC_INTAKE.action(slides),
                Global.PivotPresets.WALL_PICKUP_AUTO.action(pivot, driver.follower, tele)
            ),*/
        )
    }
    private fun telemetryAction(tele: MultipleTelemetry) : Action = object : Action {
            override fun init() {}

            override fun update(): Boolean {
                tele.update()
                return false
            }
        }
    private fun driverAction(driver: Driver, tele: MultipleTelemetry) : Action {
        driver.update()
        return object : Action {
            override fun init() {}

            override fun update(): Boolean {
                driver.update()
                tele.addData("TEST-VELOCITY", driver.follower.velocity.magnitude)
                tele.addData("TEST-TVALUE", driver.follower.currentTValue)
                tele.addData("TEST-HEADING", driver.follower.pose.heading)
                //tele.addData("TEST-VELOCITY", driver.follower.velocity.magnitude)
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
    private fun looptimeAction(tele: MultipleTelemetry): Action = object : Action {
        var loopTime = 0.0
        var loop: Double = 0.0
        override fun init() {
            loop = 0.0
            loopTime = 0.0
        }

        override fun update(): Boolean {
            tele.addData("frequency (hz)", 1000000000 / (loop - loopTime))
            loopTime = loop
            loop = System.nanoTime().toDouble()
            return false
        }
    }

    private fun cacheClearAction(allHubs: List<LynxModule>): Action {
        return object : Action {
            override fun init() {}

            override fun update(): Boolean {
                for (hub in allHubs) {
                    hub.clearBulkCache()
                }
                return false
            }

        }
    }
}

// hawk bootah! and compile on that thang