package org.firstinspires.ftc.teamcode.lib

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.baylorschool.intothedeep.controllers.PIDCoefficients
import org.baylorschool.intothedeep.controllers.PIDFController
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.lib.ExtendoPIDConfig.d
import org.firstinspires.ftc.teamcode.lib.ExtendoPIDConfig.fg
import org.firstinspires.ftc.teamcode.lib.ExtendoPIDConfig.i
import org.firstinspires.ftc.teamcode.lib.ExtendoPIDConfig.p
import org.firstinspires.ftc.teamcode.lib.ExtendoPIDConfig.target

@Config
object ExtendoPIDConfig {
    @JvmField var p: Double = 0.04
    @JvmField var i : Double = 0.0
    @JvmField var d : Double = 0.0
    @JvmField var fg: Double = 0.05
    @JvmField var target: Double = 0.0
}
class Extendo(hardwareMap: HardwareMap) {

    val ticks_per_degree = 537.7 / 360.0
    var correctedValue = target / ticks_per_degree
    val slideMotor1: DcMotorEx
    val slideMotor2 : DcMotorEx
    var slidePos: Double = 0.0
    private val pControl = PIDCoefficients(p, i ,d)
    private val controller = PIDFController(pControl)
    var slidePower = 0.0
    private var offset = 0
    private val high: Int = 3100
    private val low: Int = 0

    init {
        slideMotor1 = hardwareMap.get(DcMotorEx::class.java, "slideMotor1")
        slideMotor2 = hardwareMap.get(DcMotorEx::class.java, "slideMotor2")
        slideMotor1.direction = DcMotorSimple.Direction.REVERSE
        offset = slideMotor1.currentPosition
        slidePos = slideMotor1.currentPosition.toDouble() - offset
        target = 0.0
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("slide Motor Position", slidePos)
        telemetry.addData("Target Position", target)
        telemetry.addData("slide power", slideMotor1.power)
    }

    fun update() {
        correctedValue = target / ticks_per_degree
        slidePos = slideMotor1.currentPosition.toDouble() - offset
        controller.targetPosition = target
        slidePower =
            controller.update(slidePos) + fg
    }

    private fun increaseTarget() {
        target += 15.0
    }

    private fun decreaseTarget() {
        target = 0.0
    }

    fun slideLoop(gamepad: Gamepad) {
        target = hardStops(target.toInt(), low, high).toDouble()
        update()
        slideMotor1.power = slidePower
        slideMotor2.power = slidePower
        if (gamepad.dpad_right)
            increaseTarget()
        else if (gamepad.dpad_left)
            decreaseTarget()
    }

    private fun hardStops(value: Int, low: Int, high: Int): Int {
        return if (value < low) low + 1
        else if (value > high) high - 1
        else value
    }

}