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
    private val claw: Servo
    private val diffyL: Servo
    private val diffyR: Servo

    init {
        claw = hardwareMap.get(Servo::class.java, "claw")
        diffyL = hardwareMap.get(Servo::class.java, "diffyL")
        diffyR = hardwareMap.get(Servo::class.java, "diffyR")
        diffyL.direction = Servo.Direction.REVERSE
        claw.direction = Servo.Direction.REVERSE
        closeClaw()
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("diffyL pos", diffyL.position)
        telemetry.addData("diffyR pos", diffyR.position)
        telemetry.addData("claw pos", claw.position)
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
        diffyL.position -= 0.001
        diffyR.position += 0.001
    }

    fun diffy135() {
        Global.diffy135.set(diffyL, diffyR)
    }

    fun diffy180() {
        Global.diffy180.set(diffyL, diffyR)
    }

    fun diffySpec() {
        diffyL.position += 0.001
        diffyR.position += 0.001
    }

    fun diffyBasket() {
        Global.diffyBasket.set(diffyL, diffyR)
    }

    fun idle() {
        Global.diffyIdle.set(diffyL, diffyR)
    }
}