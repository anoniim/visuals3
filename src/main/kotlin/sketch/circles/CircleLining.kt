package sketch.circles

import BaseSketch
import shapes.Circle

open class CircleLining : BaseSketch() {

    private val strokeColor = grey9
    private val fillColor = grey5
    private val backgroundColor = grey3

    override fun setup() {
        smooth()
        stroke(strokeColor)
        noFill()

        frameRate(1F)
    }

//    var i = 0 // 38, 48, 93

    override fun draw() {
        background(backgroundColor)
        translate(screen.centerX, screen.centerY)

        val r1 = 150F

        // Center circle
        Circle(this, 0F, 0F, r1).draw()


//        for (n in 1..100) {
//            for (m in 1 until n) {
        val n = 30
            val m = 23
                println("trying m/n = $m/$n")
                val i = m.toFloat()/n.toFloat()
                println("i = $i")
                val r2 = i * r1
                println("r2 = $r2")
                val (firstCircle: Circle, lastCircle: Circle) = secondLevel(r1, r2)

                stopIfClosedCircle(firstCircle, lastCircle, r2, n, m)
                println()
//            }
//        }
        stop()

//        if (i < 99) {
//            i++
//        } else {
//            i = 0
//        }
    }

    private fun secondLevel(
        r1: Float,
        r2: Float
    ): Pair<Circle, Circle> {
        var firstCircle: Circle = dummy()
        var lastCircle: Circle = dummy()

        val thetaGrowth = 2 * asin(r2 / (r1 + r2))
        var theta = 0F
        while (theta < 2 * PI) {
            val x = sin(theta) * (r1 + r2)
            val y = cos(theta) * (r1 + r2)
            val circle = Circle(this, x, y, r2)

            if (firstCircle.r == 0F) {
                firstCircle = circle
            }
            lastCircle = circle

            circle.draw()
            theta += thetaGrowth
        }
        return Pair(firstCircle, lastCircle)
    }

    private fun stopIfClosedCircle(
        firstCircle: Circle,
        lastCircle: Circle,
        r2: Float,
        n: Int,
        m: Int
    ) {
        val dist = dist(firstCircle.x, firstCircle.y, lastCircle.x, lastCircle.y)
        val approx2r2 = floor(2 * r2).toFloat()
        val approxDist = floor(dist).toFloat()
        println("2r2: $approx2r2")
        println("dist: $approxDist")
        if (approxDist == 0F || abs(approxDist - approx2r2) < 2) {
            println()
            println("*** found for m/n = $m/$n")
//            stop()
        }
    }

    private fun dummy(): Circle {
        return Circle(this, 0F, 0F, 0F)
    }
}


