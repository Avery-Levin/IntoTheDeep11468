package org.firstinspires.ftc.teamcode.lib

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

import org.baylorschool.intothedeep.controllers.PIDFController
import org.baylorschool.intothedeep.controllers.PIDCoefficients
import org.firstinspires.ftc.teamcode.lib.armPIDConfig.fg
import org.firstinspires.ftc.teamcode.lib.armPIDConfig.target
import org.firstinspires.ftc.teamcode.lib.armPIDConfig.p

@Config
object armPIDConfig {
    @JvmField var p: Double = 0.0175
    @JvmField var fg: Double = 0.25
    @JvmField var target: Double = 0.0
}
class Arm(hardwareMap: HardwareMap) {

    val ticks_per_degree =  537.7 / 360.0
    var correctedValue = target/ticks_per_degree
    val armMotor: DcMotorEx
    var armPos: Double = 0.0
    private val pControl = PIDCoefficients(p)
    private val controller = PIDFController(pControl)
    var armPower = 0.0
    private var offset = 0
    private val high: Int = 900
    private val low: Int = 0

    init {
        armMotor = hardwareMap.get(DcMotorEx::class.java, "armMotor")
        offset = armMotor.currentPosition
        armPos = armMotor.currentPosition.toDouble() - offset
        target = 0.0
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("arm Motor Position", armPos)
        telemetry.addData("Target Position", target)
        telemetry.addData("arm power",armMotor.power)
    }

    fun update() {
        correctedValue = target / ticks_per_degree
        armPos = armMotor.currentPosition.toDouble() - offset
        controller.targetPosition = target
        armPower = controller.update(armPos) + ((Math.cos(Math.toRadians(target/ticks_per_degree))) * fg)
    }

    private fun increaseTarget() {
        target += 5
    }

    private fun decreaseTarget() {
        target -= 5
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