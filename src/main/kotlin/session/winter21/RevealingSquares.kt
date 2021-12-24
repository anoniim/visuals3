package session.winter21

import BaseSketch
import processing.core.PApplet
import processing.core.PImage
import processing.core.PVector
import show.first.MidiController
import util.plus
import util.translateToCenter

fun main() {
    PApplet.main(RevealingSquares::class.java)
}

class RevealingSquares : BaseSketch() {

    // config
    private val minSize = 20f
    private val revealBackgroundImage = false
    private val maxSize = 200f
    private val minAlpha = 100f
    private val randomnessSpeed = 0.005f
    private val radius = 600f

    private var time = 0f
    private var pointer = PVector()
    private val squares = mutableListOf<Square>()
    private val background by lazy { loadImage("input/winter21/sunset_scaled.png") } // surfer, cliff
    private val controller by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        controller.on(
            MidiController.PAD_1..MidiController.PAD_16,
            triggerAction = { pitch, velocity ->
                generateNewSquare(2f * velocity)
            })

        shapeMode(CENTER)
        // config
//        imageMode(CENTER) // enable for the cutout positions to be a bit off
    }

    override fun draw() {
        background(grey3)
        translateToCenter()
//        image(background, 0f, 0f)

        updateSquares()
        updatePointer()
    }

    @Synchronized
    private fun updateSquares() {
        squares.forEach {
            it.update()
            it.draw()
        }
    }

    override fun keyTyped() {
        if (key == ' ') {
            generateNewSquare(random(minSize, maxSize))
        }
    }

    private fun generateNewSquare(size: Float) {
        if (revealBackgroundImage) {
            addCutoutImage(size)
        } else {
            addColorSquare(size)
        }
    }

    @Synchronized
    private fun addColorSquare(size: Float) {
        squares.add(Square(pointer.copy(), size, color = colors.random))
    }

    @Synchronized
    private fun addCutoutImage(size: Float) {
        val cutout = getCutout(size)
        squares.add(Square(pointer.copy(), size, image = cutout))
    }

    private fun getCutout(size: Float): PImage? {
        val cutoutCenter = pointer.copy() + PVector(halfWidthF, halfHeightF)
        return background.get(cutoutCenter.x.toInt(), cutoutCenter.y.toInt(), size.toInt(), size.toInt())
    }

    private fun updatePointer() {
        time += randomnessSpeed
        val radius = sin(6 * time) * radius
        pointer = PVector.fromAngle(time).mult(radius)
    }

    private inner class Square(
        val position: PVector,
        val size: Float,
        val color: Int? = null,
        val image: PImage? = null
    ) {

        var alpha = 355f

        fun update() {
            // TODO optimization: fade out completely and remove from list after some time (20s)
            if (alpha > minAlpha) alpha -= 1f
        }

        fun draw() {
            if (color != null) {
                fill(color, alpha)
                stroke(white)
                strokeWeight(2f)
                square(position.x, position.y, size)
            } else if (image != null) {
                tint(255, alpha)
                image(image, position.x, position.y)
            }
        }
    }
}