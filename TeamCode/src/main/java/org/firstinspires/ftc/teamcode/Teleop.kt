package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.exception.RobotCoreException
import org.baylorschool.intothedeep.lib.Diffy
import org.firstinspires.ftc.teamcode.lib.Arm

@TeleOp
class TeleOp: LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val telemetryMultiple = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        val arm = Arm(hardwareMap)
        val diffy = Diffy(hardwareMap)
        waitForStart()

        while (opModeIsActive()) {
            arm.armLoop(gamepad2)
            arm.telemetry(telemetryMultiple)
            diffy.telemetry(telemetryMultiple)
            diffy.depositLoop(gamepad2)
            telemetryMultiple.update()
        }
    }
}