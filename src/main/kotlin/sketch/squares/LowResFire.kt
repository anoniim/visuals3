package sketch.squares

import BaseSketch
import Screen

class LowResFire : BaseSketch(Screen(600, 600)) {

    private val rectangles = List(50) {
        val growFunction = if (random(1f) < 0.5f) this::growLeft else this::growRight
        Rect(random(400f), random(400f), growFunction)
    }

    override fun draw() {
        translate(halfWidthF, 4 * heightF / 5f)
        background(grey3)

        rectangles.forEach {
            it.draw()
        }
    }

    private fun growLeft(maxWidth: Float, maxHeight: Float) {
        rotate(PI)
        translate(-maxWidth / 2f, 0f)
    }

    private fun growRight(maxWidth: Float, maxHeight: Float) {
        rotate(PI + HALF_PI)
        translate(0f, -maxHeight / 2f)
    }

    private inner class Rect(
        val maxWidth: Float,
        val maxHeight: Float,
        val growFunction: (Float, Float) -> Unit
    ) {

        private val phase = random(1000)
        private val fillColor = getFillColor()

        private fun getFillColor(): Int {
            return if (growFunction == this@LowResFire::growRight) {
                if (maxWidth / 3f > maxHeight) red
                else if (maxWidth > maxHeight) orange else yellow
            } else {
                if (maxHeight / 3f > maxWidth) red
                else if (maxHeight > maxWidth) orange else yellow
            }
        }

        fun draw() {
            pushMatrix()
            growFunction(maxWidth, maxHeight)
            fill(fillColor, 50f)
            noStroke()
            val grow = abs(sin((phase + frameCountF) / 100f))
            rect(0f, 0f, grow * maxWidth, grow * maxHeight, 100f)
            popMatrix()
        }
    }
}