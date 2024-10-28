package org.baylorschool.intothedeep.lib

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry

class Depo(hardwareMap: HardwareMap) {
    /* val arm1: Servo
    val arm2: Servo
    val claw: Servo
     */
    val diffy1: Servo
    val diffy2: Servo

    init {
        /*
        arm1 = hardwareMap.get(Servo::class.java, "arm1")
        arm2 = hardwareMap.get(Servo::class.java, "arm2")
        claw = hardwareMap.get(Servo::class.java, "claw")
         */
        diffy1 = hardwareMap.get(Servo::class.java, "diffy1")
        diffy2 = hardwareMap.get(Servo::class.java, "diffy2")
        diffy2.direction = Servo.Direction.REVERSE
      //  closeClaw()
    }

    fun telemetry(telemetry: Telemetry) {
       // telemetry.addData("arm1 pos", arm1.position)
       // telemetry.addData("arm2 pos", arm2.position)
        telemetry.addData("diffy1 pos", diffy1.position)
        telemetry.addData("diffy2 pos", diffy2.position)
     //   telemetry.addData("claw pos", claw.position)

    }

    /*
    fun openClaw() {
        claw.position = 0.8
    }

    fun closeClaw() {
        claw.position = 0.0
    }

     */
    fun diffyLeft() {
        diffy1.position -= 0.0005
        diffy2.position += 0.0005
    }

    fun diffyRight() {
        diffy1.position += 0.0005
        diffy2.position -= 0.0005
    }

    fun diffyUp() {
        diffy1.position += 0.0005
        diffy2.position += 0.0005
    }

    fun diffyDown() {

        diffy1.position -= 0.0005
        diffy2.position -= 0.0005
    }

    fun half() {
        diffy1.position = 0.5
        diffy2.position = 0.5
    }

    /*
    fun armUp() {
        arm1.position -= 0.005
        arm2.position -= 0.005
    }

    fun armDown() {
        arm1.position += 0.005
        arm2.position += 0.005
    }

    fun halfArm() {
        arm1.position = 0.5
        arm2.position = 0.5
    }

     */
    fun depositLoop(gamepad: Gamepad) {
        if (gamepad.right_bumper)
          //  openClaw()
        else if (gamepad.left_bumper)
           // closeClaw()
        else if (gamepad.dpad_left)
            diffyLeft()
        else if (gamepad.dpad_right)
            diffyRight()
        else if (gamepad.dpad_up)
            diffyUp()
        else if (gamepad.dpad_down)
            diffyDown()
        else if (gamepad.left_stick_button)
            half()
        else if (gamepad.right_stick_button) {
         //   halfArm()
        } else if (gamepad.y) {
          //  armUp()
        } else if (gamepad.a) {
           // armDown()
        }
    }
}