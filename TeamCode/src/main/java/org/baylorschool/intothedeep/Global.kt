package org.baylorschool.intothedeep

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.Gamepad
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
    const val xOffset = 4.035
    const val yOffset = 1.673
    const val mass = 5.44217

    // diffy

    val diffyIdle = DiffyPos(0.5061, 0.5233)
    val diffy45 = DiffyPos(0.5606, 0.605)
    val diffy90 = DiffyPos(0.6167, 0.5689)
    val diffy135 = DiffyPos(0.6028, 0.5833)
    val diffy180 = DiffyPos(0.5844, 0.6017)
    val diffyRetract = DiffyPos (0.5383, 0.4906)
    val diffyBasket = DiffyPos(0.51, 0.4623)
    val diffySpecIntake = DiffyPos(0.0,0.0)
    val diffySpecDepo = DiffyPos(0.0, 0.0)


    const val clawOpen = 0.55
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
        LOW_CHAMBER(0.0), HIGH_CHAMBER(900.0), HIGH_CHAMBER_SNAP(600.0),
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
        RESET(20.0), DEPO(1240.0),
        SPEC_INTAKE(220.0), LOW_RUNG(0.0), HIGH_RUNG(0.0),
    }

    @Config
    object PivotPIDConfig {
        @JvmField var p: Double = 0.006
        @JvmField var i: Double = 0.000
        @JvmField var d: Double = 0.00001
        @JvmField var fg: Double = 0.15
        @JvmField var target: Double = 0.0
    }
}




// been cappin pimps since 1987 - Dr Elliot in reference to some college