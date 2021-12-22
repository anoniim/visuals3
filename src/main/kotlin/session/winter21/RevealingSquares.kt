package session.winter21

import BaseSketch
import processing.core.PApplet
import processing.core.PImage
import processing.core.PVector
import util.plus
import util.translateToCenter

fun main() {
    PApplet.main(RevealingSquares::class.java)
}

class RevealingSquares : BaseSketch() {

    // config
    private val minAlpha = 100f
    private val randomnessSpeed = 0.005f
    private val radius = 600f

    private var time = 0f
    private var pointer = PVector()
    private val squares = mutableListOf<Square>()
    private val background by lazy { loadImage("input/winter21/surfer_scaled.png") }

    override fun setup() {
        shapeMode(CENTER)
        // config
//        imageMode(CENTER) // enable for the cutout positions to be a bit off
    }

    override fun draw() {
        background(grey3)
        translateToCenter()
//        image(background, 0f, 0f)

        squares.forEach {
            it.update()
            it.draw()
        }
        updatePointer()
    }

    override fun keyTyped() {
        if (key == ' ') {
            generateNewSquare()
        }
    }

    private fun generateNewSquare() {
        val size = random(20f, 200f)
        // config
        addColorSquare(size)
//        addCutoutImage(size)
    }

    private fun addColorSquare(size: Float) {
        squares.add(Square(pointer.copy(), size, color = colors.random))
    }

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