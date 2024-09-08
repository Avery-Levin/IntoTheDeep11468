package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.baylorschool.intothedeep.vision.Color
import org.baylorschool.intothedeep.vision.OverheadProcessor
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName

import org.firstinspires.ftc.vision.VisionPortal




class VisionTest : LinearOpMode() {
    override fun runOpMode() {
        telemetry.addData("data", "ok")
        telemetry.update()
        val overheadProcessor = OverheadProcessor(Color.RED)
        val visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap[WebcamName::class.java, "Webcam 1"], overheadProcessor)
        //visionPortal.resumeLiveView() ???
        //visionPortal.resumeStreaming() ???
        while (!isStarted) {
            telemetry.addData("detections", overheadProcessor.lastDetection)
            telemetry.update()
        }
        waitForStart()
        telemetry.addData("detections", overheadProcessor.lastDetection)
        telemetry.update()
        //Select a target and drive to it??? We'll see I guess.
    }

}