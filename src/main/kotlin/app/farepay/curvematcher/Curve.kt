package app.farepay.curvematcher

import app.farepay.curvematcher.Point.Companion.extendOnLine
import java.util.*
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

data class Curve(val points: List<Point>) {

    fun length(): Double {
        var prevPoint = points[0]
        var dist = 0.0
        for (currPoint in points.subList(1, points.size)) {
            dist += prevPoint.distance(currPoint)
            prevPoint = currPoint
        }
        return dist
    }

    fun subdivide(maxLen: Double = 0.05): Curve {
        val newCurvePoints = LinkedList(listOf(points[0]))
        points.subList(1, points.size).forEach { point ->
            val prevPoint = newCurvePoints[newCurvePoints.size - 1]
            val segLen = point.distance(prevPoint)
            if (segLen > maxLen) {
                val numNewPoints = ceil(segLen / maxLen)
                val newSegLen = segLen / numNewPoints

                var i = 0.0
                while (i < numNewPoints) {
                    newCurvePoints.addLast(extendOnLine(point, prevPoint, -1 * newSegLen * (i + 1)))
                    i += 1.0
                }
            } else {
                newCurvePoints.addLast(point)
            }
        }
        return Curve(newCurvePoints)
    }

    fun rebalance(numPoints: Int = 50): Curve {
        val curveLen = length()
        val segLen = curveLen / (numPoints - 1)
        val outlinePoints = LinkedList(listOf(points[0]))
        val endPoint = points.last()
        val remainingCurvePoints = LinkedList(points.drop(1))

        for (i in 0 until numPoints - 2) {
            var lastPoint = outlinePoints.last
            var remainingDist = segLen
            var outlinePointFound = false

            while (!outlinePointFound) {
                val nextPointDist = lastPoint.distance(remainingCurvePoints[0])
                if (nextPointDist < remainingDist) {
                    remainingDist -= nextPointDist
                    lastPoint = remainingCurvePoints.removeFirst()
                } else {
                    val nextPoint = extendOnLine(
                        lastPoint, remainingCurvePoints[0],
                        remainingDist - nextPointDist
                    )
                    outlinePoints.addLast(nextPoint)
                    outlinePointFound = true
                }
            }
        }
        outlinePoints.addLast(endPoint)
        return Curve(outlinePoints)
    }

    fun rotate(theta: Double) = Curve(
        points.map { point ->
            Point(
                x = cos(-1 * theta) * point.x - sin(-1 * theta) * point.y,
                y = sin(-1 * theta) * point.x + cos(-1 * theta) * point.y
            )
        }
    )
}
