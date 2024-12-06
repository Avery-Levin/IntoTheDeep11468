package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.acmerobotics.roadrunner.followers.PathFollower
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.intothedeep.ActionSet
import org.baylorschool.intothedeep.Action
import org.baylorschool.intothedeep.lib.Mecanum
import org.baylorschool.intothedeep.vision.Color
import org.baylorschool.intothedeep.vision.OverheadProcessor
import org.baylorschool.intothedeep.vision.Sample
import org.baylorschool.intothedeep.vision.cameraDataToRealPosition
import org.baylorschool.intothedeep.vision.findClosest
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.allowanceTuning.allowance
import org.firstinspires.ftc.teamcode.pedroPathing.follower.Follower
import org.firstinspires.ftc.teamcode.pedroPathing.localization.Pose
import org.firstinspires.ftc.teamcode.pedroPathing.util.Drawing

import org.firstinspires.ftc.vision.VisionPortal


@Config
object allowanceTuning {
    @JvmField var allowance = -40
}

@TeleOp(name = "Overhead Vision Test", group = "Concept")
class VisionTest : LinearOpMode() {
    override fun runOpMode() {
        val telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        try {
            telemetry.addData("data", "ok")
            telemetry.update()
            val overheadProcessor = OverheadProcessor(Color.RED, telemetry)
            val visionPortal = VisionPortal.easyCreateWithDefaults(
                    hardwareMap[WebcamName::class.java, "Webcam 1"], overheadProcessor)
            FtcDashboard.getInstance().startCameraStream(visionPortal, 1.0)
            //visionPortal.resumeLiveView() ???
            //visionPortal.resumeStreaming() ???
            //telemetry.addData("detections", overheadProcessor.lastDetection)
            waitForStart()
            var centerobj: Sample? = null
            while (centerobj == null) {
                centerobj = findClosest(overheadProcessor.lastDetection, overheadProcessor.width, overheadProcessor.height)
            }
            telemetry.addData("center", centerobj)
            val center = centerobj!!.middle
            telemetry.addData("width, height", "${overheadProcessor.width} ${overheadProcessor.height}")
            telemetry.update()
            val driver = Driver(Follower(hardwareMap), Pose(0.0, 0.0, 0.0))//55 is diagonal
            cameraDataToRealPosition(11.0, 0.0, overheadProcessor.width, overheadProcessor.height, center.x, center.y) { posX, posY, realAngleX, realAngleY, camWidthAngle, camHeightAngle ->
                //
                telemetry.addData("endData", "x: $posX, y: $posY, raX:$realAngleX, raY:$realAngleY, cWa(x):$camWidthAngle, cHa(y):$camHeightAngle , heading: ${centerobj.getAngle()}")
                telemetry.update()
                //Thread.sleep(25000)
                ActionSet (
                        //driver.runToAction(Pose(posY, posX, 0.0)),
                        driver.runToAction(Pose(0.0, 0.0, 0.0)),
                        object : Action {
                            override fun init() {
                                //driver.follower.holdPoint(Pose(posY, posX, 0.0))
                                //driver.follower.holdPoint(Pose(1.0, 0.0, 0.0))
                            }
                            override fun update(): Boolean {
                                driver.update()
                                Drawing.drawDebug(driver.follower)
                                return false
                            }
                        }
                ).execute()
                //Thread.sleep(25000)
            }
        } catch (t: Throwable) {
            telemetry.addData("error", t.toString())
            telemetry.update()
        }
    }
    //Select a target and drive to it??? We'll see I guess.
}