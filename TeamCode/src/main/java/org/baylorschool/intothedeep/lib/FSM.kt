package org.baylorschool.intothedeep.lib

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import org.baylorschool.intothedeep.Global
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.lib.Slides

class FSM(hardwareMap: HardwareMap) {

    enum class RobotState {
        START, INTAKE, SPEC_INTAKE, INTAKE_RETRACT, SAMPLE_DEPOSIT, SAMPLE_RETRACT, SPEC_DEPOSIT, SPEC_RETRACT
    }

    private val slides = Slides(hardwareMap)
    private val pivot = Pivot(hardwareMap)
    private val depo = Depo(hardwareMap)
    private var state = RobotState.START
    private var transTimer = ElapsedTime()
    private var retractTimer = ElapsedTime()
    private var clawMoveTimer = ElapsedTime()
    private val retractDelay = 0.2
    private var transDelay = 0.0
    private var clawMoveDelay = 0.3
    private val intakeThreshold = 200.0
    private var slideThreshold = 1250.0
    private var pivotThreshold = 150.0
    private var transition = false
    private var difference = 0.0
    private var lowerCheck = 0.0

    init {
        transTimer.reset()
        retractTimer.reset()
        clawMoveTimer.reset()
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("State", state)
        telemetry.addData("Transition", transition)
        telemetry.addData("Transition Delay", transDelay)
        telemetry.addData("Trans Timer", transTimer.seconds())
        telemetry.addData("Retract Timer", retractTimer.seconds())
        telemetry.addData("claw motor timer type shit", clawMoveTimer.seconds())
        telemetry.addData("difference", difference)
    }

    fun loop(gamepad: Gamepad, rumble: Gamepad) {
        when(state) {
            RobotState.START -> {
                depo.openClaw()
                depo.idle()
                pivot.reset()
                slides.reset()
                if (gamepad.a) {
                    transition = true
                    transDelay = 0.4
                    transTimer.reset()
                    slides.intake()
                    state = RobotState.INTAKE
                }
                if (gamepad.y) {
                    depo.specIntake()
                    transition = true
                    transDelay = 0.4
                    transTimer.reset()
                    state = RobotState.SPEC_INTAKE
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
                    } else if (gamepad.left_stick_button) {
                        depo.idle()
                    } else if (gamepad.right_bumper) {
                        depo.claw.position = 0.55
                    } else if (gamepad.left_bumper) {
                        depo.claw.position = 1.0
                    }
                }

                if (gamepad.dpad_up) {
                    rumble.rumble(10.0,1.0,400)
                    depo.retract()
                    slideThreshold = 150.0
                    transDelay = 0.6
                    transTimer.reset()
                    slides.reset()
                    state = RobotState.INTAKE_RETRACT
                }

            } RobotState.INTAKE_RETRACT -> {
                slides.slidePos = (slides.slideR.currentPosition.toDouble() * -1) - slides.offset
                difference = (slides.slidePos + lowerCheck) - Global.SlidePresets.RESET.pos
                if (difference < slideThreshold) {
                    pivot.deposit()
                }

                if (transTimer.seconds() > transDelay) {
                    if (gamepad.dpad_up) {
                        slides.highBasket()
                        slideThreshold = 200.0
                        transDelay = 1.0
                        transTimer.reset()
                        transition = true
                        state = RobotState.SAMPLE_DEPOSIT
                    }
                }

            } RobotState.SAMPLE_DEPOSIT -> {
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
                    } else if (gamepad.left_bumper) {
                        depo.openClaw()
                        retractTimer.reset()
                        clawMoveTimer.reset()
                        state = RobotState.SAMPLE_RETRACT
                    }
                }

            } RobotState.SAMPLE_RETRACT -> {
                slides.slidePos = (slides.slideR.currentPosition.toDouble() * -1) - slides.offset
                pivot.pivotPos = (pivot.pivotL.currentPosition.toDouble()) - pivot.offset
                difference = (slides.slidePos + lowerCheck) - Global.SlidePresets.RESET.pos
                if (clawMoveTimer.seconds() < clawMoveDelay) {
                    retractTimer.reset()
                }
                if (clawMoveTimer.seconds() > clawMoveDelay) {
                    depo.diffy90()
                }
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
            } RobotState.SPEC_INTAKE -> {
                pivot.specIntake()
                if (gamepad.right_bumper) {
                    depo.claw.position = 0.55
                } else if (gamepad.left_bumper) {
                    depo.claw.position = 1.0
                }

                if (gamepad.dpad_up) {
                    rumble.rumble(10.0,1.0,400)
                    pivot.deposit()
                    depo.diffySpec()
                    transDelay = 0.4
                    transTimer.reset()
                    state = RobotState.SPEC_DEPOSIT
                }
            } RobotState.SPEC_DEPOSIT ->{
                pivot.pivotPos = pivot.pivotL.currentPosition.toDouble() - slides.offset
                difference = Global.PivotPresets.DEPO.pos - pivot.pivotPos
                if (difference < pivotThreshold) {
                    slides.highChamber()
                }

                if (gamepad.left_bumper) {
                    slides.specScore()
                    state = RobotState.SAMPLE_RETRACT
                }

            } RobotState.SPEC_RETRACT -> {

            }
        }

        if (gamepad.back && state != RobotState.START)
            state = RobotState.START
    }
}