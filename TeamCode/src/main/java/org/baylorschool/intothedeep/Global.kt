package org.baylorschool.intothedeep

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.Gamepad
import org.baylorschool.intothedeep.Global.PivotPIDConfig.target
import org.baylorschool.intothedeep.lib.DiffyPos
import org.baylorschool.intothedeep.lib.Pivot
import org.firstinspires.ftc.teamcode.lib.Slides

@Config
object Global {
    // motors
    const val flMotorName = "flMotor"//These here so we can depend on them in rr code
    const val blMotorName = "blMotor"
    const val frMotorName = "frMotor"
    const val brMotorName = "brMotor"

    // odo
    const val odo = "odo"
    const val xOffset = -3.03//-4.0309
    const val yOffset = -4.21//-1.673
    const val mass = 12.24

    enum class DiffyPosition(val diffyPos: DiffyPos) {
        DiffyIdle(diffyIdle),
        Diffy45(diffy45),
        Diffy90(diffy90),
        Diffy135(diffy135),
        DiffyRetract(diffyRetract),
        DiffyBasket(diffyBasket),
        DiffySpecIntake(diffySpecIntake),
        DiffySpecDepo(diffySpecDepo)
    }
    // diffy
    val diffyIdle = DiffyPos(0.515, 0.5139)
    val diffy45 = DiffyPos(0.5661, 0.6094)
    val diffy90 = DiffyPos(0.5578, 0.6278)
    val diffy135 = DiffyPos(0.6106, 0.575)
    val diffy180 = DiffyPos(0.595, 0.5911)
    val diffyRetract = DiffyPos (0.5472, 0.4939)
    val diffyBasket = DiffyPos(0.5361, 0.47)
    val diffySpecIntake = DiffyPos(0.4222,0.4017)
    val diffySpecDepo = DiffyPos(0.5222, 0.6383)

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
        SPEC_INTAKE(120.0), LOW_CHAMBER(0.0), HIGH_CHAMBER(950.0), HIGH_CHAMBER_SNAP(450.0),
        FWINTAKE(500.0),
        LOW_RUNG(0.0), HIGH_RUNG(0.0),;
        fun action(slides: Slides) : Action {
            val x = this
            return object : Action {
                override fun init() {
                    SlidePIDConfig.target = x.pos
                }

                override fun update(): Boolean = slides.close()
            }
        }
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
        SPEC_DEPOSIT(1000.0), LOW_RUNG(0.0), HIGH_RUNG(0.0),
        WALL_PICKUP(100.0);
        fun action(pivot: Pivot) : Action {
            val x = this
            return object : Action {
                override fun init() {
                    target = x.pos
                }

                override fun update(): Boolean = pivot.close()
            }
        }
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