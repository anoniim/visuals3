package session.winter21

import BaseSketch
import input.KompleteKontrol
import input.MidiController
import processing.core.PApplet
import processing.core.PVector
import util.circle

fun main() {
    PApplet.main(CirclePads::class.java)
}

class CirclePads : BaseSketch() {

    private val numOfRows = 4
    private val numOfCols = 8
    private val alphaSpeed = 0.1f

    private val numOfCircles = numOfCols * numOfRows
    private val circleSize by lazy { heightF / numOfRows * 0.8f }
    private val gridWidth by lazy { numOfCols * circleSize + (numOfCols - 1) * margin }
    private val gridHeight by lazy { numOfRows * circleSize + (numOfRows - 1) * margin }
    private val horizontalOffset by lazy { (widthF - gridWidth) / 2f }
    private val verticalOffset by lazy { (heightF - gridHeight) / 2f }
    private val margin by lazy { circleSize * 0.1f }
    private val controller by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        controller.on(KompleteKontrol.defaultKeyRange,
            triggerAction = { pitch, _ ->
                pads[pitch-KompleteKontrol.pitchOffset].setPressed()
            },
            releaseAction = { pitch ->
                pads[pitch-KompleteKontrol.pitchOffset].setReleased()
            })
        MidiController.printDevices()
    }

    private val pads by lazy {
        List(numOfCircles) {
            val row = it / numOfCols
            val column = it % numOfCols
            val x = column * (circleSize + margin) + horizontalOffset + circleSize / 2f
            val y = row * (circleSize + margin) + verticalOffset + circleSize / 2f
            Circle(PVector(x, y), yellow)
        }
    }

    override fun draw() {
        background(grey3)

        pads.forEach {
            it.update()
            it.draw()
        }
    }

    override fun keyPressed() {
        println("keyPressed: $keyCode")
        if (keyCode in 97..106) {
            pads[keyCode - 97].setPressed()
        }
    }

    override fun keyReleased() {
        println("keyReleased: $keyCode")
        if (keyCode in 97..106) {
            pads[keyCode - 97].setReleased()
        }
    }

    private inner class Circle(
        val origin: PVector,
        val color: Int
    ) {

        private var isPressed = false
        private var currentAlpha = 0f
        private var timePressed = 0f

        fun update() {
            if (isPressed) {
                timePressed += alphaSpeed
                currentAlpha = abs(sin(timePressed)) * 255f
            }
        }

        fun draw() {
            noStroke()
            fill(color, currentAlpha)
            circle(origin, circleSize)
        }

        fun setPressed() {
            isPressed = true
        }

        fun setReleased() {
            isPressed = false
            timePressed = 0f
            currentAlpha = 0f
        }
    }
}