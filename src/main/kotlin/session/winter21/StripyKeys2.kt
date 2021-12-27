package session.winter21

import BaseSketch
import processing.core.PApplet
import show.first.MidiController

fun main() {
    PApplet.main(StripyKeys2::class.java)
}

class StripyKeys2 : BaseSketch(
    Screen.LG_ULTRAWIDE,
    renderer = P2D,
    smoothLevel = 8
) {

    // config
    private val hidingEnabled = false
    private val numOfKeys = 49 // 49
    private val numOfLines = 160
    private val lineStrokeWeight = 3f
    private val maxAlpha = 255f
    private var fadeInSpeed = 25f
    private var fadeOutSpeed = 5f
    private val framesToAlign = 4f

    private val spacing by lazy { heightF / numOfLines }
    private val keyPadWidth by lazy { widthF / numOfKeys }
    private val keyPads by lazy {
        List(numOfKeys) {
            KeyPad(it)
        }
    }
    private val midiController by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        super.setup()
        midiController.on(
            MidiController.PAD_1..MidiController.PAD_16,
            { pitch, velocity ->
                keyPads[pitch - 12].show(velocity)
            }, { pitch ->
                keyPads[pitch - 12].hide()
            })
    }

    override fun draw() {
        background(grey3)
        keyPads.forEach {
            it.draw()
            it.update()
        }
    }

    private inner class KeyPad(val stripeIndex: Int) {

        private var state = if (hidingEnabled) StripeState.HIDDEN else StripeState.SHOWN
        private var alpha = if (hidingEnabled) 0f else 255f
        private var color = colors.random
        private val lines = List(numOfLines) { lineIndex ->

            val alignmentAngle = if (stripeIndex % 2 == 0) -QUARTER_PI else QUARTER_PI
            Line(stripeIndex, lineIndex, alignmentAngle)
        }

        fun update() {
            if (state == StripeState.FADE_IN && alpha < maxAlpha) alpha += fadeInSpeed
            if (state == StripeState.FADE_OUT) alpha -= fadeOutSpeed
            if (alpha < 0f) state = StripeState.HIDDEN
        }

        fun draw() {
            if (state == StripeState.HIDDEN) return
            lines.forEach {
                it.update()
                it.draw(color, alpha)
            }
        }

        fun show(velocity: Int) {
            fadeInSpeed = velocity.toFloat()
            if (hidingEnabled) state = StripeState.FADE_IN
            lines.forEach { it.align() }
        }

        fun hide() {
            if (hidingEnabled) state = StripeState.FADE_OUT
            lines.forEach { it.unalign() }
        }
    }

    private enum class StripeState {
        FADE_IN,
        SHOWN,
        FADE_OUT,
        HIDDEN
    }

    private inner class Line(val stripeIndex: Int, val lineIndex: Int, val alignmentAngle: Float) {

        private val originalAngle: Float = random(-PI, PI)
        private var angle = originalAngle
        private var time = 0f
        private var state = LineState.STATIC

        fun update() {
            time = when (state) {
                LineState.ALIGNING -> constrain(++time, 0f, framesToAlign)
                LineState.UNALIGNING -> constrain(time - 0.2f, 0f, framesToAlign)
                else -> time
            }
            angle = map(time, 0f, framesToAlign, originalAngle, alignmentAngle)
        }

        fun draw(color: Int, alpha: Float) {
            stroke(color, alpha)
            strokeWeight(lineStrokeWeight)
            pushMatrix()
            val y = lineIndex * spacing
            translate((stripeIndex + 0.5f) * keyPadWidth, y)
            rotate(angle)
            line(-0.8f * keyPadWidth, 0f, 0.8f * keyPadWidth, 0f)
            popMatrix()
        }

        fun align() {
            state = LineState.ALIGNING
        }

        fun unalign() {
            state = LineState.UNALIGNING
        }
    }

    private enum class LineState {
        ALIGNING,
        UNALIGNING,
        STATIC,
    }
}