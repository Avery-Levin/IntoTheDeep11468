package org.baylorschool.intothedeep.lib

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.lib.PIDActuator



@Config

class Arm(hardwareMap: HardwareMap) : PIDActuator(hardwareMap)
{

init {
    var config = PIDConfig()
    PIDConfig.p = 0.0
    PIDConfig.i = 0.0
    PIDConfig.d = 0.0
    PIDConfig.fg = 0.0
    PIDConfig.target = 0.0
    motorName = "armMotor"
    ticks_per_unit = 2786.2/360.0
}
}