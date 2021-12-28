package session.winter21

import BaseSketch
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PImage
import input.MidiController

fun main() {
    PApplet.main(StripyKeys::class.java)
}

class StripyKeys : BaseSketch(
    Screen.EPSON_PROJECTOR,
    renderer = P2D,
    smoothLevel = 8
) {

    private val keyRange = MidiController.PAD_24..MidiController.PAD_72

    private val numOfKeys = 49 // 49
    private val numOfLines = 320
    private val lineStrokeWeight = 6f
    private val maxAlpha = 255f
    private var fadeInSpeed = 25f
    private var fadeOutSpeed = 5f

    private val keyPadWidth by lazy { width / numOfKeys }
    private val bottomLength by lazy { tan(QUARTER_PI) * heightF }
    private val keyPads by lazy {
        List(numOfKeys) {
            val texture = generateTexture()
            val padGraphics = generatePadGraphics(it, texture)
            KeyPad(padGraphics)
        }
    }
    private val midiController by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        super.setup()
        midiController.on(
            keyRange,
            { pitch, velocity ->
                keyPads[pitch - 35].show(velocity)
            }, { pitch ->
                keyPads[pitch - 35].hide()
            })
    }

    override fun draw() {
        background(grey3)
        keyPads.forEach {
            it.draw()
            it.update()
        }
    }

    private fun generateTexture(): PGraphics {
        val g = createGraphics(2 * width, height, P2D)
        with(g) {
            beginDraw()
            stroke(colors.random)
            strokeWeight(lineStrokeWeight)
            pushMatrix()
            translate(widthF, halfHeightF)
            val spacing = 2 * widthF / numOfLines
            for (i in -numOfLines..numOfLines) {
                val y = i * spacing
                line(-widthF, y, widthF, y)
            }
            popMatrix()
            endDraw()
        }
        return g
    }

    private fun generatePadGraphics(index: Int, texture: PGraphics): PGraphics {
        val padGraphics = createGraphics(width, height, P2D)
        with(padGraphics) {
            beginDraw()
            clear()
            noStroke()
            textureMode(NORMAL)
            beginShape()
            texture(texture)
            val x1 = index * keyPadWidth.toFloat()
            val x2 = (index + 1) * keyPadWidth.toFloat()
            vertex(x1, 0f, 0f, 0f)
            vertex(x2, 0f, 1f, 0f)
            // config: swap the next 2 vertices for an X effect
            vertex(x2 + bottomLength, heightF, 1f, 1f)
            vertex(x1 + bottomLength, heightF, 0f, 1f)
            endShape(CLOSE)
            endDraw()
        }
        return padGraphics
    }

    private inner class KeyPad(
        val padGraphics: PImage
    ) {

        private var state = State.HIDDEN
        private var alpha = 0f

        fun update() {
            if (state == State.FADE_IN && alpha < maxAlpha) alpha += fadeInSpeed
            if (state == State.FADE_OUT) alpha -= fadeOutSpeed
            if (alpha < 0f) state = State.HIDDEN
        }

        fun draw() {
            if (state == State.HIDDEN) return
            tint(255, alpha)
            image(padGraphics, -bottomLength / 2f, 0f)
        }

        fun show(velocity: Int) {
            fadeInSpeed = velocity.toFloat()
            state = State.FADE_IN
        }

        fun hide() {
            state = State.FADE_OUT
        }
    }

    private enum class State {
        FADE_IN,
        SHOWN,
        FADE_OUT,
        HIDDEN
    }
}