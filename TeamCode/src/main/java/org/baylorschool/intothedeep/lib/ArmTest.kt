package org.baylorschool.intothedeep.lib

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry

class ArmTest(hardwareMap: HardwareMap) {
    val arm1: Servo
    val arm2: Servo

    init {
        arm1 = hardwareMap.get(Servo::class.java, "arm1")
        arm2 = hardwareMap.get(Servo::class.java, "arm2")
    }

    fun telemetry(telemetry: Telemetry){
        telemetry.addData("arm1 pos", arm1.position)
        telemetry.addData("arm2 pos", arm2.position)
    }

    fun armUp() {
        arm1.position -= 0.005
        arm2.position -= 0.005
    }

    fun armDown() {
        arm1.position += 0.005
        arm2.position += 0.005
    }

    fun half() {
        arm1.position = 0.5
        arm2.position = 0.5
    }

    fun depositLoop(gamepad: Gamepad) {
        if (gamepad.dpad_up)
            armUp()
        else if (gamepad.dpad_down)
            armDown()
        else if (gamepad.a)
            half()
    }
}