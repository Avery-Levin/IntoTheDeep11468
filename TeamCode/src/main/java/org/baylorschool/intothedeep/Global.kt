package org.baylorschool.intothedeep

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.pedropathing.follower.Follower
import com.qualcomm.robotcore.hardware.Gamepad
import org.baylorschool.intothedeep.Global.DiffyConfig.standardL
import org.baylorschool.intothedeep.Global.DiffyConfig.standardR
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
        DiffyInit(diffyInit),
        DiffyAutoPickup45(diffyAutoPickup45)
    }

    // diffy
    @Config
    object DiffyConfig {
        @JvmField var standardL: Double = 0.5061
        @JvmField var standardR: Double = 0.3767
    }
    val diffyIdle = DiffyPos(standardL, standardR)
    val diffy45 = DiffyPos(standardL+0.0511, standardR+0.0955)
    val diffy90 = DiffyPos(standardL+0.0428, standardR+0.1139)
    val diffy135 = DiffyPos(standardL+0.0956, standardR+0.0611)
    val diffy180 = DiffyPos(standardL+.08, standardR+0.0772)
    val diffyRetract = DiffyPos (standardL+0.0322, standardR-0.02)
    val diffyBasket = DiffyPos(standardL+0.0211, standardR-0.0439)
    val diffySpecIntake = DiffyPos(standardL+0.0161, standardR+0.0144)
    val diffySpecDepo = DiffyPos(standardL-.0972, standardR+0.0189)
    val diffyInit = DiffyPos(standardL-.0961, standardR+0.0077)
    val diffyAutoPickup45 = DiffyPos(standardL+.0555, standardR+0.0872)

    const val clawOpen = 0.40
    const val clawClosed = 0.92

    //hardstop
    fun hardStops(value: Int, low: Int, high: Int): Int {
        return if (value < low) low + 1
        else if (value > high) high - 1
        else value
    }

    //slides
    enum class SlidePresets(var pos: Double) {
        RESET(0.0), INTAKE(1000.0), TELE_INTAKE(1500.0),
        LOW_BASKET(0.0), HIGH_BASKET(2300.0),//7
        SPEC_INTAKE(300.0), LOW_CHAMBER(0.0), HIGH_CHAMBER(700.0), HIGH_CHAMBER_SNAP(180.0),
        FWINTAKE(500.0),
        FWINTAKE_ALMOST(300.0),
        FWINTAKE_ALMOST_ALMOST(200.0),
        HIGH_CHAMBER_AUTO(580.0),
        FWINTAKE_AUTO(525.0), HIGH_CHAMBER_DROP_AUTO(180.0),
        LOW_RUNG(1000.0), HIGH_RUNG(0.0),;
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
        @JvmField var p: Double = 0.0115
        @JvmField var fg: Double = 0.04
        @JvmField var target: Double = 0.0
    }

    //pivot
    enum class PivotPresets(var pos: Double) {
        RESET(00.0), DEPO(1150.0),
        SPEC_DEPOSIT(950.0)/**/,
        SPEC_DEPOSIT_DROP(1000.0),
        WALL_PICKUP(400.0),
        SPEC_DEPOSIT_AUTO(975.0),
        WALL_PICKUP_AUTO(230.0)/**/, WALL_PICKUP_UP_AUTO(360.0),//up before pull next
        LOW_RUNG(700.0), LOW_RUNG_RETRACT(200.0), HIGH_RUNG(0.0);
        fun action(pivot: Pivot, auto: Boolean = true) : Action {
            val x = this
            return object : Action {
                override fun init() {
                    PivotPIDConfig.target = x.pos
                }

                override fun update(): Boolean = pivot.close()
            }
        }
        fun action(pivot: Pivot, follower: Follower, telemetry: MultipleTelemetry, auto: Boolean = true, multi: Double = 1.6) : Action {
            val x = this
            return object : Action {
                var oldValue = -1.0
                var distance = -1.0
                var testing = -1
                override fun init() {
                    oldValue = PivotPIDConfig.target
                    distance = oldValue - x.pos
                    testing = 0
                }

                override fun update(): Boolean {
                    pivot.update()
                    testing += 1
                    PivotPIDConfig.target = -min(follower.currentTValue * multi, 1.0) * distance + oldValue
                    telemetry.addData("pivot target", PivotPIDConfig.target)
                    telemetry.addData("pivot distance", distance)
                    telemetry.addData("pivot follower T", follower.currentTValue)
                    telemetry.addData("A-pivot testing", testing)
                    telemetry.addData("pinpoint cooked", follower.isLocalizationNAN)
                    telemetry.addData("pivot targetpos", x.pos)
                    telemetry.addData("pivot pos", pivot.pivotPos)
                    telemetry.addData("pivot targetpos", distance)
                    telemetry.addData("pivot oldvalue", oldValue)
                    telemetry.update()
                    return pivot.close(x.pos)
                }
            }
        }
    }
    @Config
    object PivotPIDConfig {
        var p: Double
            get() = if (useTeleopPID) {TeleopPivotPIDConfig.p} else {AutoPivotPIDConfig.p}
            set(_) {}
        var d: Double
            get() = if (useTeleopPID) {TeleopPivotPIDConfig.d} else {AutoPivotPIDConfig.d}
            set(_) {}
        var fg: Double
            get() = if (useTeleopPID) {TeleopPivotPIDConfig.fg} else {AutoPivotPIDConfig.fg}
            set(_) {}
        var tunedVoltage: Double
            get() = if (useTeleopPID) {TeleopPivotPIDConfig.tunedVoltage} else {AutoPivotPIDConfig.tunedVoltage}
            set(_) {}
        @JvmField var target: Double = 0.0
        @JvmField var useTeleopPID: Boolean = true
    }

    @Config
    object TeleopPivotPIDConfig {
        @JvmField var p: Double = 0.0185
        @JvmField var d: Double = 0.000026
        @JvmField var fg: Double = 0.003
        @JvmField var tunedVoltage: Double = 12.34
        //@JvmField var target: Double = 0.0
    }

    @Config
    object AutoPivotPIDConfig {
        @JvmField var p: Double = 0.0195
        @JvmField var d: Double = 0.000026
        @JvmField var fg: Double = 0.0025
        @JvmField var tunedVoltage: Double = 12.55
        //@JvmField var target: Double = 0.0
    }
}




// been cappin pimps since 1987 - Dr Elliott in reference to some college