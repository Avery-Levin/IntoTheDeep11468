package org.baylorschool.intothedeep.lib

import com.pedropathing.localization.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.baylorschool.intothedeep.Global
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class FieldCentricMec(hardwareMap: HardwareMap) {
    private val flMotor: DcMotorEx
    private val frMotor: DcMotorEx
    private val blMotor: DcMotorEx
    private val brMotor: DcMotorEx
    private val odo: GoBildaPinpointDriver
    private var s: Double = 1.0
    private var y: Double = 0.0
    private var x: Double = 0.0
    private var turn: Double = 0.0
    private var botHeading: Double = 0.0
    private var offset: Double = 0.0
    private var rotX: Double = 0.0
    private var rotY: Double = 0.0
    private var denominator: Double = 0.0


    init {
        flMotor = hardwareMap.get(DcMotorEx::class.java, Global.flMotorName)
        blMotor = hardwareMap.get(DcMotorEx::class.java, Global.blMotorName)
        frMotor = hardwareMap.get(DcMotorEx::class.java, Global.frMotorName)
        brMotor = hardwareMap.get(DcMotorEx::class.java, Global.brMotorName)
        odo = hardwareMap.get(GoBildaPinpointDriver::class.java, Global.odo)

        frMotor.direction = DcMotorSimple.Direction.REVERSE
        brMotor.direction = DcMotorSimple.Direction.REVERSE

        brMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        frMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        blMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        flMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        odo.recalibrateIMU()
        offset = odo.heading
        botHeading = odo.heading - offset
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Front Left Power", flMotor.power)
        telemetry.addData("Front Right Power", frMotor.power)
        telemetry.addData("Back Left Power", blMotor.power)
        telemetry.addData("Back Right Power", brMotor.power)
        telemetry.addData("Robot Heading", botHeading)
        telemetry.addData("Pinpoint Frequency", odo.frequency)
    }
    /*
    fun softwareDefinedLoop(forward: Float, leftRight: Float, turn: Float, fast: Boolean) {
        y = forward
        x = (leftRight * 1.1).toFloat()
        this.turn = turn
        s = if (fast)
            1.0
        else
            0.4
        power()
    }

     */

    private fun reset() {
        offset = odo.heading
        botHeading = odo.heading - offset
    }

    fun mecanumLoop(gamepad1: Gamepad){
        odo.update(GoBildaPinpointDriver.readData.ONLY_UPDATE_HEADING)

        y = gamepad1.left_stick_y.toDouble()
        x = (-gamepad1.left_stick_x).toDouble()
        turn = gamepad1.right_stick_x.toDouble()

        if (gamepad1.right_bumper)
            s = 0.4
        else if (gamepad1.left_bumper)
            s = 1.0
        else if (gamepad1.start)
            reset()

        power()
    }

    fun power() {
        botHeading =  odo.heading - offset
        rotX = (x * cos(-botHeading) - y * sin(-botHeading)) * 1.1
        rotY = x * sin(-botHeading) + y * cos(-botHeading)
        denominator = max(abs(rotY) + abs(rotX) + abs(turn), 1.0)

        flMotor.power = ((rotY + rotX + turn) ) * s
        blMotor.power = ((rotY - rotX + turn) ) * s
        frMotor.power = ((rotY - rotX - turn) ) * s
        brMotor.power = ((rotY + rotX - turn) )* s
    }
}