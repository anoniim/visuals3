package sketch.patterns

import BaseSketch
import Screen
import util.roundTo

class MaurerRosePatterns : BaseSketch(
    Screen(800, 800),
    fullscreen = true
) {

    private val nProgress = ValueProgress(minValue = 2f, maxValue = 8f)
    private val dProgress = ValueProgress(minValue = 0f, maxValue = 360f)

    private val scale = 450f
    private val textSize = 24f
    private val textScale = 1.3f
    private val stopArea = screen.widthF / 8

    override fun draw() {
        translate(width / 2f, height / 2f)
        background(grey3)
        val (n, d) = mousePressControl()
        drawRoseShape(n, d)
        drawValueText(n, d)
        drawControlGuidelines()
        if (frameCount % 5 == 0) {
            dProgress.progress()
        }
    }

    private fun drawControlGuidelines() {
        line(-stopArea, -halfHeightF, -stopArea, -halfHeightF + 20f)
        line(stopArea, -halfHeightF, stopArea, -halfHeightF + 20f)
    }

    private fun drawRoseShape(n: Float, d: Float) {
        noFill()
        strokeWeight(1f)
        stroke(grey9)
        beginShape()
        var i = 0f
        while (i <= d) {
            val radius = scale * sin(radians(n * i * d))
            val x = radius * cos(radians(i * d))
            val y = radius * sin(radians(i * d))
            vertex(x, y)
            i += 1f
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

        private val maxLoopTime: Float = 10000f // 1000f
        private val numDecimalPoints = 2 // 3
        private var increment = 1f

        var last = 0f
        private var value = 0f

        fun progress() {
            val mouseControl = mouseX - halfWidthF
            if (mouseControl < -stopArea) {
                decrement()
            } else if (mouseControl > stopArea) {
                increment()
            }
            increment = map(abs(mouseControl), stopArea, halfWidthF, 0.1f, 10f)
            last = map(value % maxLoopTime, 0f, maxLoopTime - 1, minValue, maxValue).roundTo(numDecimalPoints)
        }

        private fun increment() {
            value += increment
        }

        private fun decrement() {
            value -= increment
            if (value < 0) value = maxLoopTime
        }
    }
}