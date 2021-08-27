package sketch.circles

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import util.*

fun main() = PApplet.main(Incircles::class.java)

/**
 * TODO map to music (note velocity == circle velocity, high pitch small circles, low pitch big circles)
 */
class Incircles : BaseSketch() {

    private val radius = 400f
    private val dampening
        get() = if (keyPressed) 1f else 0.1f
    private val incircles = List(22) {
        val i = it + 1
        // config
        val aDelta = i * (PI / 1000f * 1f)
        val bDelta = i * (PI / 1000f * 2f)
        val cDelta = i * (PI / 1000f * 4f)
        // config
//        val aDelta = i * ((i / 10000f) + 0.005f)
//        val bDelta = i * ((i / 10000f) + 0.006f)
//        val cDelta = i * ((i / 10000f) + 0.007f)
        // config
//        val aDelta = 0.005f
//        val bDelta = 0.006f
//        val cDelta = i * ((i / 10000f) + 0.007f)
        // config
//        val aDelta = -0.005f
//        val bDelta = 0.005f
//        val cDelta = i * ((i / 10000f) + 0.007f)

        Incircle(aDelta, bDelta, cDelta)
    }

    override fun setup() {
    }

    override fun draw() {
        translateToCenter()
        background(grey3)

//        drawOuterCircle()
        incircles.forEach {
            // config
            it.drawIncircle()
//            it.drawIncenterPath()
//            it.drawTriangle()

            it.update()
        }
    }

    private fun drawOuterCircle() {
        noFill()
        stroke(yellow)
        strokeWeight(4f)
        circle(PVector(0f, 0f), radius * 2)
    }

    private inner class Incircle(
        private val aDelta: Float = 0.02f,
        private val bDelta: Float = 0.03f,
        private val cDelta: Float = 0.01f,
    ) {

        private val incenterPath = mutableListOf<PVector>()

        private var aAngle = 0f
        private var bAngle = HALF_PI
        private var cAngle = PI

        private var b = PVector.fromAngle(bAngle).mult(radius)
        private var a = PVector.fromAngle(aAngle).mult(radius)
        private var c = PVector.fromAngle(cAngle).mult(radius)
        private var incenter = PVector()
        private var inradius = 0f

        fun update() {
            aAngle += aDelta * dampening
            bAngle += bDelta * dampening
            cAngle += cDelta * dampening
            b = PVector.fromAngle(bAngle).mult(radius)
            a = PVector.fromAngle(aAngle).mult(radius)
            c = PVector.fromAngle(cAngle).mult(radius)
            recalculateIncircle()
        }

        private fun recalculateIncircle() {
            val ab = a.dist(b)
            val bc = b.dist(c)
            val ca = c.dist(a)
            incenter = calculateIncenter(ab, bc, ca)
            inradius = calculateInradius(ab, bc, ca)
            incenterPath.add(incenter)
        }

        private fun calculateInradius(ab: Float, bc: Float, ca: Float): Float {
            val p = (ab + bc + ca) / 2
            val abcArea = sqrt(p * (p - ab) * (p - bc) * (p - ca))
            return (2 * abcArea) / (ab + bc + ca)
        }

        private fun calculateIncenter(ab: Float, bc: Float, ca: Float): PVector {
            val denominator = ab + bc + ca
            return PVector(
                (bc * a.x + ca * b.x + ab * c.x) / denominator,
                (bc * a.y + ca * b.y + ab * c.y) / denominator
            )
        }

        fun drawTriangle() {
            stroke(grey11)
            strokeWeight(2f)
            line(a, b)
            line(b, c)
            line(c, a)
        }

        fun drawIncenterPath() {
            stroke(red)
            strokeWeight(1f)
            noFill()
            beginShape()
            incenterPath.forEach {
                vertex(it)
            }
            endShape()
        }

        fun drawIncircle() {
            val alpha = map(inradius, 0f, radius, 155f, 0f)
            fill(color(150, 150, 70), alpha)
            stroke(grey11)
            strokeWeight(1f)
            circle(incenter, inradius * 2)
        }
    }
}