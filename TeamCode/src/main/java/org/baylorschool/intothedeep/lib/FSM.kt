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
    private val retractDelay = 0.5
    private var transDelay = 0.0
    private val intakeThreshold = 200.0
    private val pivotThreshold = 120.0
    private var slideThreshold = 300.0
    private var transition = false
    private var difference = 0.0
    private var pivotDifference = 0.0

    private var lowerCheck = 0.0

    init {
        pivotTimer.reset()
        retractTimer.reset()
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("State", state)
        telemetry.addData("Transition", transition)
        telemetry.addData("Transition Delay", transDelay)

        telemetry.addData("Intake Timer", intakeTimer.seconds())
        telemetry.addData("Pivot Timer", pivotTimer.seconds())
        telemetry.addData("Trans Timer", transTimer.seconds())
        telemetry.addData("Retract Timer", retractTimer.seconds())

        telemetry.addData("difference", difference)


    }

    fun loop(gamepad: Gamepad) {
        when(state) {
            RobotState.START -> {
                depo.openClaw()
                depo.idle()
                pivot.reset()
                if (gamepad.a) {
                    transition = true
                    transDelay = 0.2
                    transTimer.reset()
                    intakeTimer.reset()
                    slides.intake()
                    state = RobotState.INTAKE
                }

            } RobotState.INTAKE -> {
                slides.slidePos = (slides.slideR.currentPosition.toDouble() * -1) - slides.offset
                difference = Global.SlidePresets.INTAKE.pos - slides.slidePos
                if (difference < intakeThreshold && transition) {
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
                    slideThreshold = 100.0
                    transDelay = 0.3
                    transTimer.reset()
                    depo.diffy180()
                    slides.reset()
                    state = RobotState.INTAKE_RETRACT
                }

            } RobotState.INTAKE_RETRACT -> {
                difference = (slides.slidePos + lowerCheck) - Global.SlidePresets.RESET.pos
                if (difference < slideThreshold) {
                    pivot.deposit()
                }

                if (transTimer.seconds() > transDelay) {
                    if (gamepad.dpad_up) {
                        slides.highBasket()
                        slideThreshold = 300.0
                        transDelay = 1.0
                        transTimer.reset()
                        transition = true
                        state = RobotState.DEPOSIT
                    }
                }

            } RobotState.DEPOSIT -> {
                slides.slidePos = (slides.slideR.currentPosition.toDouble() * -1) - slides.offset
                difference = Global.SlidePresets.HIGH_BASKET.pos - slides.slidePos
                if ((difference < slideThreshold)) {
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
                    } else if (gamepad.left_bumper) {
                        depo.openClaw()
                        retractTimer.reset()
                        state = RobotState.BUCKET_RETRACT
                    }
                }

            } RobotState.BUCKET_RETRACT -> {
                slides.slidePos = (slides.slideR.currentPosition.toDouble() * -1) - slides.offset
                pivot.pivotPos = (pivot.pivotL.currentPosition.toDouble()) - pivot.offset
                difference = (slides.slidePos + lowerCheck) - Global.SlidePresets.RESET.pos
                if (retractTimer.seconds() > retractDelay) {
                    depo.idle()
                    slides.reset()
                }
                if (difference < slideThreshold) {
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