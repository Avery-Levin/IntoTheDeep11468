package org.baylorschool.intothedeep.lib

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.baylorschool.intothedeep.controllers.PIDCoefficients
import org.baylorschool.intothedeep.controllers.PIDFController
import org.baylorschool.intothedeep.lib.ArmPIDConfig.d
import org.baylorschool.intothedeep.lib.ArmPIDConfig.fg
import org.baylorschool.intothedeep.lib.ArmPIDConfig.i
import org.baylorschool.intothedeep.lib.ArmPIDConfig.p
import org.baylorschool.intothedeep.lib.ArmPIDConfig.target
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.cos


@Config
object ArmPIDConfig {
    @JvmField var p: Double = 0.0175
    @JvmField var i: Double = 0.001
    @JvmField var d: Double = 0.0008
    @JvmField var fg: Double = 0.25
    @JvmField var target: Double = 0.0


}

class Arm(hardwareMap: HardwareMap) {

    private val ticks_per_degree =  2786.2 / 360.0
    private var correctedValue = target/ticks_per_degree
    private val armMotor1: DcMotorEx
    private val armMotor2 : DcMotorEx
    private var armPos: Double = 0.0
    private val control = PIDCoefficients(p, i, d)
    val controller = PIDFController(control, /* kF = {x, v -> ((cos(Math.toRadians(correctedValue))) * fg)} */)
    private var armPower = 0.0
    private var offset = 0
    private val high: Int = 800
    private val low: Int = 20

    init {
        controller.targetPosition = target
        armMotor1 = hardwareMap.get(DcMotorEx::class.java, "armMotor1")
        armMotor2 = hardwareMap.get(DcMotorEx::class.java, "armMotor2")
        offset = armMotor1.currentPosition
        armPos = armMotor1.currentPosition.toDouble() - offset
        target = 0.0
        armMotor1.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("arm Motor Position", armPos)
        telemetry.addData("arm Target Position", target)
        telemetry.addData("arm power",armMotor1.power)
    }

    fun update() {
        controller.targetPosition = target
        correctedValue = target / ticks_per_degree
        armPos = armMotor1.currentPosition.toDouble() - offset
        armPower = controller.update(armPos) + ((cos(Math.toRadians(correctedValue))) * fg)
    }

    private fun increaseTarget() {
        target += 1.0
    }

    private fun decreaseTarget() {
        target = 0.0
    }

    fun armLoop(gamepad: Gamepad) {
        target = hardStops(target.toInt(), low, high).toDouble()
        update()
        armMotor1.power = armPower
        if (gamepad.dpad_up )
            increaseTarget()
        else if (gamepad.dpad_down)
            decreaseTarget()
    }

    private fun hardStops(value: Int, low: Int, high: Int): Int {
        return if (value < low) low + 1
        else if (value > high) high - 1
        else value
    }

}