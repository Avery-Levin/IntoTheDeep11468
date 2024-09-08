package dev.anygenericname

import nu.pattern.OpenCV
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import java.util.*
import kotlin.math.*

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
	/**
	 * This method returns the angle of the line between the middle of the sample and the midpoint of the top line, and the line that goes across the middle of the sample horizontally.
	 */
	fun getAngle(): Double {
		val middleTop = Point((topLeft.x + topRight.x) / 2, (topLeft.y + topRight.y) / 2)
		val middleBottom = Point((bottomLeft.x + bottomRight.x) / 2, (bottomLeft.y + bottomRight.y) / 2)
		val middle = Point((middleTop.x + middleBottom.x) / 2, (middleTop.y + middleBottom.y) / 2)
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
		val middleTop = Point((topLeft.x + topRight.x) / 2, (topLeft.y + topRight.y) / 2)
		val middleBottom = Point((bottomLeft.x + bottomRight.x) / 2, (bottomLeft.y + bottomRight.y) / 2)
		val middle = Point((middleTop.x + middleBottom.x) / 2, (middleTop.y + middleBottom.y) / 2)
		val angle = findAngle(middle.x, middleBottom.x, middle.y, middleBottom.y)
		Imgproc.circle(mat, middle, 25, Scalar(0.0, 255.0, 0.0), 10)
		Imgproc.putText(mat, angle.toString(), Point(middle.x + 40, middle.y + 40), 0, 1.0, Scalar(0.0, 255.0, 0.0), 3)
		Imgproc.line(mat, middle, middleBottom, Scalar(0.0, 255.0, 0.0), 10)
		println("mid:${middle.x}, ${middle.y} midTop: ${middleBottom.x}, ${middleBottom.y}, angle: $angle")
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

//Note: png is exact, jpg is lossy
fun main() {
	//Red: 228, 67, 56. 241, 67, 78
	OpenCV.loadLocally()
	File("imagesIn").mkdir()
	File("imagesOut").mkdir()
	for (i in File("imagesIn").listFiles()!!) {
		val frame = Imgcodecs.imread(i.path)
		for (color in Color.entries) {
			val t1 = System.currentTimeMillis()
			val samples = process(frame, color, false)
			println("Time taken: ${System.currentTimeMillis() - t1}ms")
			//uncomment to save output. change frame to mat on the last two lines if you don't want background
			//val mat = Mat.zeros(frame.size(), frame.type())
			//samples.forEach { it.drawDirections(frame) }
			//Imgcodecs.imwrite("imagesOut/${color}${i.nameWithoutExtension}.png", frame)
		}
	}
}
fun process(frame: Mat, color: Color, draw: Boolean = true): List<Sample> {
	//Convert to binary image 1 is target color, 0 is not target color.
	val out = Mat()
	when (color) {
		Color.RED -> Core.inRange(
			//BGR
			frame,
			Scalar(0.0, 0.0, 160.0),
			Scalar(120.0, 120.0, 255.0),
			out
		)
		Color.BLUE -> Core.inRange(
			//BGR
			frame,
			Scalar(160.0, 0.0, 0.0),
			Scalar(255.0, 130.0, 130.0),
			out
		)
		Color.YELLOW -> Core.inRange(
			//BGR
			//214, 163, 45
			//198, 137, 42
			frame,
			Scalar(35.0, 130.0, 190.0),
			Scalar(70.0, 200.0, 250.0),
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
	//filter out small contours. These are false positives.
	val samples = contours.filter { Imgproc.contourArea(it) > 1000 }
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
		.filter {
			//how "square" is this sample identification?
			val distL = sqrt(sq(it.topLeft.x - it.bottomLeft.x) + sq(it.topLeft.y - it.bottomLeft.y))
			val distR = sqrt(sq(it.topRight.x - it.bottomRight.x) + sq(it.topRight.y - it.bottomRight.y))
			val distT = sqrt(sq(it.topLeft.x - it.topRight.x) + sq(it.topLeft.y - it.topRight.y))
			val distB = sqrt(sq(it.bottomLeft.x - it.bottomRight.x) + sq(it.bottomLeft.y - it.bottomRight.y))
			val q = abs(distL - distR) + abs(distT - distB)
			q < 50 && distL / distT > 2 && distL / distT < 5
		}
	//Draw the samples, so we can see them
	if (draw) {
		samples.forEach { sample ->
			Imgproc.line(frame, sample.topLeft, sample.topRight, Scalar(0.0, 255.0, 0.0), 10)
			Imgproc.line(frame, sample.topRight, sample.bottomRight, Scalar(0.0, 255.0, 0.0), 10)
			Imgproc.line(frame, sample.bottomRight, sample.bottomLeft, Scalar(0.0, 255.0, 0.0), 10)
			Imgproc.line(frame, sample.bottomLeft, sample.topLeft, Scalar(0.0, 255.0, 0.0), 10)
		}
	}
	return samples
}
fun sq(a: Double) = a*a