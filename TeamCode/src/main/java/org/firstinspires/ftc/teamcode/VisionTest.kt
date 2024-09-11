package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.intothedeep.vision.Color
import org.baylorschool.intothedeep.vision.OverheadProcessor
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName

import org.firstinspires.ftc.vision.VisionPortal



@TeleOp(name = "Overhead Vision Test", group = "Concept")
class VisionTest : LinearOpMode() {
    override fun runOpMode() {
        //val telemetryMultiple = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        telemetry.addData("data", "ok")
        telemetry.update()
        val overheadProcessor = OverheadProcessor(Color.RED, telemetry)
        val visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap[WebcamName::class.java, "Webcam 1"], overheadProcessor)
        //visionPortal.resumeLiveView() ???
        //visionPortal.resumeStreaming() ???
        while (!isStarted) {
            //telemetry.addData("detections", overheadProcessor.lastDetection)
            //telemetry.update()
        }
        waitForStart()
        while (opModeIsActive()) {
            //telemetry.addData("detections", overheadProcessor.lastDetection)
            //telemetry.update()
            Thread.sleep(1)
        }
        //Select a target and drive to it??? We'll see I guess.
    }

}