package sketch.patterns

import RecordedSketch
import Screen
import processing.core.PVector

class AnimatedRosePatterns : RecordedSketch(Screen(800, 800, fullscreen = false)) {

    // If n/d is rational, then the curve is closed
    // Interesting: (1, 6), (2, 7), (15.5, 17.5), (12.0, 17.9), (21.24, 42.75), (21.24, 44.81),
    private val n = 18.5f
    private val d = 6f
    private val step = 0.03f // 0.05f

    private val scale: Float = 350f
    private var theta = 0f
    private val maxAngle = TWO_PI * d
    private var last = calculateXY(theta)

    override fun setup() {
        background(grey3)
        record(ceil(maxAngle/step), label = "twoSeventh", skipFrames = 2, record = false)
    }

    override fun render(percent: Float) {
        translate(width / 2f, height / 2f)
        drawRoseShape()
        incrementAngle()
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
        if (theta > maxAngle) {
            noLoop()
        }
    }
}