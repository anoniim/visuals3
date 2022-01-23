package sketch.patterns

import BaseSketch
import Screen
import com.hamoid.VideoExport
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


// i = 27
// 68.1
// 70.0
// 70.55
// 71.0

/**
 * TODO map speed and save controls to Maschine (slow/medium/fast, forward/backward, stop/start, save start, save end)
 * TODO add symbol for speed mode (arrow left/right, color by speed)
 * TODO each line different color -> gradient
 */
class MaurerRosePatterns2 : BaseSketch(
//    Screen.LG_ULTRAWIDE,
    Screen(1280, 720),
) {

    private val dProgress = ValueProgress(minValue = 69.9f, maxValue = 71.0f)
    private var granularity = 0.2109375f // 1f

    private val scale = 350f
    private val textSize = 24f
    private val textScale = 1.3f
    private val stopArea = screen.widthF / 8
    private var direction = 1f
    private var recordingOn: Boolean = false // record

    private val midiController by lazy { MidiController(this, 1, 2) }
    private val videoExport by lazy { VideoExport(this) }

    override fun setup() {
        super.setup()
        if (recordingOn) videoExport.startMovie()
        MidiController.printDevices()
        midiController.on(36..37,
            triggerAction = { pitch, velocity ->
                when (pitch) {
                    36 -> direction = -1f
                    37 -> direction = 1f
                }
            })
        midiController.on(38, { _, _ ->
            recordingOn = !recordingOn
        }, null)
        midiController.on(MidiController.CONTROL_1) { value ->
            val change = map(value.toFloat(), 0f, 127f, 0f, 1f)
            dProgress.setIncrement(change)
        }
        midiController.on(MidiController.CONTROL_18) { value ->
            val change = map(value.toFloat(), 0f, 127f, 0f, 0.001f)
            dProgress.setIncrement(change)
        }
        midiController.on(MidiController.CONTROL_19) { value ->
            val change = map(value.toFloat(), 0f, 127f, 0f, 0.01f)
            dProgress.setIncrement(change)
        }
        midiController.on(MidiController.CONTROL_16) { value ->
            val change = map(value.toFloat(), 0f, 127f, 0f, 1f)
            dProgress.setIncrement(change)
        }
        midiController.on(MidiController.CONTROL_17) { value ->
            val change = map(value.toFloat(), 0f, 127f, 0f, 1f)
            granularity = change
        }
    }


    override fun draw() {
        translate(width / 2f, height / 2f)
        background(grey3)
//        val (n, d) = mousePressControl()
        val d = dProgress.last
        val n = 12.42f
        drawRoseShape(n, d)
        dProgress.progress()

        manageRecording()
        drawValueText(n, d)
//        drawControlGuidelines()
    }

    private fun manageRecording() {
        videoExport.saveFrame()
        strokeWeight(20f)
        stroke(red)
        point(-halfWidthF + 20f, -halfHeightF + 20f)
    }

    private fun drawRoseShape(n: Float, d: Float) {
        noFill()
        strokeWeight(1f)
        stroke(white)
        beginShape()
        var i = 0f
        while (i <= d) {
            addVertex(n, i, d)
            i += granularity
        }
//        addVertex(n, 0f, d)
//        addVertex(n, granularity, d)
//        addVertex(n, 2 * granularity, d)
        endShape()
    }

    private fun addVertex(n: Float, i: Float, d: Float) {
        val radius = scale * sin(radians(n * i * d))
        val x = radius * cos(radians(i * d))
        val y = radius * sin(radians(i * d))
        curveVertex(x, y)
    }

    private fun drawValueText(n: Float, d: Float) {
        fill(grey9)
        textSize(textSize)
        text("n: $n", -widthF / 2 + textSize * textScale, -heightF / 2 + textSize * textScale)
        text("d: $d", -widthF / 2 + textSize * textScale, -heightF / 2 + 2 * (textSize * textScale))
    }

    inner class ValueProgress(val minValue: Float, val maxValue: Float) {

        private val maxLoopTime: Float = 10000f // 1000f
        private val numDecimalPoints = 5 // 3
        private var increment = 3f

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
            increment = direction * change
        }
    }
}