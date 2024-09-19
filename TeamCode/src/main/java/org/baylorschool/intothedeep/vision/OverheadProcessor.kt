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
    var lastFrame = Mat()
    var width = 640
    var height = 480
    override fun processFrame(frame: Mat?, captureTimeNanos: Long): List<Sample> {
        if (frame != null) {
            width = frame.width()
            height = frame.height()
            //p = private
            val plastDetection = process(frame, color, true, telemetry).toMutableList()
            if (color != Color.YELLOW) {
                process(frame, Color.YELLOW, false, telemetry)
                        .forEach {
                            plastDetection.add(it)
                        }
            }
            lastDetection = plastDetection.toList()
            //telemetry.update()
            lastFrame = frame
            return lastDetection
        }
        return listOf()
    }

    override fun onDrawFrame(canvas: Canvas?, onscreenWidth: Int, onscreenHeight: Int, scaleBmpPxToCanvasPx: Float, scaleCanvasDensity: Float, userContext: Any?) {
        /*for (sample in lastDetection) {
            sample.drawDirections(canvas!!)
        }*/
    }
}