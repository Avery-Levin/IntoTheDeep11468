package org.baylorschool.intothedeep.lib

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.robot.Robot
import com.qualcomm.robotcore.util.ElapsedTime
import org.baylorschool.intothedeep.Global
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.lib.Slides
import kotlin.math.abs

class FSM(hardwareMap: HardwareMap) {

    enum class RobotState {
        START, INTAKE, INTAKE_RETRACT, DEPOSIT, BUCKET_RETRACT, SPEC_RETRACT
    }

    private val slides = Slides(hardwareMap)
    private val pivot = Pivot(hardwareMap)
    private val depo = Depo(hardwareMap)
    private var state = RobotState.START

    private var intakeTimer = ElapsedTime()
    private var transTimer = ElapsedTime()
    private var pivotTimer = ElapsedTime()
    private var retractTimer = ElapsedTime()
    private val intakeDelay = 0.5
    private val retractDelay = 1.0
    private var transDelay = 0.0
    private val pivotThreshold = 100
    private val slideThreshold = 300
    private var transition = true

    private var lowerCheck = 0.0

    init {
        pivotTimer.reset()
        retractTimer.reset()
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("State", state)
        telemetry.addData("Intake Timer", intakeTimer.seconds())
        telemetry.addData("Pivot Timer", pivotTimer.seconds())
        telemetry.addData("Trans Timer", transTimer.seconds())
        telemetry.addData("Retract Timer", retractTimer.seconds())
    }

    fun loop(gamepad: Gamepad) {
        when(state) {
            RobotState.START -> {
                depo.openClaw()
                depo.idle()
                pivot.reset()
                if (gamepad.a) {
                    transDelay = 0.5
                    intakeTimer.reset()
                    slides.intake()
                    transition = true
                    state = RobotState.INTAKE
                }

            } RobotState.INTAKE -> {
                if (intakeTimer.seconds() > intakeDelay && transition) {
                    depo.diffy180()
                }

                if (transTimer.seconds() > transDelay) {
                    transition = false
                    if (gamepad.a) {
                        depo.diffy180()
                    } else if (gamepad.b) {
                        depo.diffy45()
                    } else if (gamepad.x) {
                        depo.diffy135()
                    } else if (gamepad.y) {
                        depo.diffy90()
                    } else if (gamepad.right_bumper) {
                        depo.closeClaw()
                    } else if (gamepad.left_bumper) {
                        depo.openClaw()
                    }
                }

                if (gamepad.dpad_up) {
                    transDelay = 1.0
                    transTimer.reset()
                    depo.idle()
                    slides.reset()
                    state = RobotState.INTAKE_RETRACT
                }

            } RobotState.INTAKE_RETRACT -> {
                if (transTimer.seconds() > transDelay) {
                    if (gamepad.dpad_up) {
                        transDelay = 1.5
                        transTimer.reset()
                        pivot.deposit()
                        transition = true
                        state = RobotState.DEPOSIT
                    }
                }

            } RobotState.DEPOSIT -> {
                if ((abs(Global.PivotPresets.DEPO.pos - pivot.pivotPos) > pivotThreshold) && transition) {
                    slides.highBasket()
                }
                if ((abs(Global.SlidePresets.HIGH_BASKET.pos - slides.slidePos) > slideThreshold) && transition) {
                    depo.diffyBasket()
                }
                if (transTimer.seconds() > transDelay) {
                    transition = false

                    if (gamepad.dpad_up) {
                        slides.highBasket()
                        lowerCheck = 0.0
                    } else if (gamepad.dpad_down) {
                        slides.lowBasket()
                        lowerCheck = 0.0
                    } else if (gamepad.dpad_left) {
                        slides.lowChamber()
                    } else if (gamepad.dpad_right) {
                        slides.highChamber()
                    }

                    if (gamepad.x) {
                        depo.diffySpec()
                        state = RobotState.SPEC_RETRACT
                    } else if (gamepad.a) {
                        depo.openClaw()
                        retractTimer.reset()
                        state = RobotState.BUCKET_RETRACT
                    }
                }

            } RobotState.BUCKET_RETRACT -> {
                if (retractTimer.seconds() > retractDelay) {
                    depo.idle()
                    slides.reset()
                }
                if (abs(Global.SlidePresets.HIGH_BASKET.pos - (slides.slidePos + lowerCheck)) > slideThreshold) {
                    pivot.reset()
                }
                if (pivot.pivotPos < 50) {
                    state = RobotState.START
                }

            } RobotState.SPEC_RETRACT -> {

            }
        }

        if (gamepad.back && state != RobotState.START)
            state = RobotState.START
    }
}