package org.baylorschool.intothedeep.lib

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.baylorschool.intothedeep.Global
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.baylorschool.intothedeep.drivers.GoBildaPinpointDriver
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class FieldCentricMec(hardwareMap: HardwareMap) {
    private val flMotor: DcMotorEx
    private val frMotor: DcMotorEx
    private val blMotor: DcMotorEx
    private val brMotor: DcMotorEx
    private val odo: GoBildaPinpointDriver
    private var s: Double = 1.0
    private var y: Float = 0.0F
    private var x: Float = 0.0F
    private var turn: Float = 0.0F
    private var botHeading: Float = 0.0F
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

        flMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        blMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        frMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        brMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER

        odo.recalibrateIMU()
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Front Left Power", flMotor.power)
        telemetry.addData("Front Right Power", frMotor.power)
        telemetry.addData("Back Left Power", blMotor.power)
        telemetry.addData("Back Right Power", brMotor.power)
        telemetry.addData("Robot Heading", odo.heading)
        telemetry.addData("Pinpoint Frequency", odo.frequency);
    }

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

    fun mecanumLoop(gamepad1: Gamepad){
        odo.bulkUpdate()

        y = (-gamepad1.left_stick_y).pow(3)
        x = gamepad1.left_stick_x.pow(3)
        turn = -gamepad1.right_stick_x.pow(3)

        if (gamepad1.right_bumper)
            s = 0.4
        else if (gamepad1.left_bumper)
            s = 1.0
        else if (gamepad1.options)
            odo.recalibrateIMU()

        power()
    }

    fun power() {
        botHeading = odo.yawScalar
        rotX = (x * cos(-botHeading.toDouble()) - y * sin(-botHeading.toDouble())) * 1.1
        rotY = x * sin(-botHeading.toDouble()) + y * cos(-botHeading.toDouble())
        denominator = Math.max(abs(rotY) + abs(rotX) + abs(turn), 1.0)

        flMotor.power = ((rotY + rotX + turn) ) * s
        blMotor.power = ((rotY - rotX + turn) ) * s
        frMotor.power = ((rotY - rotX - turn) ) * s
        brMotor.power = ((rotY + rotX - turn) )* s
    }
}