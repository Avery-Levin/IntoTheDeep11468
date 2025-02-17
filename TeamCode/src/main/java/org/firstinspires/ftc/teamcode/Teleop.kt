package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.intothedeep.Global
import org.baylorschool.intothedeep.lib.Depo
import org.baylorschool.intothedeep.lib.FSM
import org.baylorschool.intothedeep.lib.FieldCentricMec
import org.baylorschool.intothedeep.lib.Mecanum
import org.baylorschool.intothedeep.lib.Pivot
import org.firstinspires.ftc.teamcode.lib.Slides

@TeleOp
class TeleOp: LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val allHubs = hardwareMap.getAll(LynxModule::class.java)
        for (hub in allHubs) {
            hub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
        }
        val telemetryMultiple = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        val pivot = Pivot(hardwareMap)
        val slide = Slides(hardwareMap)
        val mecanum = FieldCentricMec(hardwareMap)
        val depo = Depo(hardwareMap)
        val fsm = FSM(hardwareMap)
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
            fsm.loop(gamepad2, gamepad1)

            pivot.update()
            slide.update()

            depo.telemetry(telemetryMultiple)
            pivot.telemetry(telemetryMultiple)
            slide.telemetry(telemetryMultiple)
            mecanum.telemetry(telemetryMultiple)
            fsm.telemetry(telemetryMultiple)

            telemetryMultiple.addData("frequency (hz)", 1000000000 / (loop - loopTime))
            loopTime = loop
            telemetryMultiple.update()
        }
    }
}