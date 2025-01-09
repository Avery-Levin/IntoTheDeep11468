package org.baylorschool.intothedeep.lib

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.baylorschool.intothedeep.Global
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.GoBildaPinpointDriver
import kotlin.math.pow

class Mecanum(hardwareMap: HardwareMap) {
    private val flMotor: DcMotorEx
    private val frMotor: DcMotorEx
    private val blMotor: DcMotorEx
    private val brMotor: DcMotorEx
    private var s: Double = 1.0
    private var y: Float = 0.0F
    private var x: Float = 0.0F
    private var turn: Float = 0.0F

    init {
        flMotor = hardwareMap.get(DcMotorEx::class.java, Global.flMotorName)
        blMotor = hardwareMap.get(DcMotorEx::class.java, Global.blMotorName)
        frMotor = hardwareMap.get(DcMotorEx::class.java, Global.frMotorName)
        brMotor = hardwareMap.get(DcMotorEx::class.java, Global.brMotorName)

        blMotor.direction = DcMotorSimple.Direction.REVERSE
        flMotor.direction = DcMotorSimple.Direction.REVERSE

        brMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        frMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        blMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        flMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        flMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        blMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        frMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        brMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Front Left Power", flMotor.power)
        telemetry.addData("Front Right Power", frMotor.power)
        telemetry.addData("Back Left Power", blMotor.power)
        telemetry.addData("Back Right Power", brMotor.power)
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
        y = gamepad1.left_stick_y
        x = -gamepad1.left_stick_x
        turn = gamepad1.right_stick_x

        if (gamepad1.right_bumper)
            s = 0.4
        else if (gamepad1.left_bumper)
            s = 1.0
        power()
    }

    fun power() {
        flMotor.power = (y + x + turn) * s
        blMotor.power = (y - x + turn) * s
        frMotor.power = (y - x - turn) * s
        brMotor.power = (y + x - turn) * s
    }
}