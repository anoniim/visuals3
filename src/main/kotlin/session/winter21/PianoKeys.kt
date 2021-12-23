package session.winter21

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import show.first.MidiController
import util.line

fun main() {
    PApplet.main(PianoKeys::class.java)
}

/**
 * There is a problem with Processing's [keyPressed] which is called multiple times when the key press is too long.
 * That will hopefully go away when MIDI controller is used.
 *
 * TODO: make the lines to spiral to the center of the screen
 */
class PianoKeys : BaseSketch() {

    // config
    private val movementSpeed = 3f
    private val numOfPads = 16
    private val padSize = 40f
    private val bottomMargin = 50f

    private val margin by lazy { padSize }
    private val gridWidth by lazy { numOfPads * padSize + (numOfPads - 1) * margin }
    private val horizontalOffset by lazy { (widthF - gridWidth) / 2f }
    private val controller by lazy { MidiController(this, 1, 2) }

    private val pads = mutableMapOf<Int, MutableList<Pad>>()
    private val padPositions by lazy {
        List(numOfPads) { index ->
            val x = index * (padSize + margin) + horizontalOffset + (padSize + margin) / 2f
            PVector(x, heightF - bottomMargin)
        }
    }

    override fun setup() {
        controller.on(MidiController.PAD_1..MidiController.PAD_16,
            triggerAction = { pitch, velocity ->
                addPad(pitch - 12)
            },
            releaseAction = { pitch ->
                releasePad(pitch - 12)
            })
    }

    override fun draw() {
        background(grey3)

        pads.values.flatten().forEach {
            it.update()
            it.draw()
        }
    }

    override fun keyPressed() {
        println("keyPressed: $keyCode")
        if (keyCode in 97..106) {
            val pitch = keyCode - 97
            addPad(pitch)
        }
    }

    private fun addPad(pitch: Int) {
        val newPad = Pad(padPositions[pitch].copy(), yellow).apply { setPressed() }
        val existingPads = (pads[pitch] ?: mutableListOf()).apply { add(newPad) }
        pads[pitch] = existingPads
    }

    override fun keyReleased() {
        println("keyReleased: $keyCode")
        if (keyCode in 97..106) {
            // FIXME Throws IllegalArgumentException: Collection contains more than one matching element
            // if pressed for too long (limitation of Processing's keyPress()
            val pitch = keyCode - 97
            releasePad(pitch)
        }
    }

    private fun releasePad(pitch: Int) {
        pads[pitch]?.single { it.isPressed }?.setReleased()
    }

    private inner class Pad(
        var origin: PVector,
        val color: Int
    ) {

        var isPressed = false
        private var length = 0f
        private var timePressed = 0f
        private val end
            get() = PVector(origin.x, origin.y + length)

        fun update() {
            if(isPressed) {
                timePressed += movementSpeed
                length += movementSpeed
            }
            origin.add(0f, -movementSpeed)
        }

        fun draw() {
//            if (!isPressed) return
            stroke(color)
            strokeWeight(padSize)
            line(origin, end)
        }

        fun setPressed() {
            isPressed = true
        }

        fun setReleased() {
            isPressed = false
            timePressed = 0f
//            length = 0f
        }
    }
}