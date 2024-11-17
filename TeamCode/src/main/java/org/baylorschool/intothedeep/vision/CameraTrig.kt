package org.baylorschool.intothedeep.vision

import org.opencv.core.Point
import kotlin.math.sin
import kotlin.math.tan

//TODO find these
//also these aren't in globals b/c they won't be changed
val camWidthPixels = 400
val camHeightPixels = 400
val camWidthAngle = 50
val camHeightAngle = 50
//idk how to get cam angle rn, we haven't decided where it goes yet
fun botDataToPos(armLength: Double, armAngle: Double, cameraAngle: Double, objX: Double, objY: Double) {
    val height = sin(Math.toRadians(armAngle)) * armLength

    return cameraDataToRealPosition(height, cameraAngle, camWidthPixels, camHeightPixels, camWidthAngle, camHeightAngle, objX, objY)
}
/**
 * @param cameraHeight the height of the camera off the ground
 * @param cameraAngle At what angle the camera is facing. The angle between a line coming out of the camera and a line going straight down
 * @param camWidthPixels the width in pixels of the image
 * @param camHeightPixels the height in pixels of the image
 * @param camWidthAngle the width of the camera's image in degrees
 * @param camHeightAngle the height of the camera's image in degrees
 * @param targetObjX in pixels
 * @param targetObjY in pixels
 */
fun cameraDataToRealPosition(cameraHeight: Double, cameraAngle: Double, camWidthPixels: Int, camHeightPixels: Int, camWidthAngle: Int, camHeightAngle: Int, targetObjX: Double, targetObjY: Double) {
    val realAngleX = pixelsToAngle(camWidthPixels, camWidthAngle, targetObjX)
    val realAngleY = pixelsToAngle(camHeightPixels, camHeightAngle, targetObjY)

    //aah trig!!!
    val posX = tan(Math.toRadians(realAngleX)) * cameraHeight
    val posY = tan(Math.toRadians(realAngleY + cameraAngle)) * cameraHeight
}

private fun pixelsToAngle(camSizePixels: Int, camSizeAngle: Int, pos: Double): Double {
    val mid = camSizePixels/2
    val adjPos = pos-mid
    return adjPos / camSizePixels * camSizeAngle//should give in range -camSizeAngle/2 to +camSizeAngle/2
}
