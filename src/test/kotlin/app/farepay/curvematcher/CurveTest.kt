package app.farepay.curvematcher

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.doubles.shouldBeLessThan
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

class CurveDistanceTests : StringSpec({
    "is 0 if the curves are the same" {
        val curve1 = Curve(listOf(
            Point(0.0, 0.0),
            Point(4.0, 4.0)
        ))
        val curve2 = Curve(listOf(
            Point(0.0, 0.0),
            Point(4.0, 4.0)
        ))

        curve1.distance(curve2) shouldBe 0
        curve2.distance(curve1) shouldBe 0
    }
    "less than then max length of any segment if curves are identical" {
        val curve1 = Curve(listOf(
            Point(0.0, 0.0),
            Point(2.0, 2.0),
            Point(4.0, 4.0)
        ))
        val curve2 = Curve(listOf(
            Point(0.0, 0.0),
            Point(4.0, 4.0)
        ))

        curve1.subdivide().distance(curve2.subdivide()) shouldBeLessThan 0.5
        curve1.subdivide(0.1).distance(curve2.subdivide(0.1)) shouldBeLessThan 0.1
        curve1.subdivide(0.01).distance(curve2.subdivide(0.01)) shouldBeLessThan 0.01
    }
    "will be the dist of the starting points if those are the only difference" {
        val curve1 = Curve(listOf(
            Point(1.0, 0.0),
            Point(4.0, 4.0)
        ))
        val curve2 = Curve(listOf(
            Point(0.0, 0.0),
            Point(4.0, 4.0)
        ))

        curve1.distance(curve2) shouldBe 1
        curve2.distance(curve1) shouldBe 1
    }
    "gives correct results 1" {
        val curve1 = Curve(listOf(
            Point(1.0, 0.0),
            Point(2.4, 43.0),
            Point(-1.0, 4.3),
            Point(4.0, 4.0)
        ))
        val curve2 = Curve(listOf(
            Point(0.0, 0.0),
            Point(14.0, 2.4),
            Point(4.0, 4.0)
        ))

        curve1.distance(curve2) shouldBe (39.0328 plusOrMinus 0.001)
    }
    "gives correct results 2" {
        val curve1 = Curve(listOf(
            Point(63.44852183813086, 24.420192387119634),
            Point(19.472881275654252, 77.306125067647),
            Point(22.0150089075698, 5.115699052924483),
            Point(90.85925658487311, 80.37914225209231),
            Point(96.81784894898642, 81.33960258698878),
            Point(75.45756084113779, 96.87017085629488),
            Point(87.77706429291412, 15.70163068744641),
            Point(37.36893642596093, 44.86136460914203),
            Point(37.35720453846581, 90.65479959420186),
            Point(41.28185352889147, 34.02195976325355),
            Point(27.65820587389076, 12.382281496757997),
            Point(42.43674529129338, 33.38959395979349),
            Point(3.377463737709774, 52.387593489371966),
            Point(50.93481600582428, 16.868378936261696),
            Point(68.46675900966153, 52.04265123799294),
            Point(1.9235036598383326, 55.87935516876048),
            Point(28.02334783421687, 98.08317663407114),
            Point(53.74539146366855, 33.27918237496243),
            Point(49.39670128874036, 47.59663728140997),
            Point(47.51990428391566, 11.23339071630216),
            Point(53.31256301680558, 55.4279696833061),
            Point(38.797168750480026, 26.172634107810833),
            Point(45.604650160570515, 71.69212699940685),
            Point(36.83931368726911, 38.74324014933978),
            Point(68.76987877419623, 1.2518741233677577),
            Point(91.27606575268427, 96.2141050404784),
            Point(24.407614843135406, 76.20115332073458),
            Point(8.764170623754097, 37.003392529458104),
            Point(52.97112238152346, 9.76631343977752),
            Point(88.85357966283867, 60.767524033054144)
        ))
        val curve2 = Curve(listOf(
            Point(0.0, 0.0),
            Point(14.0, 2.4),
            Point(4.0, 4.0)
        ))

        curve1.distance(curve2) shouldBe (121.5429 plusOrMinus 0.001)
    }
    "doesn't overflow the node stack if the curves are very long" {
        val curve1 = Curve(listOf(
            Point(1.0, 0.0),
            Point(4.0, 4.0)
        )).rebalance(1000)
        val curve2 = Curve(listOf(
            Point(0.0, 0.0),
            Point(4.0, 4.0)
        )).rebalance(1000)

        curve1.distance(curve2) shouldBe 1
    }
})

class CurveNormalizationTests : StringSpec({
    "normalizes the scale and translation of the curve" {
        val curve1 = Curve(listOf(
            Point(0.0, 0.0),
            Point(4.0, 4.0)
        ))

        val normalizedCurve = curve1.normalize(rebalance = false)

        normalizedCurve.points[0].x shouldBe ((-1 * sqrt(2.0)) / 2 plusOrMinus 0.001)
        normalizedCurve.points[0].y shouldBe ((-1 * sqrt(2.0)) / 2 plusOrMinus 0.001)

        normalizedCurve.points[1].x shouldBe (sqrt(2.0) / 2 plusOrMinus 0.001)
        normalizedCurve.points[1].y shouldBe (sqrt(2.0) / 2 plusOrMinus 0.001)
    }

    "can be configured to rebalance with a custom number of points" {
        val curve1 = Curve(listOf(
            Point(0.0, 0.0),
            Point(4.0, 4.0)
        ))

        val normalizedCurve = curve1.normalize(estimationPoints = 3)

        normalizedCurve.points[0].x shouldBe ((-1 * sqrt(3.0)) / 2 plusOrMinus 0.001)
        normalizedCurve.points[0].y shouldBe ((-1 * sqrt(3.0)) / 2 plusOrMinus 0.001)

        normalizedCurve.points[1].x shouldBe 0.0
        normalizedCurve.points[1].y shouldBe 0.0

        normalizedCurve.points[2].x shouldBe (sqrt(3.0) / 2 plusOrMinus 0.001)
        normalizedCurve.points[2].y shouldBe (sqrt(3.0) / 2 plusOrMinus 0.001)
    }

    "gives identical results for identical curves with different numbers of points after rebalancing" {
        val curve1 = Curve(listOf(
            Point(0.0, 0.0),
            Point(4.0, 4.0)
        ))
        val normalizedCurve1 = curve1.normalize()

        val curve2 = Curve(listOf(
            Point(0.0, 0.0),
            Point(3.0, 3.0),
            Point(4.0, 4.0)
        ))
        val normalizedCurve2 = curve2.normalize()

        normalizedCurve1.points.forEachIndexed { i, (p1x, p1y) ->
            val (p2x, p2y) = normalizedCurve2.points[i]

            p1x shouldBe (p2x plusOrMinus 0.001)
            p1y shouldBe (p2y plusOrMinus 0.001)
        }
    }
})

class CurveRotationAngleTests : StringSpec({
    "determines the optimal rotation angle to match 2 curves on top of each other" {
        val curve1 = Curve(listOf(
            Point(0.0, 0.0),
            Point(1.0, 0.0)
        ))
        val curve2 = Curve(listOf(
            Point(0.0, 0.0),
            Point(0.0, 1.0)
        ))

        curve1.rotationAngle(curve2) shouldBe ((-1 * Math.PI) / 2 plusOrMinus 0.001)
    }

    "return 0 if the curves have the same rotation" {
        val curve1 = Curve(listOf(
            Point(0.0, 0.0),
            Point(1.0, 1.0)
        ))
        val curve2 = Curve(listOf(
            Point(0.0, 0.0),
            Point(1.5, 1.5)
        ))

        curve1.rotationAngle(curve2) shouldBe 0.0
    }
})
