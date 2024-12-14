package org.firstinspires.ftc.teamcode.lib

import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.baylorschool.intothedeep.Global
import org.baylorschool.intothedeep.Global.SlidePIDConfig.fg
import org.baylorschool.intothedeep.Global.SlidePIDConfig.p
import org.baylorschool.intothedeep.Global.SlidePIDConfig.target
import org.baylorschool.intothedeep.controllers.PIDCoefficients
import org.baylorschool.intothedeep.controllers.PIDFController
import org.firstinspires.ftc.robotcore.external.Telemetry

class Slides(hardwareMap: HardwareMap) {
    val ticks_per_degree = 384.5 / 360.0
    var correctedValue = target / ticks_per_degree
    val slideL: DcMotorEx
    val slideR : DcMotorEx
    var slidePos: Double = 0.0
    private val pControl = PIDCoefficients(p)
    private val controller = PIDFController(pControl)
    var slidePower = 0.0
    private var offset = 0
    private val high: Int = 2300
    private val low: Int = 0

    init {
        slideL = hardwareMap.get(DcMotorEx::class.java, "slideL")
        slideR = hardwareMap.get(DcMotorEx::class.java, "slideR")
        slideL.direction = DcMotorSimple.Direction.REVERSE
        offset = slideR.currentPosition * -1
        slidePos = slideR.currentPosition.toDouble() - offset
        target = 0.0
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("slide Motor Position", slidePos)
        telemetry.addData("Target Position", target)
        telemetry.addData("slide power", slideL.power)
    }

    fun update() {
        correctedValue = target / ticks_per_degree
        slidePos = (slideR.currentPosition.toDouble() * -1) - offset
        controller.targetPosition = target
        slidePower = controller.update(slidePos) + fg
        slideL.power = slidePower
        slideR.power = slidePower
    }

    private fun increaseTarget() {
        target = Global.SlidePresets.HIGH_BASKET.pos
    }

    private fun decreaseTarget() {
        target = Global.SlidePresets.RESET.pos
    }

    fun slideLoop(gamepad: Gamepad) {
        target = Global.hardStops(target.toInt(), low, high).toDouble()
        update()
        if (gamepad.dpad_right)
            increaseTarget()
        else if (gamepad.dpad_left)
            decreaseTarget()
    }


}