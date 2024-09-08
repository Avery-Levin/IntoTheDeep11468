package org.firstinspires.ftc.teamcode.vision

import android.graphics.Canvas
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration
import org.firstinspires.ftc.vision.VisionProcessor
import org.opencv.core.Mat

class OverheadProcessor(val color: Color): VisionProcessor {
    override fun init(width: Int, height: Int, calibration: CameraCalibration?) {
        //Nothing to do here :) (I think)
    }
    var lastDetection = listOf<Sample>()
    override fun processFrame(frame: Mat?, captureTimeNanos: Long): List<Sample> {
        if (frame != null) {
            lastDetection = process(frame, color, false)
            return lastDetection
        }
        return listOf()
    }

    override fun onDrawFrame(canvas: Canvas?, onscreenWidth: Int, onscreenHeight: Int, scaleBmpPxToCanvasPx: Float, scaleCanvasDensity: Float, userContext: Any?) {
        lastDetection.forEach {
            it.drawDirections(canvas!!)
        }
    }
}