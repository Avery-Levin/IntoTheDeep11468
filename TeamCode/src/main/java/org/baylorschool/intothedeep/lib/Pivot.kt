package org.baylorschool.intothedeep.lib

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.HardwareMap
import org.baylorschool.intothedeep.Action
import org.baylorschool.intothedeep.Global
import org.baylorschool.intothedeep.Global.PivotPIDConfig.d
import org.baylorschool.intothedeep.Global.PivotPIDConfig.fg
import org.baylorschool.intothedeep.Global.PivotPIDConfig.i
import org.baylorschool.intothedeep.Global.PivotPIDConfig.p
import org.baylorschool.intothedeep.Global.PivotPIDConfig.target
import org.baylorschool.intothedeep.controllers.PIDCoefficients
import org.baylorschool.intothedeep.controllers.PIDFController
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs
import kotlin.math.cos

class Pivot(hardwareMap: HardwareMap) {
    private val ticks_per_degree =  (2786.2/1.583333) / 180.0
    private var correctedValue = target/ticks_per_degree
    val pivotL: DcMotorEx
    private val pivotR : DcMotorEx
    private val switch: DigitalChannel
    var pivotPos: Double = 0.0
    private val control = PIDCoefficients(p, i, d)
    private val controller = PIDFController(control)
    private var armPower = 0.0
    var offset = 0
    private val high: Int = 1200
    private val low: Int = -1

    init {
        controller.targetPosition = target
        pivotL = hardwareMap.get(DcMotorEx::class.java, "pivotL")
        pivotR = hardwareMap.get(DcMotorEx::class.java, "pivotR")
        switch = hardwareMap.get(DigitalChannel::class.java, "switch")
        pivotL.direction = DcMotorSimple.Direction.REVERSE
        pivotL.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

        pivotL.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        pivotR.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        pivotL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        pivotR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT

        pivotPos = (pivotL.currentPosition.toDouble()) + offset
        target = 0.0
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("arm angle", pivotPos/ticks_per_degree)
        telemetry.addData("arm Motor Position", pivotPos)
        telemetry.addData("arm Target Position", target)
        telemetry.addData("arm power",pivotL.power)
        telemetry.addData("switch", switch.state)
    }

    fun update() {
        controller.targetPosition = target
        correctedValue = target / ticks_per_degree
        pivotPos = (pivotL.currentPosition.toDouble()) + offset
        armPower = controller.update(pivotPos) //* ((cos(Math.toRadians(correctedValue))) * fg))
        pivotL.power = armPower
        pivotR.power = armPower
        target = Global.hardStops(target.toInt(), low, high).toDouble()
        /*
        if (switch.state == true) {
            offset = -(pivotL.currentPosition)
        }
         */
    }
    
    fun close(): Boolean {
        return abs(target - pivotPos) < 30
    }
    fun close(pos: Double): Boolean {
        return abs(pos - pivotPos) < 30
    }

    fun reset() {
        target = Global.PivotPresets.RESET.pos
    }

    fun deposit() {
        target = Global.PivotPresets.DEPO.pos
    }

    fun specIntake() {
        target = Global.PivotPresets.WALL_PICKUP.pos
    }

    fun specDeposit() {
        target = Global.PivotPresets.SPEC_DEPOSIT.pos
    }

    fun l2Hang() {
        target = Global.PivotPresets.LOW_RUNG.pos
    }

    fun action(telemetry: MultipleTelemetry) : Action {
        val pivot = this
        return object : Action {
            override fun init() {}

            override fun update(): Boolean {
                pivot.update()
                pivot.telemetry(telemetry)
                return false
            }

        }
    }
}