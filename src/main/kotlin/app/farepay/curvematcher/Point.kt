package app.farepay.curvematcher

import kotlin.math.sqrt

data class Point(val x: Double, val y: Double) {

    fun magnitude() = sqrt(x * x + y * y)

    fun subtract(that: Point) =
        Point(
            x = this.x - that.x,
            y = this.y - that.y
        )

    fun distance(that: Point) = subtract(that).magnitude()

    companion object {
        fun extendOnLine(p1: Point, p2: Point, dist: Double): Point {
            val vect = p2.subtract(p1)
            val norm = dist / vect.magnitude()
            return Point(p2.x + norm * vect.x, p2.y + norm * vect.y)
        }
    }
}
