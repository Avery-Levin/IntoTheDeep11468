package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.intothedeep.Global
import org.baylorschool.intothedeep.lib.Depo
import org.baylorschool.intothedeep.lib.FieldCentricMec
import org.baylorschool.intothedeep.lib.Mecanum
import org.baylorschool.intothedeep.lib.Pivot
import org.firstinspires.ftc.teamcode.lib.Slides

@TeleOp
class TuningTeleOp: LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val allHubs = hardwareMap.getAll(LynxModule::class.java)
        for (hub in allHubs) {
            hub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
        }
        val telemetryMultiple = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        val mecanum = FieldCentricMec(hardwareMap)
        val pivot = Pivot(hardwareMap)
        val slide = Slides(hardwareMap)
        val depo = Depo(hardwareMap)
        var loopTime = 0.0
        var loop: Double
        Global.PivotPIDConfig.useTeleopPID = true
        waitForStart()
        resetRuntime()
        while (opModeIsActive()) {
            loop = System.nanoTime().toDouble()

            for (hub in allHubs) {
                hub.clearBulkCache()
            }

            mecanum.mecanumLoop(gamepad1)
            depo.tuning(gamepad1)
            pivot.update()
            slide.update()
            slide.tuning(gamepad2)

            depo.telemetry(telemetryMultiple)
            pivot.telemetry(telemetryMultiple)
            slide.telemetry(telemetryMultiple)
            telemetryMultiple.addData("frequency (hz)", 1000000000 / (loop - loopTime))
            loopTime = loop
            telemetryMultiple.update()
        }
    }
}