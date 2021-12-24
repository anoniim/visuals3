package session.winter21

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import show.first.MidiController
import util.translate

fun main() {
    PApplet.main(SymmetricLineKeys::class.java)
}

class SymmetricLineKeys : BaseSketch() {

    // config
    private val moveSpeed = 3f // 2-5 low without fixed spacing, higher otherwise
    private val pitchLengthFactor = 30f
    private val fixedSpacingEnabled = true
    private val fixedSpacing = 40f
    private val fadeInSpeed = 1f
    private val expandSpeed = 4f
    private val lineColor = purple

    private val lines = mutableListOf<Line>()
    private val controller by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        controller.on(
            MidiController.PAD_1..MidiController.PAD_16,
            triggerAction = { pitch, velocity ->
                generateNewLine(pitch * pitchLengthFactor)
            })
    }

    override fun draw() {
        translate(halfWidthF, 0.9f * heightF)
        background(grey3)

        updateLines()
    }

    @Synchronized
    private fun updateLines() {
        lines.forEach {
            it.update()
            it.draw()
        }
    }

    override fun keyTyped() {
        if (keyPressed && key == ' ') {
            generateNewLine(random(800f))
        }
    }

    @Synchronized
    private fun generateNewLine(length: Float) {
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