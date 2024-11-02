package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.intothedeep.lib.Mecanum
import org.baylorschool.intothedeep.vision.Color
import org.baylorschool.intothedeep.vision.OverheadProcessor
import org.baylorschool.intothedeep.vision.findClosest
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.allowanceTuning.allowance

import org.firstinspires.ftc.vision.VisionPortal


@Config
object allowanceTuning {
    @JvmField var allowance = -40
}

@TeleOp(name = "Overhead Vision Test", group = "Concept")
class VisionTest : LinearOpMode() {
    override fun runOpMode() {
        val telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        telemetry.addData("data", "ok")
        telemetry.update()
        val overheadProcessor = OverheadProcessor(Color.RED, telemetry)
        val visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap[WebcamName::class.java, "Webcam 1"], overheadProcessor)
        FtcDashboard.getInstance().startCameraStream(visionPortal, 0.0)
        val mecanum = Mecanum(hardwareMap)
        //visionPortal.resumeLiveView() ???
        //visionPortal.resumeStreaming() ???
        loop@while (!isStarted) {
            Thread.sleep(100)
            //telemetry.addData("detections", overheadProcessor.lastDetection)
            val centerobj = findClosest(overheadProcessor.lastDetection, overheadProcessor.width, overheadProcessor.height)
            if (centerobj == null) {
                telemetry.addData("center", "null")
                telemetry.update()
                continue@loop
            }
            telemetry.addData("center", centerobj)
            val center = centerobj!!.middle
            val centerx = overheadProcessor.width / 2
            val centery = overheadProcessor.height / 2
            var weGoUp = center.y - 75 > centery//kinda a Ghostbusters reference hehe
            var weGoDown = center.y + 75 < centery
            var weGoLeft = center.x - 75 > centerx
            var weGoRight = center.x + 75 < centerx
            telemetry.addData("weGoUp", weGoUp)
            telemetry.addData("weGoDown", weGoDown)
            telemetry.addData("weGoLeft", weGoLeft)
            telemetry.addData("weGoRight", weGoRight)
            telemetry.addData("fw", if (weGoDown) 1.0F else if (weGoUp) -1.0F else 0.0F)
            telemetry.addData("lr", if (weGoRight) 1.0F else if (weGoLeft) -1.0F else 0.0F)
            telemetry.update()
        }
        waitForStart()
        var loopTime = 0.0
        var loop: Double
        loop@while (opModeIsActive()) {
            loop = System.nanoTime().toDouble()
            //telemetry.addData("detections", overheadProcessor.lastDetection)
            val centerobj = findClosest(overheadProcessor.lastDetection, overheadProcessor.width, overheadProcessor.height)
            if (centerobj == null) {
                telemetry.addData("center", "null")
                telemetry.update()
                mecanum.softwareDefinedLoop(
                        0.0F,
                        0.0F,
                        0.0F,
                        false
                )
                continue@loop
            }
            telemetry.addData("middle", centerobj.middle)
            val angle = centerobj.getAngle();
            telemetry.addData("middle angle", angle)
            val center = centerobj.middle
            val centerx = overheadProcessor.width / 2
            val centery = overheadProcessor.height / 2
            telemetry.addData("cx", centerx)
            telemetry.addData("cy", centery)

            var weGoUp = center.y - allowance < centery
            var weGoDown = center.y + allowance > centery
            var weGoLeft = center.x - allowance < centerx
            var weGoRight = center.x + allowance > centerx
            telemetry.addData("weGoUp", weGoUp)
            telemetry.addData("weGoDown", weGoDown)
            telemetry.addData("weGoLeft", weGoLeft)
            telemetry.addData("weGoRight", weGoRight)
            telemetry.addData("fw", if (weGoDown) 1.0F else 0.0F + if (weGoUp) -1.0F else 0.0F)
            telemetry.addData("lr", if (weGoRight) 1.0F else 0.0F + if (weGoLeft) -1.0F else 0.0F)
            mecanum.softwareDefinedLoop(
                    (if (weGoDown) 1.0F else 0.0F + if (weGoUp) -1.0F else 0.0F)/-6.0F,
                    (if (weGoRight) 1.0F else 0.0F + if (weGoLeft) -1.0F else 0.0F)/3.0F,
                    if (angle < 75) {0.25F} else if (angle > 105) {-0.25F} else {0.0F},
                    false
            )
            telemetry.addData("freq (Hz)", 1000000000/(loop-loopTime))
            loopTime = loop
            telemetry.update()
        }
        //Select a target and drive to it??? We'll see I guess.
    }

}