package sketch.patterns

import BaseSketch
import Screen
import processing.core.PApplet
import input.MidiController
import util.roundTo

fun main() {
    PApplet.main(MaurerRosePatterns2::class.java)
}

// d 74
// d 87
// d 90
// d 93
// d 105
// d 115

// under 1 minute

/**
 * TODO Find an interesting n (e.g. 3.66), scan through all 360° and pick most interesting segments that will be slowed down
 * TODO map speed and save controls to Maschine (slow/medium/fast, forward/backward, stop/start, save start, save end)
 * TODO add symbol for speed mode (arrow left/right, color by speed)
 * TODO each line different color -> gradient
 */
class MaurerRosePatterns2 : BaseSketch(
    Screen.LG_ULTRAWIDE,
) {

    private val nProgress = ValueProgress(minValue = 2f, maxValue = 8f)
    private val dProgress = ValueProgress(minValue = 0f, maxValue = 360f)

    private val scale = 450f
    private val textSize = 24f
    private val textScale = 1.3f
    private val stopArea = screen.widthF / 8
    private var direction = 1f

    private val midiController by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        super.setup()
        MidiController.printDevices()
        midiController.on(36..37,
            triggerAction = { pitch, velocity ->
                println("Pitch: $pitch")
                when (pitch) {
                    36 -> direction = -1f
                    37 -> direction = 1f
                }
        })
        midiController.on(MidiController.CONTROL_1) { value ->
            val change = map(value.toFloat(), 0f, 127f, 0f, 1f)
            dProgress.setIncrement(change)
        }
    }


    override fun draw() {
        translate(width / 2f, height / 2f)
        background(grey3)
//        val (n, d) = mousePressControl()
        val d = dProgress.last
        val n = 2.42f
        drawRoseShape(n, d)
        drawValueText(n, d)
//        drawControlGuidelines()
        dProgress.progress()
    }

    private fun drawControlGuidelines() {
        line(-stopArea, -halfHeightF, -stopArea, -halfHeightF + 20f)
        line(stopArea, -halfHeightF, stopArea, -halfHeightF + 20f)
    }

    private fun drawRoseShape(n: Float, d: Float) {
        noFill()
        strokeWeight(4f)
        stroke(grey9)
        beginShape()
        var i = 0f
        while (i <= d) {
            val radius = scale * sin(radians(n * i * d))
            val x = radius * cos(radians(i * d))
            val y = radius * sin(radians(i * d))
            curveVertex(x, y)
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
        private var increment = 0.5f

        var last = 0f
        private var value = 0f

        fun progress() {
//            increment = incrementFromMouseControl()
            value += increment
            last = map(value % maxLoopTime, 0f, maxLoopTime - 1, minValue, maxValue).roundTo(numDecimalPoints)
        }

        private fun incrementFromKnob(change: Float): Float {
            return constrain(map(change, 0f, 1f, 0f, 4f), 0.0f, 4f)
        }

        private fun incrementFromMouseControl(): Float {
            val mouseControl = mouseX - halfWidthF
            if (mouseControl < -stopArea) {
                decrement()
            } else if (mouseControl > stopArea) {
                increment()
            }
            return map(abs(mouseControl), stopArea, halfWidthF, 0.1f, 10f)
        }

        private fun increment() {
            value += increment
        }

        private fun decrement() {
            value -= increment
            if (value < 0) value = maxLoopTime
        }

        fun setIncrement(change: Float) {
            increment = direction * incrementFromKnob(change)
        }
    }
}