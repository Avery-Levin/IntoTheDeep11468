package org.baylorschool.intothedeep.vision

import org.opencv.core.Point
import kotlin.math.atan
import kotlin.math.sin
import kotlin.math.tan

private fun fovCal(fovd: Int, pixelLengthX: Int, pixelLengthY: Int, x: Boolean): Double {
    val theta0 = atan((pixelLengthY/pixelLengthX).toDouble())
    val theta1 = 90 - theta0
    val fovx = (fovd * sin(theta1))/sin(90.0)
    val fovy = (fovd * sin(theta0))/sin(90.0)
    return if (x) {
        //fovx
        57.76873092100172
    } else {
        //fovy
        44.79994302976408
    }
}
//TODO find these
//also these aren't in globals b/c they won't be changed
//val camWidthPixels = 400
//val camHeightPixels = 400
val fovd = 55
//idk how to get cam angle rn, we haven't decided where it goes yet
fun botDataToPos(armLength: Double, armAngle: Double, cameraAngle: Double, objX: Double, objY: Double, camWidthPixels: Int, camHeightPixels: Int, xyz: (Double, Double) -> Unit) {
    val height = sin(Math.toRadians(armAngle)) * armLength

    cameraDataToRealPosition(height, cameraAngle, camWidthPixels, camHeightPixels, objX, objY) {x, y, _, _, _, _ ->
        xyz(x, y)
    }
}
/**
 * @param cameraHeight the height of the camera off the ground
 * @param cameraAngle At what angle the camera is facing. The angle between a line coming out of the camera and a line going straight down
 * @param camWidthPixels the width in pixels of the image
 * @param camHeightPixels the height in pixels of the image
 * @param targetObjX in pixels
 * @param targetObjY in pixels
 */
fun cameraDataToRealPosition(cameraHeight: Double, cameraAngle: Double, camWidthPixels: Int, camHeightPixels: Int, targetObjX: Double, targetObjY: Double, xyz: (Double, Double, Double, Double, Int, Int) -> Unit) {
    val camWidthAngle = fovCal(fovd, camWidthPixels, camHeightPixels, true).toInt()
    val camHeightAngle = fovCal(fovd, camWidthPixels, camHeightPixels, false).toInt()

    val realAngleX = pixelsToAngle(camWidthPixels, camWidthAngle, targetObjX)
    val realAngleY = pixelsToAngle(camHeightPixels, camHeightAngle, targetObjY)

    //aah trig!!!
    val posX = tan(Math.toRadians(realAngleX)) * cameraHeight
    val posY = -tan(Math.toRadians(realAngleY + cameraAngle)) * cameraHeight
    xyz(posX, posY, realAngleX, realAngleY, camWidthAngle, camHeightAngle)
}

private fun pixelsToAngle(camSizePixels: Int, camSizeAngle: Int, pos: Double): Double {
    val mid = camSizePixels/2
    val adjPos = pos-mid
    return adjPos / camSizePixels * camSizeAngle//should give in range -camSizeAngle/2 to +camSizeAngle/2
}