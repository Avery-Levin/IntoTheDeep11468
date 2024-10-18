package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import kotlin.math.abs
import kotlin.math.max


@TeleOp
class MecanumTeleOp : LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        // Declare our motors
        // Make sure your ID's match your configuration
        val frontLeftMotor = hardwareMap.dcMotor["fl"]
        val backLeftMotor = hardwareMap.dcMotor["bl"]
        val frontRightMotor = hardwareMap.dcMotor["fr"]
        val backRightMotor = hardwareMap.dcMotor["br"]
        var rightSwitchState : Boolean = false
        var previousGamepad2 = Gamepad()
        var speed : Double = 1.0

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.direction = DcMotorSimple.Direction.REVERSE
        backRightMotor.direction = DcMotorSimple.Direction.REVERSE

        frontRightMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        frontLeftMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        backRightMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        backLeftMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        waitForStart()

        if (isStopRequested) return

        while (opModeIsActive()) {
            var y = -gamepad1.left_stick_y.toDouble() // Remember, Y stick value is reversed
            var x = gamepad1.left_stick_x * 1.1 // Counteract imperfect strafing
            var rx = gamepad1.right_stick_x.toDouble()

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            var denominator = max(abs(y) + abs(x) + abs(rx), 1.0)
            var frontLeftPower = (y + x + rx) / denominator
            var backLeftPower = (y - x + rx) / denominator
            var frontRightPower = (y - x - rx) / denominator
            var backRightPower = (y + x - rx) / denominator
            previousGamepad2.copy(gamepad2)
            if(gamepad2.right_bumper && !previousGamepad2.right_bumper){
                rightSwitchState = !rightSwitchState
            }

            if (gamepad2.y){
                speed = 0.2
            } else if(gamepad2.b){
                speed = 0.4
            } else if(gamepad2.a){
                speed = 0.6
            } else if (gamepad2.x){
                speed = 1.0
            }

            if(rightSwitchState){
                speed = 0.0
            }


            frontLeftMotor.power = frontLeftPower * speed
            backLeftMotor.power = backLeftPower * speed
            frontRightMotor.power = frontRightPower * speed
            backRightMotor.power = backRightPower * speed

            telemetry.addLine(frontLeftPower.toString())
            telemetry.addLine(frontRightPower.toString())
            telemetry.addLine(backLeftPower.toString())
            telemetry.addLine(backRightPower.toString())

            telemetry.addLine(speed.toString())

            telemetry.addLine(rightSwitchState.toString())
            telemetry.update()
        }
    }
}