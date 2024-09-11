package org.baylorschool.intothedeep.vision

import android.graphics.Canvas
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration
import org.firstinspires.ftc.vision.VisionProcessor
import org.opencv.core.Mat

class OverheadProcessor(val color: Color, val telemetry: Telemetry): VisionProcessor {
    override fun init(width: Int, height: Int, calibration: CameraCalibration?) {
        //Nothing to do here :) (I think)
    }
    var lastDetection = listOf<Sample>()
    override fun processFrame(frame: Mat?, captureTimeNanos: Long): List<Sample> {
        if (frame != null) {
            //p = private
            val plastDetection = process(frame, color, false, telemetry).toMutableList()
            if (color != Color.YELLOW) {
                process(frame, Color.YELLOW, false, telemetry)
                        .forEach {
                            plastDetection.add(it)
                        }
            }
            lastDetection = plastDetection.toList()
            //telemetry.update()
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