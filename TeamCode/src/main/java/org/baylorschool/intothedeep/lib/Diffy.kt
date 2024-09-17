package org.baylorschool.intothedeep.lib

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry

class Diffy(hardwareMap: HardwareMap) {
    val claw: Servo
    val diffy1: Servo
    val diffy2: Servo

    init {
        claw = hardwareMap.get(Servo::class.java, "claw")
        diffy1 = hardwareMap.get(Servo::class.java, "diffy1")
        diffy2 = hardwareMap.get(Servo::class.java, "diffy2")
        diffy2.direction = Servo.Direction.REVERSE
        closeClaw()
    }

    fun telemetry(telemetry: Telemetry){
        telemetry.addData("diffy1 pos", diffy1.position)
        telemetry.addData("diffy2 pos", diffy2.position)
        telemetry.addData("claw pos", claw.position)
    }
    fun openClaw() {
        claw.position = 0.5
    }

    fun closeClaw() {
        claw.position = 0.0
    }

    fun diffyLeft() {
        diffy1.position -= 0.001
        diffy2.position += 0.001
    }

    fun diffyRight() {
        diffy1.position += 0.001
        diffy2.position -= 0.001
    }

    fun diffyUp() {
        diffy1.position -= 0.001
        diffy2.position -= 0.001
    }

    fun diffyDown() {
        diffy1.position += 0.001
        diffy2.position += 0.001
    }
    fun depositLoop(gamepad: Gamepad) {
        if (gamepad.y)
            openClaw()
        else if (gamepad.b)
            closeClaw()
        else if (gamepad.dpad_left)
            diffyLeft()
        else if (gamepad.dpad_right)
            diffyRight()
        else if (gamepad.a)
            diffyDown()
        else if (gamepad.y)
            diffyUp()
    }
}