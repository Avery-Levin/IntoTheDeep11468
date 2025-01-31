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
class ParkAuto : LinearOpMode() {
    override fun runOpMode() {
        Constants.setConstants(FConstants::class.java, LConstants::class.java)
        val driver = Driver(Follower(hardwareMap), Pose(0.0, 0.0, 0.0))

        val telemetryA = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        val pivot = Pivot(hardwareMap)//up down
        val slides = Slides(hardwareMap)//forward back
        val depo = Depo(hardwareMap)//THE CLAW
        telemetryA.update()
        driver.update()

        waitForStart()
        ActionGroup(ActionSet(
                driver.runToAction(Pose(1.0,12.0)),
        ), pivot.action(), slides.action(), driverAction(driver)).execute()
        driver.holdPoint(Pose(2.0,12.0))

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