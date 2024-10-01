package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.intothedeep.lib.Mecanum

@TeleOp
class EasyMecanum : LinearOpMode() {
    val mecanum : Mecanum = Mecanum(hardwareMap)
    override fun runOpMode() {
        while(opModeIsActive()) {
            mecanum.mecanumLoop(gamepad1 = gamepad1)
        }
    }

}