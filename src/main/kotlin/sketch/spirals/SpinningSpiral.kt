package sketch.spirals

import BaseSketch
import Screen


class SpinningSpiral : BaseSketch(
    Screen(800, 800),
    fullscreen = false,
) {

    private val frequencyFrames = 300f // 300-1000 (300)
    private val speed = 0.01f // 0.01
    private val travelDistance = 1f // 0.5-7 (1)
    private val segmentLength = 0.8f // 0.2-5 (0.8)
    private val segmentMoveDelay = 20 // 1-50 (20)
    private val segmentSpacing = 20f // 10-50 (10, 20, 40)
    private val strokeWeight = 10f
    private val radiusSpacing = 25f // 10-30 (25)

    private val segments = mutableListOf<Segment>()

    override fun setup() {
        for (i in 0..90) {
            segments.add(Segment(i.toFloat()))
        }
    }

    override fun draw() {
        translate(screen.centerX, screen.centerY)
        background(grey3)
        noFill()
        drawSpiral()
    }

    private fun drawSpiral() {
        strokeWeight(strokeWeight)
        for (segment in segments) {
            segment.update()
            segment.show()
        }
    }

    inner class Segment(val i: Float) {

        private val radius = i * radiusSpacing

        private var startColor = grey11
        private var targetColor = startColor
        private var currentAngle = radians(i * segmentSpacing)
        private var originAngle = currentAngle
        private var targetAngle = currentAngle
        private var move = false

        fun update() {
            initiateMove()
            doMove()
            finishMove()
        }

        private fun initiateMove() {
            if (100 + (-1 * frameCount + i * segmentMoveDelay) % frequencyFrames == 0f) {
                move = true
                originAngle = currentAngle
                targetAngle = currentAngle - travelDistance
                targetColor = generateNewColor(frameCount + i)
            }
        }

        private fun doMove() {
            if (move) currentAngle -= speed
        }

        private fun finishMove() {
            if (currentAngle <= targetAngle) {
                move = false
                startColor = targetColor
            }
        }

        fun show() {
            setColor()
            arc(0f, 0f, radius, radius, currentAngle, getEndAngle())
        }

        private fun generateNewColor(index: Float): Int {
            val random = index / 50f
            val r = map(noise(random), 0f, 1f, 0f, 200f)
            val g = map(noise(random + 1000), 0f, 1f, 0f, 200f)
            val b = map(noise(random + 5000), 0f, 1f, 0f, 200f)
            return color(r, g, b)
        }

        private fun setColor() {
            if (originAngle != targetAngle) {
                val colorProgress = map(currentAngle, originAngle, targetAngle, 0f, 1f)
                stroke(lerpColor(startColor, targetColor, colorProgress))
            } else {
                stroke(startColor)
            }
        }

        private fun getEndAngle() = currentAngle + segmentLength
    }
}