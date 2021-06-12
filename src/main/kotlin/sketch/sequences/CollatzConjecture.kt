package sketch.sequences

import BaseSketch
import Screen

class CollatzConjecture : BaseSketch(
    Screen(1500, 900),
    fullscreen = false,
) {

    private var length = 12f
    private val evenAngle = PI / 8
    private var oddAngleFactor = 1.7f
    private val oddAngle
        get() = -oddAngleFactor * PI / 8

    override fun setup() {
        frameRate(3f)
    }

    override fun draw() {
        length = map(mouseYF, 0f, heightF, 1f, 20f)
        oddAngleFactor = map(mouseXF, 0f, widthF, 1f, 2.5f)
        drawCollatz()
    }

    private fun drawCollatz() {
        background(grey3)
        stroke(grey11, 100f)
        strokeWeight(1f)
        resetPosition()

        val numbers = 2700
        for (i in 0..numbers) {
            var number = i
            do {
                translate(0f, -length)
                number = collatz(number)
                drawLine()
            } while (number > 1)
            resetPosition()
        }
    }

    private fun resetPosition() {
        resetMatrix()
//        translate(screen.centerX, screen.centerY)
        translate(screen.centerX, 0.7f * screen.heightF)
    }

    private fun drawLine() {
        line(0f, 0f, 0f, -length)
    }

    private fun collatz(number: Int): Int {
        return if (number % 2 == 0) {
            rotate(evenAngle)
            number / 2
        } else {
            rotate(oddAngle)
            3 * number + 1
        }
    }
}

