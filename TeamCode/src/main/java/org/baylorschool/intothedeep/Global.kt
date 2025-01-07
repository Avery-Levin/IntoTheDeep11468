package org.baylorschool.intothedeep

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.Servo
import org.baylorschool.intothedeep.lib.DiffyPos

@Config
object Global {
    // motors
    const val flMotorName = "flMotor"//These here so we can depend on them in rr code
    const val blMotorName = "blMotor"
    const val frMotorName = "frMotor"
    const val brMotorName = "brMotor"

    // odo
    const val odo = "odo"
    const val xOffset = 16.33858/2
    const val yOffset = -8.464567//in
    const val mass = 5.44217

    // diffy

    val diffyIdle = DiffyPos(0.485, 0.515)
    val diffy45 = DiffyPos(0.28, 0.15)
    val diffy90 = DiffyPos(0.35, 0.09)
    val diffy135 = DiffyPos(0.13, 0.3)
    val diffy180 = DiffyPos(0.20, 0.23)
    val diffyBasket = DiffyPos(0.58, 0.61)

    //claw
    const val claw = "claw"
    const val clawOpen = 0.5
    const val clawClosed = 1.0

    //hardstop
    fun hardStops(value: Int, low: Int, high: Int): Int {
        return if (value < low) low + 1
        else if (value > high) high - 1
        else value
    }

    //slides
    enum class SlidePresets(var pos: Double) {
        RESET(0.0), INTAKE(1000.0),
        LOW_BASKET(0.0), HIGH_BASKET(2300.0),
        LOW_CHAMBER(0.0), HIGH_CHAMBER(0.0),
        LOW_RUNG(0.0), HIGH_RUNG(0.0),
    }
    @Config
    object SlidePIDConfig {
        @JvmField var p: Double = 0.01
        @JvmField var fg: Double = 0.1
        @JvmField var target: Double = 0.0
    }

    //pivot
    enum class PivotPresets(var pos: Double) {
        RESET(10.0), DEPO(950.0),
        LOW_RUNG(0.0), HIGH_RUNG(0.0),
    }

    @Config
    object PivotPIDConfig {
        @JvmField var p: Double = 0.003 //0.006
        @JvmField var i: Double = 0.000
        @JvmField var d: Double = 0.00001
        @JvmField var fg: Double = 0.15
        @JvmField var target: Double = 0.0
    }

}