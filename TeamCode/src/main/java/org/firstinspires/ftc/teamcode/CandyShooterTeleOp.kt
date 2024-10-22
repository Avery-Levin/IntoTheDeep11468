package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.intothedeep.lib.CandyShooter
import org.baylorschool.intothedeep.lib.Mecanum

@TeleOp(name = "Candy Shooter",group = "Beta Bot")

class CandyShooterTeleOp : LinearOpMode() {
    val mecanum  = Mecanum(hardwareMap)
    val candyShooter : CandyShooter = CandyShooter(hardwareMap)
    override fun runOpMode() {
        while(opModeIsActive()) {
            mecanum.mecanumLoop(gamepad1)
            candyShooter.shooterLoop(gamepad1)

            candyShooter.telemetry(telemetry)
        }
    }
}