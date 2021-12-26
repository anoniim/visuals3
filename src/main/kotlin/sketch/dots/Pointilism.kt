package sketch.dots

import BaseSketch
import Screen
import processing.core.PApplet
import kotlin.random.Random

fun main() {
    PApplet.main(Pointillism::class.java)
}

class Pointillism: BaseSketch(Screen.LG_ULTRAWIDE) {

    private val resolution = 1f
    private val randomSize = 10f
    private val speed = 500

    private val image by lazy { loadImage("input/winter21/joel2.png") } // joel1, club

    override fun setup() {
        super.setup()
        image.resize(width, 0)
        background(image.pixels[0])
    }

    override fun draw() {
        image.loadPixels()
        repeat(speed) {
            val x = Random.nextInt(width)
            val y = Random.nextInt(height)
            val pixel = image.get(x, y)
            drawPoint(pixel, x, y)
        }
    }

    private fun drawPoint(color: Int, x: Int, y: Int) {
        noStroke()
        fill(color, 200f)
        val size = random(randomSize) + resolution * 2
        circle(x.toFloat(), y.toFloat(), size)
    }
}