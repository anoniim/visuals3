package session.winter21

import BaseSketch
import Screen
import processing.core.PApplet
import processing.core.PVector
import input.MidiController
import util.curveVertex
import util.translateToCenter
import util.vertex

fun main() {
    PApplet.main(CircularStrings::class.java)
}

class CircularStrings : BaseSketch(Screen.EPSON_PROJECTOR) {

    private val keyRange = MidiController.PAD_24..MidiController.PAD_72

    // config
    private val maxPluckOffset = 30f
    private val centralGap = 100f
    private val numOfCircles = 49
    private val resolution = 10

    private val circles by lazy(this::generateCircles)
    private val spacing by lazy { (1.5f * heightF / 2f - centralGap) / numOfCircles }
    private val midiController by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        super.setup()
        midiController.on(keyRange,
            { pitch, velocity ->
                circles[pitch - 35].pluck(velocity)
            })
    }

    override fun draw() {
        translateToCenter()
        rotate(HALF_PI)
        background(grey3)

        circles.forEach {
            it.update()
            it.draw()
        }
    }

    private fun generateCircles() = List(numOfCircles) {
        Circle(centralGap + it * spacing, (it + 2) * resolution)
    }

    private inner class Circle(
        val radius: Float,
        val resolution: Int
    ) {

        private val angle = TWO_PI / resolution
        private var time = 0f
        private var dampening = 0f
        private var isPlucked = false
        private var color = grey11

        fun update() {
            time += 1f
            dampening -= 0.01f
            if (dampening <= 0f) {
                time = 0f
                dampening = 0f
                isPlucked = false
            }
        }

        fun pluck(strength: Int) {
            dampening += map(strength.toFloat(), 0f, 120f, 0f, 1f)
            if (!isPlucked) color = colors.random
            isPlucked = true
        }

        fun draw() {
            noFill()
            stroke(getColor())
            strokeWeight(4f)
            // config
//            addVerticesWithCurve()
            addVertices()
        }

        private fun getColor() = if (isPlucked) {
            lerpColor(grey11, color, dampening)
        } else grey11

        private fun addVertices() {
            beginShape()
            for (index in 0 until resolution) {
                val theta = index * angle
                vertex(PVector.fromAngle(theta).mult(gerRadiusForPoint(index)))
            }
            endShape(CLOSE)
        }

        private fun addVerticesWithCurve() {
            beginShape()
            for (index in 1 until resolution) {
                val theta = index * angle
                vertex(PVector.fromAngle(theta).mult(gerRadiusForPoint(index)))
            }
            curveVertex(PVector.fromAngle(TWO_PI - angle).mult(gerRadiusForPoint(-1)))
            curveVertex(PVector.fromAngle(0f).mult(gerRadiusForPoint(0)))
            curveVertex(PVector.fromAngle(angle).mult(gerRadiusForPoint(1)))
            curveVertex(PVector.fromAngle(2 * angle).mult(gerRadiusForPoint(2)))
            endShape()
        }

        private fun gerRadiusForPoint(index: Int): Float {
            return radius + if (isPlucked)
                (index % 2 * 2 - 1) * // -1 or 1 based on index
                        sin(time / 10f) * // sine wave
                        maxPluckOffset *
                        dampening // 0+
            else 0f
        }
    }
}