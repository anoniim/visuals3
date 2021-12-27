package sketch.pixels

import BaseSketch
import processing.core.PApplet

fun main() {
    PApplet.main(ImageFilters::class.java)
}

class ImageFilters : BaseSketch(Screen.LG_ULTRAWIDE) {

    private var parameter = 2f
    private val maxParamValue = 255f
    private val speed = 0.01f

    private val image by lazy {
        val original = loadImage("input/winter21/joel1.png")
        original.resize(width, 0)
        original.get(0, 0, width, height)
    }

    override fun setup() {
        super.setup()
        background(grey11)
    }

    override fun draw() {
//        loadPixels()
//        val imagePixels = image.pixels
//        for (i in imagePixels.indices) {
//            val pixel = imagePixels[i]
//            if (brightness(pixel) <= colorThreshold) {
//                pixels[i] = pixel
//            }
//        }
//        updatePixels()
        val modifiedImage = image.copy()
        modifiedImage.filter(POSTERIZE, parameter)
        image(modifiedImage, 0f, 0f)

        showParameter()
        parameter += speed
        if (parameter > maxParamValue) noLoop()
    }

    private fun showParameter() {
        stroke(white)
        textSize(40f)
        text(parameter, 10f, 50f)
    }
}