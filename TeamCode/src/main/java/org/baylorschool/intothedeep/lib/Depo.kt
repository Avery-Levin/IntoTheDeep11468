package org.baylorschool.intothedeep.lib

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.baylorschool.intothedeep.Global
import org.firstinspires.ftc.robotcore.external.Telemetry

class DiffyPos (var left: Double, var right: Double) {
    fun set(diffyLeft: Servo, diffyRight: Servo) {
        diffyLeft.position = left
        diffyRight.position = right
    }
}

class Depo(hardwareMap: HardwareMap) {
    val claw: Servo
    private val diffyL: Servo
    private val diffyR: Servo

    init {
        claw = hardwareMap.get(Servo::class.java, "claw")
        diffyL = hardwareMap.get(Servo::class.java, "diffyL")
        diffyR = hardwareMap.get(Servo::class.java, "diffyR")
        diffyL.direction = Servo.Direction.REVERSE
        claw.direction = Servo.Direction.REVERSE
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("diffyL pos", diffyL.position)
        telemetry.addData("diffyR pos", diffyR.position)
        telemetry.addData("claw pos", claw.position)
    }

    fun set(diffyPosition: Global.DiffyPosition) {
        diffyPosition.diffyPos.set(diffyL, diffyR)
    }

    fun openClaw() {
        claw.position = Global.clawOpen
    }

    fun closeClaw() {
        claw.position = Global.clawClosed
    }

    fun diffy90() {
        Global.diffy90.set(diffyL, diffyR)
    }

    fun diffy45() {
        Global.diffy45.set(diffyL, diffyR)
    }

    fun diffy135() {
        Global.diffy135.set(diffyL, diffyR)
    }

    fun diffy180() {
        Global.diffy180.set(diffyL, diffyR)
    }

    fun diffyBasket() {
        Global.diffyBasket.set(diffyL, diffyR)
    }

    fun idle() {
        Global.diffyIdle.set(diffyL, diffyR)
    }

    fun retract() {
        Global.diffyRetract.set(diffyL, diffyR)
    }

    fun specIntake() {
        Global.diffySpecIntake.set(diffyL, diffyR)
    }
    fun diffySpec() {
        Global.diffySpecDepo.set(diffyL, diffyR)
    }

    fun tuning(gamepad: Gamepad) {
        if (gamepad.dpad_up) {
            diffyL.position += 0.0003
            diffyR.position += 0.0003
        } else if (gamepad.dpad_down) {
            diffyL.position -= 0.0003
            diffyR.position -= 0.0003
        } else if (gamepad.dpad_right) {
            diffyL.position -= 0.0003
            diffyR.position += 0.0003
        } else if (gamepad.dpad_left) {
            diffyL.position += 0.0003
            diffyR.position -= 0.0003
        } else if (gamepad.a) {
            diffy180()
        } else if (gamepad.left_bumper) {
            closeClaw()
        } else if (gamepad.right_bumper) {
            openClaw()
        } else if (gamepad.y) {
            idle()
        }
    }
}