package org.firstinspires.ftc.teamcode.lib

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.baylorschool.intothedeep.Action
import org.baylorschool.intothedeep.Global
import org.baylorschool.intothedeep.Global.PivotPIDConfig
import org.baylorschool.intothedeep.Global.SlidePIDConfig.fg
import org.baylorschool.intothedeep.Global.SlidePIDConfig.p
import org.baylorschool.intothedeep.Global.SlidePIDConfig.target
import org.baylorschool.intothedeep.controllers.PIDCoefficients
import org.baylorschool.intothedeep.controllers.PIDFController
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs

class Slides(hardwareMap: HardwareMap) {
    val ticks_per_degree = 384.5 / 360.0
    var correctedValue = target / ticks_per_degree
    val slideL: DcMotorEx
    val slideR : DcMotorEx
    var slidePos: Double = 0.0
    private val pControl = PIDCoefficients(p)
    private val controller = PIDFController(pControl)
    var slidePower = 0.0
    private val high: Int = 2300
    private val low: Int = 0

    init {
        slideL = hardwareMap.get(DcMotorEx::class.java, "slideL")
        slideR = hardwareMap.get(DcMotorEx::class.java, "slideR")
        slideL.direction = DcMotorSimple.Direction.REVERSE
        slideR.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

        slideL.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        slideR.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        slideL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        slideR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT

        slidePos = slideR.currentPosition.toDouble()
        target = 0.0
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("slide Motor Position", slidePos)
        telemetry.addData("Target Position", target)
        telemetry.addData("slide power", slideL.power)
    }

    fun update() {
        correctedValue = target / ticks_per_degree
        slidePos = (slideR.currentPosition.toDouble() * -1)
        controller.targetPosition = target
        slidePower = controller.update(slidePos) + fg
        slideL.power = slidePower
        slideR.power = slidePower
        target = Global.hardStops(target.toInt(), low, high).toDouble()
    }
    fun close(): Boolean {
        return abs(target - slidePos) < 30
    }

    fun reset() {
        target = Global.SlidePresets.RESET.pos
    }

    fun intake() {
        target = Global.SlidePresets.INTAKE.pos
    }

    fun highBasket() {
        target = Global.SlidePresets.HIGH_BASKET.pos
    }

    fun lowBasket() {
        target = Global.SlidePresets.LOW_BASKET.pos
    }

    fun lowChamber() {
        target = Global.SlidePresets.LOW_CHAMBER.pos
    }

    fun highChamber() {
        target = Global.SlidePresets.HIGH_CHAMBER.pos
    }

    fun specScore() {
        target = Global.SlidePresets.HIGH_CHAMBER_SNAP.pos
    }

    fun specIntake() {
        target = Global.SlidePresets.SPEC_INTAKE.pos
    }

    fun l2Hang() {
        PivotPIDConfig.target = Global.PivotPresets.LOW_RUNG.pos
    }

    fun action() : Action {
        val slides = this
        return object : Action {
            override fun init() {}

            override fun update(): Boolean {
                slides.update()
                return false
            }

        }
    }

    fun tuning(gamepad2: Gamepad?) {
        if (gamepad2!!.dpad_up) {
            target++
        } else if (gamepad2.dpad_down) {
            target--
        }
    }
}