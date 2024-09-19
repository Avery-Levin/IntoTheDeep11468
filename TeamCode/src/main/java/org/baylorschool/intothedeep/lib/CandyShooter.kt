package org.baylorschool.intothedeep.lib

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry


class CandyShooter(hardwareMap: HardwareMap) {
    val leftShooter: DcMotor
    val rightShooter: DcMotor
    val shooterServo: Servo
    var servoIsExtended : Boolean
    var shooterIsOn : Boolean
    var previousA : Boolean = false
    var previousB : Boolean = false
    var armIsExtended : Boolean = false
    init  {
        leftShooter = hardwareMap.get(DcMotor::class.java, "leftShooter")
        rightShooter = hardwareMap.get(DcMotor::class.java, "rightShooter")
        shooterServo = hardwareMap.get(Servo::class.java,"shooterServo")
        leftShooter.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rightShooter.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        servoIsExtended = false
        shooterIsOn = false


    }

    fun shooterOn() {
        leftShooter.power = 1.0
        rightShooter.power = 1.0
        shooterIsOn = true
    }

    fun shooterOff(){
        leftShooter.power = 0.0
        rightShooter.power = 0.0
        shooterIsOn = false
    }

    fun swingServo(){
        shooterServo.position += (1/12)
        armIsExtended = true
    }

    fun retractServo(){
        shooterServo.position -= (1/12)
        armIsExtended = false
    }







    fun shooterLoop(gamepad : Gamepad){
        if(gamepad.a && !previousA) {
            shooterOn()
            previousA = true
        } else if(!gamepad.a && previousA) {
            shooterOff()
            previousA = false
        }

        if(gamepad.b && !previousB){
            swingServo()
            previousB = true
        } else if(!gamepad.b && previousB) {
            retractServo()
            previousB = false
        }


    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Shooter is powered:",shooterIsOn)
        telemetry.addData("Servo arm is extended", armIsExtended)
    }


}