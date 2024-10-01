package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.baylorschool.intothedeep.lib.Mecanum

class EasyMecanum : LinearOpMode() {
    val mecanum : Mecanum = Mecanum(hardwareMap)
    override fun runOpMode() {
        mecanum.mecanumLoop(gamepad1 = gamepad1)
    }

}