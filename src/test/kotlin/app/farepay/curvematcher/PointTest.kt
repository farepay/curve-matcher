package app.farepay.curvematcher

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PointExtendOnLineTests : StringSpec({
    "returns a point distance away from the end point" {
        val p1 = Point(0.0, 0.0)
        val p2 = Point(8.0, 6.0)
        Point.extendOnLine(p1, p2, 5.0) shouldBe Point(12.0, 9.0)
    }
    "works with negative distances" {
        val p1 = Point(0.0, 0.0)
        val p2 = Point(8.0, 6.0)
        Point.extendOnLine(p1, p2, -5.0) shouldBe Point(4.0, 3.0)
    }
    "works when p2 is before p1 in the line" {
        val p1 = Point(12.0, 9.0)
        val p2 = Point(8.0, 6.0)
        Point.extendOnLine(p1, p2, 10.0) shouldBe Point(0.0, 0.0)
    }
    "works with vertical lines" {
        val p1 = Point(2.0, 4.0)
        val p2 = Point(2.0, 6.0)
        Point.extendOnLine(p1, p2, 7.0) shouldBe Point(2.0, 13.0)
    }
    "works with vertical lines where p2 is above p1" {
        val p1 = Point(2.0, 6.0)
        val p2 = Point(2.0, 4.0)
        Point.extendOnLine(p1, p2, 7.0) shouldBe Point(2.0, -3.0)
    }
})
