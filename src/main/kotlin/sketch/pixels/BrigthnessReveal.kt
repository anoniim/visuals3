package sketch.pixels

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import java.awt.Color.green
import java.awt.Color.red
import kotlin.random.Random

fun main() {
    PApplet.main(Pointillism::class.java)
}

class Pointillism : BaseSketch(Screen.LG_ULTRAWIDE) {

    private var brightnessThreshold = 255f
    private val image by lazy {
        val original = loadImage("input/winter21/cliff_full.png")
        original.resize(width, 0)
        original.get(0, 0, width, height)
    }

    override fun setup() {
        super.setup()
        background(grey11)
    }

    override fun draw() {
        loadPixels()
        val imagePixels = image.pixels
        for (i in imagePixels.indices) {
            val pixel = imagePixels[i]
            val brightness = brightness(pixel)
            if (brightness > brightnessThreshold) {
                pixels[i] = pixel
            }
        }
        updatePixels()

        showBrightnessThreshold()
        brightnessThreshold -= 1f
        if (brightnessThreshold < 0f) noLoop()
    }

    private fun showBrightnessThreshold() {
        stroke(white)
        textSize(40f)
        text(brightnessThreshold, 10f, 50f)
    }
}