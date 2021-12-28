package session.winter21

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import input.MidiController
import util.translate
import kotlin.random.Random

fun main() {
    PApplet.main(HelixKeys::class.java)
}

class HelixKeys : BaseSketch(
    screen = Screen.EPSON_PROJECTOR
) {

    private val keyRange = MidiController.PAD_24..MidiController.PAD_72

    private val numOfSlots = 49
    private val lineLength = 80f
    private val helixRadius = 10f
    private val moveSpeed = 5f
    private val rotationSpeed = 0.05f
    private val fadeSpeed = 0.6f
    private val lineColor = purple
    private val helices = mutableMapOf<Int, MutableList<Helix>>()
    private val newHelices = mutableMapOf<Int, MutableList<Helix>>()
    private val spacing by lazy { widthF / numOfSlots }
    private val controller by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        controller.on(keyRange,
            triggerAction = { pitch, velocity ->
                generateNewHelix(pitch - 35)
            },
            releaseAction = { pitch ->
                helices[pitch - 35]
                    ?.takeLast(2)
                    ?.forEach {
                        it.finish()
                    }
            })
    }

    override fun draw() {
        translate(halfWidthF, 0.9f * heightF)
        background(grey3)

        helices.values.forEach { helicesForPitch ->
            helicesForPitch.forEach {
                it.update()
                it.draw()
            }
        }

        if (keyPressed && key == 'c') {
            helices.values.forEach { helicesForPitch ->
                helicesForPitch.forEach {
                    it.finish()
                }
            }
        }
        addNewHelices()
    }

    override fun keyTyped() {
        if (keyPressed && key == ' ') {
            generateNewHelix(Random.nextInt(numOfSlots))
        }
    }

    private fun generateNewHelix(index: Int) {
        val origin = PVector(-halfWidthF + index * spacing, 0f)
        newHelices[index] = mutableListOf(
            Helix(origin, random(0f, TWO_PI), 1f, lineColor),
            Helix(origin, random(0f, TWO_PI), -1f, lineColor)
        )
    }

    private fun addNewHelices() {
        newHelices.forEach { (index, newList) ->
            if (helices[index] == null) {
                helices[index] = newList
            } else {
                helices[index]?.addAll(newList)
            }
        }
        newHelices.clear()
    }

    inner class Helix(
        private val origin: PVector,
        private val phase: Float,
        private val direction: Float,
        private val color: Int
    ) {

        private var time: Float = 0f
        private val tail = mutableListOf<Line>()
        private var finished = false

        fun update() {
            time += direction * rotationSpeed
            if (!finished) tail.addNewLine(time)
            tail.removeIf { it.hasFinished }
        }

        fun draw() {
            tail.forEach {
                it.update()
                it.draw()
            }
        }

        private fun MutableList<Line>.addNewLine(time: Float) {
            val offset = getOffset(time)
            val angle = time + phase
            add(Line(origin, offset, angle, color))
        }

        private fun getOffset(time: Float): PVector {
            val x = (helixRadius + sin(time)) * cos(time)
            val y = (helixRadius + sin(time)) * sin(time)
            return PVector(x, y)
        }

        fun finish() {
            finished = true
        }
    }

    private inner class Line(
        val origin: PVector,
        var offset: PVector = PVector(),
        val angle: Float,
        val color: Int
    ) {

        var hasFinished = false
        var alpha = 100f
        var length = 0f
        var yOffset = 0f

        fun update() {
            alpha -= fadeSpeed
            length += fadeSpeed
            yOffset -= moveSpeed
            if (alpha < 0f) hasFinished = true
        }

        fun draw() {
            pushMatrix()
            translate(origin)
            translate(0f, yOffset)
            translate(offset)
            rotate(angle)
            stroke(color, alpha)
            strokeWeight(10f)
            val halfLineLength = constrain(length, 0f, lineLength / 2f)
            line(-halfLineLength, 0f, halfLineLength, 0f)
            popMatrix()
        }
    }
}