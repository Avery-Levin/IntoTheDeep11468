package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.intothedeep.lib.CandyShooter
import org.baylorschool.intothedeep.lib.Mecanum

@TeleOp(name = "Candy Shooter",group = "Beta Bot")
class CandyShooterTeleOp : LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val candyShooter = CandyShooter(hardwareMap)
        val mecanum = Mecanum(hardwareMap)

        waitForStart()
        while(opModeIsActive()) {
            candyShooter.shooterLoop(gamepad1)
            mecanum.mecanumLoop(gamepad1)
            candyShooter.telemetry(telemetry)
        }
    }
}