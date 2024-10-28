package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.outoftheboxrobotics.photoncore.Photon
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.baylorschool.intothedeep.lib.ArmTest
import org.baylorschool.intothedeep.lib.Depo
import org.firstinspires.ftc.teamcode.lib.Arm

@TeleOp
class TeleOp2: LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val telemetryMultiple = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        val depo = Depo(hardwareMap)
        var loopTime = 0.0
        var loop: Double

        waitForStart()
        resetRuntime()
        while (opModeIsActive()) {
            loop = System.nanoTime().toDouble()
            depo.depositLoop(gamepad2)
            depo.telemetry(telemetryMultiple)
            telemetryMultiple.addData("frequency (hz):", 1000000000 / (loop - loopTime))
            loopTime = loop
            telemetryMultiple.update()
        }
    }
}