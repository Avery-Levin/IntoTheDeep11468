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
import com.qualcomm.robotcore.hardware.Gamepad
import org.baylorschool.intothedeep.ActionGroup
import org.baylorschool.intothedeep.ActionSet
import org.baylorschool.intothedeep.Global
import org.baylorschool.intothedeep.ensureMinTime
import org.baylorschool.intothedeep.lib.Pivot
import org.firstinspires.ftc.teamcode.Driver
import org.firstinspires.ftc.teamcode.lib.Slides
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants

@TeleOp
class AutoPivotTest : LinearOpMode() {
    fun multiplier(gp: Gamepad): Int {
        //left bumper is backwards, right is 10x speed
        return (if (gp.left_bumper) -1 else 1) * (if (gp.right_bumper) 10 else 1)
    }
    fun int(b: Boolean) = if (b) 1 else 0
    override fun runOpMode() {
        Constants.setConstants(FConstants::class.java, LConstants::class.java)
        val driver = Driver(Follower(hardwareMap), Pose(0.0, 0.0, 0.0))

        val telemetryA = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        val pivot = Pivot(hardwareMap)
        val slides = Slides(hardwareMap)
        telemetryA.addLine("Position selector: y is for slides, x is for pivot. ")
        telemetryA.addLine("Default is +1 per loop to the target.")
        telemetryA.addLine("Press left bumper to go backwards. ")
        telemetryA.addLine("Press right bumper to to 10x. Idk how helpful that will be tbh.")
        telemetryA.update()

        driver.holdPoint(Pose(0.0, 0.0))
        driver.update()

        waitForStart()
        //ActionGroup(ActionSet(
        //    Global.PivotPresets.HIGH_RUNG.action(pivot),
        //), pivot.action()).execute()
        var id = 0
        while (!isStopRequested && opModeIsActive()) {
            //driver.update()
            //driver.telemetryDebug(telemetryA)
            telemetryA.addData("pivot-target", Global.PivotPIDConfig.target)
            telemetryA.addData("pivot-pos", pivot.pivotPos)
            telemetryA.addData("slides-target", Global.SlidePIDConfig.target)
            telemetryA.addData("slides-pos", slides.slidePos)
            telemetryA.update()

            Global.PivotPIDConfig.target += int(gamepad1.x) * multiplier(gamepad1)
            Global.SlidePIDConfig.target += int(gamepad1.y) * multiplier(gamepad1)

            pivot.update()
            slides.update()
            driver.update()
        }
    }
}