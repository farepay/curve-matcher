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

    "gives correct results 0.05904781410962402" {
        val curve1 = Curve(listOf(
            Point(x = -1.3383296967610327, y = -0.3549407645542184),
            Point(x = -1.3162547250509251, y = -0.2828077308252626),
            Point(x = -1.294179753340817, y = -0.21067469709630685),
            Point(x = -1.2721047816307094, y = -0.1385416633673512),
            Point(x = -1.2500298099206018, y = -0.06640862963839547),
            Point(x = -1.227954838210494, y = 0.00572440409056027),
            Point(x = -1.2058798665003863, y = 0.07785743781951598),
            Point(x = -1.1838048947902784, y = 0.1499904715484719),
            Point(x = -1.1617299230801708, y = 0.2221235052774275),
            Point(x = -1.139654951370063, y = 0.2942565390063834),
            Point(x = -1.1170461537299434, y = 0.3654441946895771),
            Point(x = -1.0438772186147467, y = 0.34709246820259754),
            Point(x = -0.9707082834995506, y = 0.3287407417156183),
            Point(x = -0.8975393483843545, y = 0.31038901522863893),
            Point(x = -0.8243704132691579, y = 0.2920372887416595),
            Point(x = -0.7512014781539613, y = 0.2736855622546801),
            Point(x = -0.6780325430387649, y = 0.25533383576770063),
            Point(x = -0.6048636079235683, y = 0.23698210928072128),
            Point(x = -0.5316946728083719, y = 0.21863038279374203),
            Point(x = -0.4585257376931754, y = 0.20027865630676278),
            Point(x = -0.3853568025779789, y = 0.18192692981978348),
            Point(x = -0.3121878674627827, y = 0.16357520333280415),
            Point(x = -0.23901893234758617, y = 0.14522347684582484),
            Point(x = -0.16584999723238997, y = 0.12687175035884554),
            Point(x = -0.09268106211719349, y = 0.10852002387186624),
            Point(x = -0.019512127001997278, y = 0.09016829738488691),
            Point(x = 0.05365680811319923, y = 0.07181657089790763),
            Point(x = 0.1268257432283957, y = 0.053464844410928275),
            Point(x = 0.19999467834359225, y = 0.03511311792394892),
            Point(x = 0.2731636134587884, y = 0.01676139143696951),
            Point(x = 0.34633254857398466, y = -0.0015903350500098903),
            Point(x = 0.41950148368918116, y = -0.019942061536989256),
            Point(x = 0.4926704188043777, y = -0.03829378802396868),
            Point(x = 0.5658393539195742, y = -0.0566455145109481),
            Point(x = 0.6390082890347707, y = -0.07499724099792746),
            Point(x = 0.7121772241499672, y = -0.0933489674849069),
            Point(x = 0.7853461592651638, y = -0.11170069397188631),
            Point(x = 0.8585150943803602, y = -0.13005242045886575),
            Point(x = 0.9316840294955568, y = -0.1484041469458451),
            Point(x = 1.0048529646107534, y = -0.16675587343282455),
            Point(x = 1.0780218997259499, y = -0.18510759991980397),
            Point(x = 1.1511908348411464, y = -0.2034593264067834),
            Point(x = 1.2243597699563429, y = -0.22181105289376274),
            Point(x = 1.2975287050715394, y = -0.24016277938074218),
            Point(x = 1.3706976401867361, y = -0.25851450586772157),
            Point(x = 1.4438665753019324, y = -0.276866232354701),
            Point(x = 1.517035510417129, y = -0.29521795884168045),
            Point(x = 1.5902044455323256, y = -0.31356968532865986),
            Point(x = 1.6633733806475222, y = -0.3319214118156393),
            Point(x = 1.7365423157627176, y = -0.35027313830261847)
        ))
        val curve2 = Curve(listOf(
            Point(x = -1.325757081156583, y = -0.3993463225547311),
            Point(x = -1.3060983614008737, y = -0.32651766289034356),
            Point(x = -1.2864396416451642, y = -0.253689003225956),
            Point(x = -1.2667809218894548, y = -0.1808603435615684),
            Point(x = -1.2471222021337454, y = -0.10803168389718085),
            Point(x = -1.2274634823780362, y = -0.03520302423279328),
            Point(x = -1.207804762622327, y = 0.037625635431594286),
            Point(x = -1.188146042866618, y = 0.11045429509598186),
            Point(x = -1.1684873231109085, y = 0.18328295476036943),
            Point(x = -1.1488286033551995, y = 0.256111614424757),
            Point(x = -1.1286048474457988, y = 0.3280132121074092),
            Point(x = -1.0548649470350413, y = 0.3121101929488881),
            Point(x = -0.9811250466242839, y = 0.296207173790367),
            Point(x = -0.907385146213526, y = 0.2803041546318459),
            Point(x = -0.8336452458027684, y = 0.26440113547332483),
            Point(x = -0.7599053453920109, y = 0.24849811631480373),
            Point(x = -0.6861654449812534, y = 0.23259509715628265),
            Point(x = -0.6124255445704959, y = 0.21669207799776155),
            Point(x = -0.5386856441597384, y = 0.20078905883924048),
            Point(x = -0.4649457437489809, y = 0.1848860396807194),
            Point(x = -0.3912058433382234, y = 0.1689830205221983),
            Point(x = -0.3174659429274659, y = 0.15308000136367722),
            Point(x = -0.24372604251670837, y = 0.13717698220515612),
            Point(x = -0.16998614210595087, y = 0.12127396304663504),
            Point(x = -0.09624624169519334, y = 0.10537094388811395),
            Point(x = -0.02250634128443583, y = 0.08946792472959288),
            Point(x = 0.05123355912632168, y = 0.07356490557107179),
            Point(x = 0.1249734595370792, y = 0.0576618864125507),
            Point(x = 0.1987133599478367, y = 0.041758867254029615),
            Point(x = 0.27245326035859424, y = 0.025855848095508525),
            Point(x = 0.34619316076935175, y = 0.00995282893698744),
            Point(x = 0.41993306118010926, y = -0.0059501902215336475),
            Point(x = 0.49367296159086677, y = -0.021853209380054733),
            Point(x = 0.5674128620016242, y = -0.03775622853857657),
            Point(x = 0.6411527624123817, y = -0.0536592476970984),
            Point(x = 0.7148926628231392, y = -0.06956226685561949),
            Point(x = 0.7886325632338967, y = -0.08546528601414058),
            Point(x = 0.8623724636446543, y = -0.1013683051726624),
            Point(x = 0.9361123640554118, y = -0.1172713243311835),
            Point(x = 1.0098522644661694, y = -0.13317434348970458),
            Point(x = 1.0835921648769269, y = -0.1490773626482264),
            Point(x = 1.1573320652876844, y = -0.16498038180674826),
            Point(x = 1.231071965698442, y = -0.18088340096526934),
            Point(x = 1.3048118661091994, y = -0.19678642012379116),
            Point(x = 1.378551766519957, y = -0.21268943928231226),
            Point(x = 1.4522916669307144, y = -0.2285924584408341),
            Point(x = 1.526031567341472, y = -0.2444954775993552),
            Point(x = 1.5997714677522294, y = -0.260398496757877),
            Point(x = 1.673511368162987, y = -0.2763015159163981),
            Point(x = 1.747251268573746, y = -0.29220453507491995)
        ))

        curve1.distance(curve2) shouldBe (0.05904781410962402 plusOrMinus 0.001)
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

private val rotations = arrayOf(Math.PI / 3.0, 1.3 * Math.PI, Math.PI, -1.0)
private val translations = arrayOf(18.0, -3.0, -2000.0, 90.0, 1.345)
private val scales = arrayOf(0.2, 1.7, 10.0, 2000.0)

private fun translateScaleAndRotate(curve: Curve, rotation: Double, translation: Double, scale: Double): Curve {
    return Curve(curve.points.map { (x, y) ->
        Point(
            x = scale * (x + translation),
            y = scale * (y + translation)
        )
    }).rotate(rotation)
}

class CurveSimilarityTests : StringSpec({
    "returns 1 if curves are identical no matter the rotation, scale, and translation between the curves" {
        val curve = Curve(listOf(
            Point(0.0, 0.0),
            Point(2.0, 4.0),
            Point(18.0, -3.0)
        ))
        rotations.forEach { theta ->
            translations.forEach { translation ->
                scales.forEach { scale ->
                    val newCurve = translateScaleAndRotate(curve, theta, translation, scale)
                    curve.similarityTo(newCurve) shouldBe (1.0 plusOrMinus 0.001)
                }
            }
        }
    }

    "allows restricting the rotation angles that are checked" {
        val curve = Curve(
            listOf(
                Point(0.0, 0.0),
                Point(2.0, 4.0),
                Point(18.0, -3.0)
            )
        )
        val withinRangeRotations = listOf(0.0, -0.2, -0.3, 0.2, 0.3)
        val outOfRangeRotations = listOf(-0.5, 0.5, Math.PI)

        translations.forEach { translation ->
            scales.forEach { scale ->
                withinRangeRotations.forEach { theta ->
                    val newCurve = translateScaleAndRotate(curve, theta, translation, scale)
                    curve.similarityTo(newCurve, restrictRotationAngle = 0.3) shouldBe (1.0 plusOrMinus 0.001)
                }
                outOfRangeRotations.forEach { theta ->
                    val newCurve = translateScaleAndRotate(curve, theta, translation, scale)
                    curve.similarityTo(newCurve, restrictRotationAngle = 0.3) shouldBeLessThan 0.9
                }
            }
        }
    }
})
