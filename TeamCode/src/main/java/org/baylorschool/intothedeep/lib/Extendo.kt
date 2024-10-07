package org.baylorschool.intothedeep.lib

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import org.baylorschool.intothedeep.controllers.PIDCoefficients
import org.baylorschool.intothedeep.controllers.PIDFController
import org.baylorschool.intothedeep.lib.extendoPIDConfig.target
import org.firstinspires.ftc.teamcode.lib.armPIDConfig
import org.firstinspires.ftc.teamcode.lib.armPIDConfig.d
import org.firstinspires.ftc.teamcode.lib.armPIDConfig.i
import org.firstinspires.ftc.teamcode.lib.armPIDConfig.p


@Config
object extendoPIDConfig {
    @JvmField var p: Double = 0.0
    @JvmField var i: Double = 0.0
    @JvmField var d: Double = 0.0
    @JvmField var fg: Double = 0.25
    @JvmField var target: Double = 0.0
}

class Extendo(hardwareMap: HardwareMap) {
    val ticks_per_inch : Double = TODO()
    var correctedValue = target /ticks_per_inch
    val extendoMotor : DcMotorEx
    var extendoPos: Double = 0.0
    private val pControl = PIDCoefficients(p, i, d)
    private val controller = PIDFController(pControl)
    var extendoPower = 0.0
    private var offset = 0

    init {
        extendoMotor = hardwareMap.get(DcMotorEx::class.java, "armMotor")
        offset = extendoMotor.currentPosition
        extendoPos = extendoMotor.currentPosition.toDouble() - offset
        armPIDConfig.target = 0.0
    }
}