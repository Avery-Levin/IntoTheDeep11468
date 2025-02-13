package org.baylorschool.intothedeep

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.pedropathing.follower.Follower
import com.qualcomm.robotcore.hardware.Gamepad
import org.baylorschool.intothedeep.Global.PivotPIDConfig.target
import org.baylorschool.intothedeep.lib.DiffyPos
import org.baylorschool.intothedeep.lib.Pivot
import org.baylorschool.intothedeep.vision.dist
import org.firstinspires.ftc.teamcode.lib.Slides
import kotlin.math.min

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
    const val mass = 12.57

    enum class DiffyPosition(val diffyPos: DiffyPos) {
        DiffyIdle(diffyIdle),
        Diffy45(diffy45),
        Diffy90(diffy90),
        Diffy135(diffy135),
        DiffyRetract(diffyRetract),
        DiffyBasket(diffyBasket),
        DiffySpecIntake(diffySpecIntake),
        DiffySpecDepo(diffySpecDepo),
        DiffyInit(diffyInit)
    }

    // diffy
    val standardL = 0.4633
    val standardR = 0.45
    val diffyIdle = DiffyPos(standardL, standardR)
    val diffy45 = DiffyPos(standardL+0.0511, standardR+0.0955)
    val diffy90 = DiffyPos(standardL+0.0428, standardR+0.1139)
    val diffy135 = DiffyPos(standardL+0.0956, standardR+0.0611)
    val diffy180 = DiffyPos(standardL+.08, standardR+0.0772)
    val diffyRetract = DiffyPos (standardL+0.0322, standardR-0.02)
    val diffyBasket = DiffyPos(standardL+0.0211, standardR-0.0439)
    val diffySpecIntake = DiffyPos(standardL+0.0190, standardR+0.0190)
    val diffySpecDepo = DiffyPos(standardL-.1006, standardR+0.0161)
    val diffyInit = DiffyPos(standardL-.0961, standardR+0.0077)

    const val clawOpen = 0.55
    const val clawClosed = 0.95

    //hardstop
    fun hardStops(value: Int, low: Int, high: Int): Int {
        return if (value < low) low + 1
        else if (value > high) high - 1
        else value
    }

    //slides
    enum class SlidePresets(var pos: Double) {
        RESET(0.0), INTAKE(1000.0),
        LOW_BASKET(0.0), HIGH_BASKET(2300.0),//7
        SPEC_INTAKE(300.0), LOW_CHAMBER(0.0), HIGH_CHAMBER(680.0), HIGH_CHAMBER_SNAP(150.0),
        FWINTAKE(500.0),
        FWINTAKE_ALMOST(400.0),
        HIGH_CHAMBER_AUTO(580.0),
        FWINTAKE_AUTO(525.0), HIGH_CHAMBER_DROP_AUTO(150.0),
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
        @JvmField var p: Double = 0.025
        @JvmField var fg: Double = 0.1
        @JvmField var target: Double = 0.0
    }

    //pivot
    enum class PivotPresets(var pos: Double) {
        RESET(0.0), DEPO(1100.0),
        SPEC_DEPOSIT(950.0)/**/,
        SPEC_DEPOSIT_DROP(1000.0),
        WALL_PICKUP(230.0),
        SPEC_DEPOSIT_AUTO(975.0),
        WALL_PICKUP_AUTO(230.0)/**/, WALL_PICKUP_UP_AUTO(330.0),//up before pull next
        LOW_RUNG(0.0), HIGH_RUNG(0.0);
        fun action(pivot: Pivot) : Action {
            val x = this
            return object : Action {
                override fun init() {
                    target = x.pos
                }

                override fun update(): Boolean = pivot.close()
            }
        }
        fun action(pivot: Pivot, follower: Follower, telemetry: MultipleTelemetry) : Action {
            val x = this
            return object : Action {
                var oldValue = -1.0
                var distance = -1.0
                var testing = -1
                override fun init() {
                    oldValue = target
                    distance = oldValue - x.pos
                    testing = 0
                }

                override fun update(): Boolean {
                    pivot.update()
                    target = -min(follower.currentTValue*1.6, 1.0) * distance + oldValue
                    testing += 1
//                    telemetry.addData("pivot target", target)
//                    telemetry.addData("pivot distance", distance)
//                    telemetry.addData("pivot follower T", follower.currentTValue)
//                    telemetry.addData("A-pivot testing", testing)
//                    telemetry.addData("pinpoint cooked", follower.isPinpointCooked)
//                    telemetry.addData("pivot targetpos", x.pos)
//                    telemetry.addData("pivot pos", pivot.pivotPos)
//                    telemetry.addData("pivot targetpos", distance)
//                    telemetry.addData("pivot oldvalue", oldValue)
                    telemetry.update()
                    return pivot.close(x.pos)
                }
            }
        }
    }

    @Config
    object PivotPIDConfig {
        @JvmField var p: Double = 0.025
        @JvmField var i: Double = 0.000
        @JvmField var d: Double = 0.00005
        @JvmField var fg: Double = 0.00
        @JvmField var target: Double = 0.0
    }
}




// been cappin pimps since 1987 - Dr Elliot in reference to some college