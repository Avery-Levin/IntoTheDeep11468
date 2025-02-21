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
import org.baylorschool.intothedeep.lib.Depo
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
        val driver = Driver(Follower(hardwareMap, FConstants::class.java, LConstants::class.java), Pose(0.0, 0.0, 0.0))

        val telemetryA = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        val pivot = Pivot(hardwareMap)
        val slides = Slides(hardwareMap)
        val depo = Depo(hardwareMap)
        telemetryA.addLine("Position selector: y is for slides, x is for pivot. ")
        telemetryA.addLine("Default is +1 per loop to the target.")
        telemetryA.addLine("Press left bumper to go backwards. ")
        telemetryA.addLine("Press right bumper to to 10x. Idk how helpful that will be tbh.")
        telemetryA.addLine("Press a/b for claw open/close. Dpad left/right to select diffy position")
        telemetryA.update()

        driver.holdPoint(Pose(0.0, 0.0))
        driver.update()

        waitForStart()
        //ActionGroup(ActionSet(
        //    Global.PivotPresets.HIGH_RUNG.action(pivot),
        //), pivot.action()).execute()
        var id = 0
        var wasPressedLeft = false
        var wasPressedRight = false
        var clawOpen = false
        while (!isStopRequested && opModeIsActive()) {
            //driver.update()
            //driver.telemetryDebug(telemetryA)
            telemetryA.addData("pivot-target", Global.PivotPIDConfig.target)
            telemetryA.addData("pivot-pos", pivot.pivotPos)
            telemetryA.addData("slides-target", Global.SlidePIDConfig.target)
            telemetryA.addData("slides-pos", slides.slidePos)
            telemetryA.addData("claw is open", clawOpen)
            telemetryA.addData("diffy target", Global.DiffyPosition.values()[id].name)
            telemetryA.update()

            Global.PivotPIDConfig.target += int(gamepad1.x) * multiplier(gamepad1)
            Global.SlidePIDConfig.target += int(gamepad1.y) * multiplier(gamepad1)
            if (gamepad1.a) {
                depo.openClaw()
            } else if (gamepad1.b) {
                depo.closeClaw()
            }
            if (!wasPressedLeft && gamepad1.dpad_left) {
                id--
                if (id < 0) id = Global.DiffyPosition.values().size-1
                depo.set(Global.DiffyPosition.values()[id])
            }
            wasPressedLeft = gamepad1.dpad_left
            if (!wasPressedRight && gamepad1.dpad_right) {
                id = (id + 1) % Global.DiffyPosition.values().size
                depo.set(Global.DiffyPosition.values()[id])
            }
            wasPressedRight = gamepad1.dpad_right
            if (gamepad1.dpad_left) {
                clawOpen = true
            } else if (gamepad1.dpad_right) {
                clawOpen = false
            }

            if (clawOpen) {
                depo.openClaw()
            } else {
                depo.closeClaw()
            }
            pivot.update()
            slides.update()
            driver.update()
        }
    }
}