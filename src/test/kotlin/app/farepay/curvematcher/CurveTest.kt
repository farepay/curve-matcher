package app.farepay.curvematcher

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.math.sqrt

class CurveLengthTests : StringSpec({
    "return a curve length equals to distance between individual points" {
        val p1 = Point(0.0, 0.0)
        val p2 = Point(15.0, 0.0)
        val p3 = Point(30.0, 0.0)

        p1.distance(p2) shouldBe 15.0
        p2.distance(p3) shouldBe 15.0

        Curve(listOf(p1, p2, p3)).length() shouldBe p1.distance(p2) + p2.distance(p3)
    }
})

class CurveSubdivideTests : StringSpec({
    "leave the curve the same if segment lengths are less than maxLen apart" {
        val curve = Curve(listOf(
            Point(0.0, 0.0), Point(4.0, 4.0)
        ))
        curve.subdivide(10.0) shouldBe Curve(listOf(
            Point(0.0, 0.0), Point(4.0, 4.0)
        ))
    }
    "breaks up segments so that each segment is less than maxLen length" {
        val curve = Curve(listOf(
            Point(0.0, 0.0), Point(4.0, 4.0), Point(0.0, 8.0)
        ))
        curve.subdivide(sqrt(2.0)) shouldBe Curve(listOf(
            Point(0.0, 0.0),
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0),
            Point(4.0, 4.0),
            Point(3.0, 5.0),
            Point(2.0, 6.0),
            Point(1.0, 7.0),
            Point(0.0, 8.0)
        ))
    }
    "uses maxLen of 0.05 by default" {
        val curve = Curve(listOf(
            Point(0.0, 0.0), Point(0.0, 0.1)
        ))
        curve.subdivide() shouldBe Curve(listOf(
            Point(0.0, 0.0),
            Point(0.0, 0.05),
            Point(0.0, 0.1)
        ))
    }
})

class CurveRebalanceTests : StringSpec({
    "divides a curve into equally spaced segments" {
        val curve1 = Curve(listOf(
            Point(0.0, 0.0),
            Point(4.0, 6.0)
        ))
        curve1.rebalance(3) shouldBe Curve(listOf(
            Point(0.0, 0.0),
            Point(2.0, 3.0),
            Point(4.0, 6.0)
        ))

        val curve2 = Curve(listOf(
            Point(0.0, 0.0),
            Point(9.0, 12.0),
            Point(0.0, 24.0)
        ))
        curve2.rebalance(4) shouldBe Curve(listOf(
            Point(0.0, 0.0),
            Point(6.0, 8.0),
            Point(6.0, 16.0),
            Point(0.0, 24.0)
        ))
    }
})
