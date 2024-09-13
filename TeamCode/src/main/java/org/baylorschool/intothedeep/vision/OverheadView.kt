package org.baylorschool.intothedeep.vision

import android.graphics.Canvas
import android.graphics.Paint
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

enum class Color {
	RED, BLUE, YELLOW
}

/**
 * Warning: copilot generated method. Allegedly uses something called the "shoelace formula" if something breaks it might be this
 */
fun calculateArea(p1: Point, p2: Point, p3: Point, p4: Point): Double {
	return 0.5 * abs(
			p1.x * p2.y + p2.x * p3.y + p3.x * p4.y + p4.x * p1.y -
					(p2.x * p1.y + p3.x * p2.y + p4.x * p3.y + p1.x * p4.y)
	)
}
class Sample(val topLeft: Point, val topRight: Point, val bottomLeft: Point, val bottomRight: Point) {
	val middleTop = Point((topLeft.x + topRight.x) / 2, (topLeft.y + topRight.y) / 2)
	val middleBottom = Point((bottomLeft.x + bottomRight.x) / 2, (bottomLeft.y + bottomRight.y) / 2)
	val middle = Point((middleTop.x + middleBottom.x) / 2, (middleTop.y + middleBottom.y) / 2)
	/**
	 * This method returns the angle of the line between the middle of the sample and the midpoint of the top line, and the line that goes across the middle of the sample horizontally.
	 */
	fun getAngle(): Double {
		val angle = findAngle(middle.x, middleBottom.x, middle.y, middleBottom.y)
		return angle
	}
	/**
	 * This will return a VERY small number under normal circumstances.
	 */
	fun getSize(mat: Mat): Double {
		//get area of rectangle
		val area = calculateArea(topLeft, topRight, bottomRight, bottomLeft)
		return area / (mat.width() * mat.height())
	}

	/**
	 * This function for demonstration purposes only. It will draw the direction of the sample on the image.
	 */
	fun drawDirections(mat: Mat) {
		//get the angle of the side lines
		val angle = findAngle(middle.x, middleBottom.x, middle.y, middleBottom.y)
		Imgproc.circle(mat, middle, 25, Scalar(0.0, 255.0, 0.0), 10)
		Imgproc.putText(mat, angle.toString(), Point(middle.x + 40, middle.y + 40), 0, 1.0, Scalar(0.0, 255.0, 0.0), 3)
		Imgproc.line(mat, middle, middleBottom, Scalar(0.0, 255.0, 0.0), 10)
		println("mid:${middle.x}, ${middle.y} midTop: ${middleBottom.x}, ${middleBottom.y}, angle: $angle")
	}
	fun drawDirections(mat: Canvas) {
		val angle = findAngle(middle.x, middleBottom.x, middle.y, middleBottom.y)
		val paint = Paint()
		paint.color = android.graphics.Color.rgb(0, 255, 0)
		paint.strokeWidth = 3F
		mat.drawCircle(middle.x.toFloat(), middle.y.toFloat(), 25.0F, paint)
		//Idk what start and end are
		mat.drawText(angle.toString(), 0, 4, middle.x.toFloat() + 25.0F, middle.y.toFloat() + 25.0F, paint)
		mat.drawLine(middle.x.toFloat(), middle.y.toFloat(), middleBottom.x.toFloat(), middleBottom.y.toFloat(), paint)
	}

	/**
	 * Dark magic. Do not touch if at all possible.
	 * This function will return the angle of the line between two points, obviously.
	 * Written with help from https://maththebeautiful.com/angle-between-points/
	 */
	private fun findAngle(x1: Double, x2: Double, y1: Double, y2: Double): Double {
		//rise/run
		var x = atan2((y2 - y1), (x2 - x1))
		if (x < 0) {
			x += Math.PI * 2
		}
		return (180 - x * (180 / Math.PI).toInt().toDouble()) + 180
	}

}
const val log = true
fun process(frame0: Mat, color: Color, draw: Boolean = true, telemetry: Telemetry): List<Sample> {
	val frame = Mat()
	Imgproc.blur(frame0, frame, org.opencv.core.Size(2.0, 2.0))
	if (log) telemetry.addData("Processing color:", color)
	//24 4 0
	if (log) telemetry.addData("Imagetype", "type:${frame.type()},channels:${frame.channels()},depth:${frame.depth()}")
	if (log) telemetry.addData("Central color", frame[frame.width()/2, frame.height()/2].contentToString())
	//Convert to binary image 1 is target color, 0 is not target color.
	val out = Mat()
	when (color) {
		//RGBA is on robot!
		Color.RED -> Core.inRange(
				//BGR
				frame,
				Scalar(160.0, 0.0, 0.0, 0.0),
				Scalar(255.0, 120.0, 120.0, 256.0),
				out
		)
		Color.BLUE -> Core.inRange(
				//BGR
				frame,
				Scalar(0.0, 0.0, 160.0, 0.0),
				Scalar(120.0, 140.0, 255.0, 256.0),
				out
		)
		Color.YELLOW -> Core.inRange(
				//BGR
				//214, 163, 45
				//198, 137, 42
				frame,
				Scalar(170.0, 110.0, 0.0, 0.0),
				Scalar(256.0, 225.0, 90.0, 256.0),
				out
		)//.also { Imgcodecs.imwrite("imagesOut/y${i.nameWithoutExtension}.png", out) }
	}

	//find the contours, that is, contiguous areas of 1s (the target color)
	val hierarchyOutput = Mat()
	val contours: List<MatOfPoint> = LinkedList()
	Imgproc.findContours(
			out,
			contours,
			hierarchyOutput,
			Imgproc.RETR_TREE,
			Imgproc.CHAIN_APPROX_SIMPLE
	)
	if (log) telemetry.addData("# Contours", contours.size)
	//filter out small contours. These are false positives.
	val samples = contours.filter { Imgproc.contourArea(it) > 500 }
			.also { if (log) telemetry.addData("# Samples", it.size) }
			.map {
				//find the four corners of the contour. These are the corners of the sample.
				//Warning: This is a very expensive operation, as it is preformed in kotlin instead of C++ like "findContours" and other such methods
				var leftMost = Point(Double.POSITIVE_INFINITY, 0.0)
				var rightMost = Point(Double.NEGATIVE_INFINITY, 0.0)
				var topMost = Point(0.0, Double.NEGATIVE_INFINITY)
				var bottomMost = Point(0.0, Double.POSITIVE_INFINITY)
				for (i in it.toArray()) {
					if (i.x < leftMost.x) {
						leftMost = i
					} else if (i.x > rightMost.x) {
						rightMost = i
					} else if (i.y > topMost.y) {
						topMost = i
					} else if (i.y < bottomMost.y) {
						bottomMost = i
					}
				}
				//is the box diagonal to the left or right
				//convert to a "Sample", which is easier to deal with
				if (leftMost.y > rightMost.y) {
					//diagonal like \ (to left)
					Sample(leftMost, topMost, bottomMost, rightMost)
				} else {
					//diagonal like / (to right)
					Sample(topMost, rightMost, leftMost, bottomMost)
				}
			}
			.map {
				if (dist(it.topLeft, it.topRight) > dist(it.topLeft, it.bottomLeft)) {
					Sample(it.topRight, it.bottomRight, it.topLeft, it.bottomLeft)
				} else {
					it
				}
			}
			.filter {
				//how "square" is this sample identification?
				val distL = sqrt(sq(it.topLeft.x - it.bottomLeft.x) + sq(it.topLeft.y - it.bottomLeft.y))
				val distR = sqrt(sq(it.topRight.x - it.bottomRight.x) + sq(it.topRight.y - it.bottomRight.y))
				val distT = sqrt(sq(it.topLeft.x - it.topRight.x) + sq(it.topLeft.y - it.topRight.y))
				val distB = sqrt(sq(it.bottomLeft.x - it.bottomRight.x) + sq(it.bottomLeft.y - it.bottomRight.y))
				val q = abs(distL - distR) + abs(distT - distB)
				q < 50 && distL / distT > 2 && distL / distT < 5
			}
			.also { if (log) telemetry.addData("# Rectangles", it.size) }
	//Draw the samples, so we can see them
	if (draw) {
		samples.forEach { sample ->
			Imgproc.line(frame, sample.topLeft, sample.topRight, Scalar(0.0, 255.0, 0.0), 10)
			Imgproc.line(frame, sample.topRight, sample.bottomRight, Scalar(0.0, 255.0, 0.0), 10)
			Imgproc.line(frame, sample.bottomRight, sample.bottomLeft, Scalar(0.0, 255.0, 0.0), 10)
			Imgproc.line(frame, sample.bottomLeft, sample.topLeft, Scalar(0.0, 255.0, 0.0), 10)
		}
	}
	telemetry.update()
	return samples
}
fun sq(a: Double) = a*a
fun findClosest(samples: List<Sample>, width: Int, height: Int): Sample? {
	val centerx = width / 2
	val centery = height / 2
	var closest: Sample? = null
	var closestDist = Int.MAX_VALUE
	samples.forEach {
		val dist = dist(Point(centerx.toDouble(), centery.toDouble()), it.middle)
		if (dist < closestDist) {
			closestDist = dist.toInt()
			closest = it
		}
	}
	return closest
}
fun dist(a: Point, b: Point) = sqrt(sq(a.x - b.x) + sq(a.y - b.y))