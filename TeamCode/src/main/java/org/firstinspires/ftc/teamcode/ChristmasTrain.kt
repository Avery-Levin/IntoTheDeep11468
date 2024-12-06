package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.baylorschool.intothedeep.Global
import kotlin.math.absoluteValue

@TeleOp
class ChristmasTrain : LinearOpMode() {
    override fun runOpMode() {
         val flMotor = hardwareMap.get(DcMotor::class.java, Global.flMotorName)
         val frMotor = hardwareMap.get(DcMotor::class.java, Global.frMotorName)
         val blMotor = hardwareMap.get(DcMotor::class.java, Global.blMotorName)
         val brMotor = hardwareMap.get(DcMotor::class.java, Global.brMotorName)
        waitForStart()
        while(opModeIsActive()){
            flMotor.power = gamepad1.right_stick_y.toDouble().absoluteValue
            blMotor.power = gamepad1.right_stick_y.toDouble().absoluteValue
            frMotor.power = gamepad1.left_stick_y.toDouble().absoluteValue *-1
            brMotor.power = gamepad1.left_stick_y.toDouble().absoluteValue * -1
        }
    }
}