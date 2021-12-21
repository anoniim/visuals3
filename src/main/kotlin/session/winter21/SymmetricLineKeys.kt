package session.winter21

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import util.translate

fun main() {
    PApplet.main(SymmetricLineKeys::class.java)
}

class SymmetricLineKeys : BaseSketch() {

    // config
    private val moveSpeed = 4f // low without fixed spacing, higher otherwise
    private val fadeInSpeed = 1f
    private val expandSpeed = 3f
    private val lineColor = purple
    private val fixedSpacingEnabled = true
    private val fixedSpacing = 50f

    private val lines = mutableListOf<Line>()

    override fun draw() {
        translate(halfWidthF, 0.9f * heightF)
        background(grey3)

        lines.forEach {
            it.update()
            it.draw()
        }
    }

    override fun keyTyped() {
        if (keyPressed && key == ' ') {
            generateNewLine()
        }
    }

    private fun generateNewLine() {
        val length = random(800f)
        lines.forEach { it.moveUp() }
        lines.add(Line(lineLength = length, color = lineColor))
    }

    private inner class Line(
        val origin: PVector = PVector(),
        private val lineLength: Float = 80f,
        val color: Int
    ) {

        var alpha = 0f
        var length = 0f
        var yOffset = 0f
        var index = 0

        fun update() {
            if (alpha < 255f) {
                alpha += fadeInSpeed
                length += expandSpeed
            }
            if ((fixedSpacingEnabled && (yOffset > -1 * index * fixedSpacing)) || !fixedSpacingEnabled) {
                yOffset -= moveSpeed
            }
        }

        fun draw() {
            pushMatrix()
            translate(origin)
            translate(0f, yOffset)
            stroke(color, alpha)
            strokeWeight(30f)
            val halfLineLength = constrain(length, 0f, lineLength / 2f)
            line(-halfLineLength, 0f, halfLineLength, 0f)
            popMatrix()
        }

        fun moveUp() {
            index++
        }
    }
}