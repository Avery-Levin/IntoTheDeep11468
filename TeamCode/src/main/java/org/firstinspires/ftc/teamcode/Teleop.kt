package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.outoftheboxrobotics.photoncore.Photon
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.intothedeep.lib.Depo
import org.baylorschool.intothedeep.lib.Mecanum
import org.baylorschool.intothedeep.lib.Arm
import org.baylorschool.intothedeep.lib.FieldCentricMec
import org.firstinspires.ftc.teamcode.lib.Extendo

@Photon
@TeleOp
class TeleOp: LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val telemetryMultiple = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
       // val arm = Arm(hardwareMap)
       // val extendo = Extendo(hardwareMap)
        val mecanum = FieldCentricMec(hardwareMap)
        //val depo = Depo(hardwareMap)
        var loopTime = 0.0
        var loop: Double

        waitForStart()
        resetRuntime()
        //arm.mpTime.reset()
        while (opModeIsActive()) {
            loop = System.nanoTime().toDouble()
            /* arm.armLoop(gamepad2)
            extendo.slideLoop(gamepad2)
             */
            mecanum.mecanumLoop(gamepad2)
            /* depo.depositLoop(gamepad2)
            arm.telemetry(telemetryMultiple)
            extendo.telemetry(telemetryMultiple)
             */
            mecanum.telemetry(telemetryMultiple)
            // depo.telemetry(telemetryMultiple)

            telemetryMultiple.addData("frequency (hz)", 1000000000 / (loop - loopTime))
            loopTime = loop

            telemetryMultiple.update()
        }
    }
}