package org.baylorschool.intothedeep.lib

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry


class CandyShooter(hardwareMap: HardwareMap) {
    val leftShooter: DcMotor
    val rightShooter: DcMotor
    val shooterServo: Servo

    init  {
        leftShooter = hardwareMap.get(DcMotor::class.java, "leftShooter")
        rightShooter = hardwareMap.get(DcMotor::class.java, "rightShooter")
        shooterServo = hardwareMap.get(Servo::class.java,"shooterServo")
        leftShooter.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rightShooter.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        leftShooter.direction = DcMotorSimple.Direction.REVERSE
        shooterServo.direction = Servo.Direction.REVERSE
    }

    fun shooterOn() {
        leftShooter.power = 1.0
        rightShooter.power = 1.0
    }

    fun shooterOff(){
        leftShooter.power = 0.0
        rightShooter.power = 0.0
    }

    fun swingServo(){
        shooterServo.position = 0.0
    }

    fun retractServo(){
        shooterServo.position = 1.0
    }

    fun shooterLoop(gamepad : Gamepad){
        if (gamepad.right_bumper) {
            shooterOn()
        } else {
            shooterOff()
        }

        if (gamepad.a) {
            swingServo()
        } else {
            retractServo()
        }
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("shooter pos", shooterServo.position)
        telemetry.update()
    }
}