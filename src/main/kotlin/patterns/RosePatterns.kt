package patterns

import BaseSketch
import Screen
import util.roundTo

class RosePatterns : BaseSketch(Screen(800, 800, fullscreen = false)) {

    // Interesting n (for d: 8): 3.5-4.5, 8-9, 13.5
    private val nProgress = ValueProgress(minValue = 0f, maxValue = 6f)
    private val dProgress = ValueProgress(minValue = 0f, maxValue = 6f)

    private val scale: Float = 350f
    private val textSize = 24f
    private val textScale = 1.3f

    override fun draw() {
        translate(width / 2f, height / 2f)
        background(grey3)
        val (n, d) = mousePressControl()
        drawRoseShape(n, d)
        drawValueText(n, d)
    }

    private fun drawRoseShape(n: Float, d: Float) {
        noFill()
        strokeWeight(1f)
        stroke(grey9)
        beginShape()
        var theta = 0f
        while (theta <= TWO_PI * d) {
            val radius = scale * cos(n / d * theta)
            val x = radius * cos(theta)
            val y = radius * sin(theta)
            vertex(x, y)
            theta += 0.01f
        }
        endShape()
    }

    private fun drawValueText(n: Float, d: Float) {
        fill(grey9)
        textSize(textSize)
        text("n: $n", -widthF / 2 + textSize * textScale, -heightF / 2 + textSize * textScale)
        text("d: $d", -widthF / 2 + textSize * textScale, -heightF / 2 + 2 * (textSize * textScale))
    }

    private fun mousePressControl(): Pair<Float, Float> {
        if (mousePressed) {
            if (mouseY < height / 2) {
                nProgress.progress()
            } else {
                dProgress.progress()
            }
        }
        return Pair(nProgress.last, dProgress.last)
    }

    inner class ValueProgress(val minValue: Float, val maxValue: Float) {

        private val loopTime: Float = 2000f // 1000f
        private val numDecimalPoints = 2 // 3

        var last = 0f
        private var value = 0f

        fun progress() {
            if (mouseX < width / numDecimalPoints) {
                decrement()
            } else {
                increment()
            }
            last = map(value % loopTime, 0f, loopTime - 1, minValue, maxValue).roundTo(numDecimalPoints)
        }

        private fun increment() {
            value++
        }

        private fun decrement() {
            value--
            if (value < 0) value = loopTime
        }
    }
}