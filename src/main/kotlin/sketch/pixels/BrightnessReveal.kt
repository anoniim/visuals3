package sketch.pixels

import BaseSketch
import processing.core.PApplet

fun main() {
    PApplet.main(BrightnessReveal::class.java)
}

class BrightnessReveal : BaseSketch(Screen.LG_ULTRAWIDE) {

    private var colorThreshold = 0f
    private val speed = 0.5f

    private val image by lazy {
        val original = loadImage("input/winter21/park.png") // cliff_full
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
            if (brightness(pixel) <= colorThreshold) {
                pixels[i] = pixel
            }
        }
        updatePixels()

//        showBrightnessThreshold()
        colorThreshold += speed
        if (colorThreshold > 255) noLoop()
    }

    private fun showBrightnessThreshold() {
        stroke(white)
        textSize(40f)
        text(colorThreshold, 10f, 50f)
    }
}