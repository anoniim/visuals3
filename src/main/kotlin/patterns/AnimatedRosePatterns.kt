package patterns

import BaseSketch
import Screen
import processing.core.PVector

class AnimatedRosePatterns : BaseSketch(Screen(800, 800, fullscreen = false)) {

    // If n/d is rational, then the curve is closed
    // Interesting: (15.5, 17.5), (12.0, 17.9), (21.24, 42.75), (21.24, 44.81),
    private val n = 21.24f
    private val d = 44.81f
    private val step = 0.05f // 0.05f

    private val scale: Float = 350f
    private var theta = 0f
    private var last = calculateXY(theta)

    override fun setup() {
        background(grey3)
    }

    override fun draw() {
        translate(width / 2f, height / 2f)
        drawRoseShape()
        incrementAngle()
        println(theta)
    }

    private fun drawRoseShape() {
        noFill()
        strokeWeight(1f)
        stroke(grey9)
        beginShape()
        vertex(last.x, last.y)
        val current = calculateXY(theta + step)
        vertex(current.x, current.y)
        endShape()
        last = current
    }

    private fun calculateXY(angle: Float): PVector {
        val radius = scale * cos(n / d * angle)
        val x = radius * cos(angle)
        val y = radius * sin(angle)
        return PVector(x, y)
    }

    private fun incrementAngle() {
        theta += step
        if (theta > TWO_PI * d) {
            noLoop()
        }
    }
}