package org.baylorschool.intothedeep.lib

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator
import com.acmerobotics.roadrunner.profile.MotionState
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry

import org.baylorschool.intothedeep.controllers.PIDFController
import org.baylorschool.intothedeep.controllers.PIDCoefficients
import org.baylorschool.intothedeep.lib.ArmPIDConfig.target
import org.baylorschool.intothedeep.lib.ArmPIDConfig.p
import org.baylorschool.intothedeep.lib.ArmPIDConfig.i
import org.baylorschool.intothedeep.lib.ArmPIDConfig.d
import org.baylorschool.intothedeep.lib.ArmPIDConfig.fg

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
    val mpTime = ElapsedTime()
    private val ticks_per_degree =  2786.2 / 360.0
    private var correctedValue = target/ticks_per_degree
    private val armMotor: DcMotorEx
    private var armPos: Double = 0.0
    private val control = PIDCoefficients(p, i, d)
    val controller = PIDFController(control, /* kF = {x, v -> ((cos(Math.toRadians(correctedValue))) * fg)} */)
    private var armPower = 0.0
    private var offset = 0
    private val high: Int = 800
    private val low: Int = 20

    init {
        controller.targetPosition = target
        armMotor = hardwareMap.get(DcMotorEx::class.java, "armMotor")
        offset = armMotor.currentPosition
        armPos = armMotor.currentPosition.toDouble() - offset
        target = 0.0
        armMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("arm Motor Position", armPos)
        telemetry.addData("arm Target Position", target)
        telemetry.addData("arm power",armMotor.power)
    }

    fun update() {
        controller.targetPosition = target
        correctedValue = target / ticks_per_degree
        armPos = armMotor.currentPosition.toDouble() - offset
        /* val profile = MotionProfileGenerator.generateSimpleMotionProfile(
            MotionState(0.0, 0.0, 0.0),
            MotionState(target, 0.0, 0.0),
            0.0,
            0.0,
            0.0,
        )
        val state = profile[mpTime]

         controller.apply {
            targetPosition = state.x
            targetVelocity = state.v
            targetAcceleration = state.a
        }
         */
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
        armMotor.power = armPower
        if (gamepad.dpad_up)
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