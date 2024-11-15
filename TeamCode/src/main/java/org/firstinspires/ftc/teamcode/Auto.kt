package org.firstinspires.ftc.teamcode

/*import org.baylorschool.intothedeep.roadsrunner.drive.SampleMecanumDrive
import org.baylorschool.intothedeep.roadsrunner.drive.StandardTrackingWheelLocalizer
import org.baylorschool.intothedeep.roadsrunner.util.Encoder*/
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.pedroPathing.follower.Follower
import org.firstinspires.ftc.teamcode.pedroPathing.localization.Pose

@TeleOp()
class Auto : LinearOpMode() {
    private val startPos = Pose(-32.0, -5.0*12.0, Math.toRadians(90.0))
    private val place0Pos = Pose(0.0, -37.0, Math.toRadians(90.0))
    private val pickupBezierPoint = Pose(24.0 + 6.0, -48.0 + 6.0, Math.toRadians(90.0))
    private val pickup0Pos = Pose(36.0, -24.0, Math.toRadians(0.0))
    private val pickup1Pos = Pose(36.0 + 10, -24.0, Math.toRadians(0.0))
    private val pickup2Pos = Pose(36.0 + 20, -24.0, Math.toRadians(0.0))
    private val basketPos = Pose(-50.0, -50.0, Math.toRadians(180.0 + 45))
    private val endPos = Pose(36.0, 0.0, Math.toRadians(0.0))
    override fun runOpMode() {
        val driver = Driver(Follower(hardwareMap), startPos)

        val telemetryA = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        telemetryA.update()

        driver.update()

        waitForStart()
        ActionSet(
                driver.runToAction(place0Pos),
                driver.runToAction(pickup0Pos, pickupBezierPoint.asPoint()),
                driver.runToAction(pickupBezierPoint),
                driver.runToAction(basketPos),
                driver.runToAction(pickup1Pos, pickupBezierPoint.asPoint()),
                driver.runToAction(basketPos, pickupBezierPoint.asPoint()),
                driver.runToAction(pickup2Pos, pickupBezierPoint.asPoint()),
                driver.runToAction(basketPos, pickupBezierPoint.asPoint()),
                driver.runToAction(pickupBezierPoint),
                driver.runToAction(endPos, pickup0Pos.asPoint()),
        ).execute()
        driver.holdPoint(endPos)

        while (!isStopRequested && opModeIsActive()) {
            driver.update()
            driver.telemetryDebug(telemetryA)
        }
    }
}