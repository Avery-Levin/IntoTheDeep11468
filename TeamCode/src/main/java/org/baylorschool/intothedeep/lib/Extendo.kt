package org.firstinspires.ftc.teamcode.lib

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

import org.baylorschool.intothedeep.controllers.PIDFController
import org.baylorschool.intothedeep.controllers.PIDCoefficients
import org.firstinspires.ftc.teamcode.lib.ExtendoPIDConfig.fg
import org.firstinspires.ftc.teamcode.lib.ExtendoPIDConfig.target
import org.firstinspires.ftc.teamcode.lib.ExtendoPIDConfig.p

@Config
object ExtendoPIDConfig {
    @JvmField var p: Double = 0.0175
    @JvmField var fg: Double = 0.0
    @JvmField var target: Double = 0.0
}
class Extendo(hardwareMap: HardwareMap) {

    val ticks_per_degree = 537.7 / 360.0
    var correctedValue = target / ticks_per_degree
    val slideMotor: DcMotorEx
    var slidePos: Double = 0.0
    private val pControl = PIDCoefficients(p)
    private val controller = PIDFController(pControl)
    var slidePower = 0.0
    private var offset = 0

    init {
        slideMotor = hardwareMap.get(DcMotorEx::class.java, "slideMotor")
        offset = slideMotor.currentPosition
        slidePos = slideMotor.currentPosition.toDouble() - offset
        target = 0.0
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("slide Motor Position", slidePos)
        telemetry.addData("Target Position", target)
        telemetry.addData("power", slideMotor.power)
    }

    fun update() {
        correctedValue = target / ticks_per_degree
        slidePos = slideMotor.currentPosition.toDouble() - offset
        controller.targetPosition = target
        slidePower =
            controller.update(slidePos) + fg
    }

    private fun increaseTarget() {

        target += .5
    }

    private fun decreaseTarget() {
        target -= .5
    }

    fun slideLoop(gamepad: Gamepad) {
        update()
        slideMotor.power = slidePower

        if (gamepad.dpad_up)
            increaseTarget()
        else if (gamepad.dpad_down)
            decreaseTarget()
    }

}