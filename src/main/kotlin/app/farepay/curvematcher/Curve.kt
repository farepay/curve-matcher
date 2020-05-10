package app.farepay.curvematcher

import app.farepay.curvematcher.Point.Companion.extendOnLine
import java.util.*
import kotlin.math.*

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

    fun distance(that: Curve): Double {
        val thisLen = this.length()
        val thatLen = that.length()

        val longCurve = if (thisLen >= thatLen) { this } else { that }
        val shortCurve = if (thisLen <= thatLen) { this } else { that }

        fun calcVal(i: Int, j: Int, prevResultsCol: LinkedList<Double>, curResultsCol: LinkedList<Double>): Double {
            if (i == 0 && j == 0) {
                return longCurve.points[0].distance(shortCurve.points[0])
            }

            if (i > 0 && j == 0) {
                return max(prevResultsCol[0], longCurve.points[i].distance(shortCurve.points[0]))
            }

            val lastResult = curResultsCol[curResultsCol.size - 1]
            if (i == 0 && j > 0) {
                return max(lastResult, longCurve.points[0].distance(shortCurve.points[j]))
            }

            return max(minOf(prevResultsCol[j], prevResultsCol[j - 1], lastResult),
                longCurve.points[i].distance(shortCurve.points[j])
            )
        }

        var prevResultsCol = LinkedList<Double>()
        for (i in longCurve.points.indices) {
            val curResultsCol = LinkedList<Double>()

            for (j in shortCurve.points.indices) {
                curResultsCol.addLast(calcVal(i, j, prevResultsCol, curResultsCol))
            }
            prevResultsCol = curResultsCol
        }

        return prevResultsCol[shortCurve.points.size - 1]
    }

    fun normalize(rebalance: Boolean = true, estimationPoints: Int = 50): Curve {
        val curve = if (rebalance) { this.rebalance(estimationPoints) } else { this }
        val meanX = curve.points.map { it.x }.average()
        val meanY = curve.points.map { it.y }.average()
        val mean = Point(x = meanX, y = meanY)
        val translatedPoints = curve.points.map { it.subtract(mean) }
        val scale = sqrt(translatedPoints.map { (x, y) -> x * x + y * y}.average())
        return Curve(translatedPoints.map{ (x, y) -> Point(x = x / scale, y = y / scale) })
    }

    fun rotationAngle(relativeCurve: Curve): Double {
        if (points.size != relativeCurve.points.size) {
            throw IllegalArgumentException("curve and relativeCurve must have the same length")
        }

        val numerator = points.mapIndexed { i, (x, y) -> y * relativeCurve.points[i].x - x * relativeCurve.points[i].y}.sum()
        val denominator = points.mapIndexed { i, (x, y) -> x * relativeCurve.points[i].x + y * relativeCurve.points[i].y}.sum()
        return atan2(numerator, denominator)
    }
}
