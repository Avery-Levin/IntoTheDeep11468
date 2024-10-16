package org.firstinspires.ftc.teamcode.lib

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

import org.baylorschool.intothedeep.controllers.PIDFController
import org.baylorschool.intothedeep.controllers.PIDCoefficients
import org.baylorschool.intothedeep.lib.PIDConfig
import org.baylorschool.intothedeep.lib.PIDConfig.target
import org.baylorschool.intothedeep.lib.PIDConfig.p
import org.baylorschool.intothedeep.lib.PIDConfig.i
import org.baylorschool.intothedeep.lib.PIDConfig.d
import org.baylorschool.intothedeep.lib.PIDConfig.fg



abstract class PIDActuator(hardwareMap: HardwareMap) {
    var config = PIDConfig()
    var ticks_per_unit : Double? = null
    var correctedValue : Double? = target/ ticks_per_unit!!
    val actuatorMotor: DcMotorEx
    var actuatorPos: Double = 0.0
    private val pControl = PIDCoefficients(p, i,d)
    private val controller = PIDFController(pControl)
    var actuatorPower = 0.0
    private var offset = 0
    var motorName : String? = "actuatorMotor"
    var distancePerClick = 0.4

    init {
        actuatorMotor = hardwareMap.get(DcMotorEx::class.java, motorName)
        offset = actuatorMotor.currentPosition
        actuatorPos = actuatorMotor.currentPosition.toDouble() - offset
        target = 0.0
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("actuator Motor Position", actuatorPos)
        telemetry.addData("Target Position", target)
        telemetry.addData("power",actuatorMotor.power)
    }

    fun update() {
        correctedValue = target / ticks_per_unit!!
        actuatorPos = actuatorMotor.currentPosition.toDouble() - offset
        controller.targetPosition = target
        actuatorPower = controller.update(actuatorPos) + ((Math.cos(Math.toRadians(target / ticks_per_unit!!))) * fg)
    }

    private fun increaseTarget() {

        target += distancePerClick
    }

    private fun decreaseTarget() {
        target -= distancePerClick
    }

    fun actuatorLoop(gamepad: Gamepad) {
        update()
        actuatorMotor.power = actuatorPower

        if (gamepad.dpad_up)
            increaseTarget()
        else if (gamepad.dpad_down)
            decreaseTarget()
    }

}