package sketch.spirals

import BaseSketch
import Screen
import com.hamoid.VideoExport
import processing.core.PShape
import util.getRolling
import util.translateToCenter

class PolygonStripeTunnel : BaseSketch(Screen()) {

    // config
    private val shape = Shape.OCTAGON
    private val numOfStripes = 15
    private val frameWidth = -1f // -1f - 50f
    private val fadeRate = 0.007f // 0.005 - 0.1
    private val flashingSpeed = 20 // 1 - 30 (lower is faster)
    private val colorPalette = listOf(colors.blues, colors.dirtyBeach, colors.sunrise, colors.pastel)
    private val bgColor = grey3

    private var stripeColors = colorPalette[0]
    private var colorPaletteIndex = 0
    private var flashingStripeIndex = 0
    private val stripeWidth by lazy {
        val maxRadius = halfWidthF * 1.7f
        (maxRadius / numOfStripes) + 15f
    }
    private val stripes by lazy {
        List(numOfStripes) {
            Stripe(shape, (it + 1) * stripeWidth, bgColor)
        }
    }

    override fun draw() {
        background(bgColor)
        translateToCenter()

        drawStripes()
        flashStripes()
        changeColorPalette()
//        videoExport.saveFrame() // record
    }

    private fun drawStripes() {
        stripes.reversed().forEach {
            it.update()
            it.draw()
        }
    }

    private fun flashStripes() {
        if (frameCount % flashingSpeed == 0) {
            val stripeIndex = flashingStripeIndex % stripes.size
            val color = if ((flashingStripeIndex / stripes.size) % 2 == 0) {
                stripeColors.getRolling(stripeIndex)
            } else bgColor
            stripes[stripeIndex].flash(color)
            flashingStripeIndex++
        }
    }

    private fun changeColorPalette() {
        if (frameCount % (flashingSpeed * stripes.size * 2) == 0) {
            colorPaletteIndex++
            stripeColors = colorPalette.getRolling(colorPaletteIndex)
        }
    }

    private inner class Stripe(
        shape: Shape,
        val radius: Float,
        var color: Int,
    ) {

        val shape: PShape = createPolygonShape(shape)
        var colorProgress = 0f
        var newColor = color
        var currentColor = color

        fun flash(color: Int) {
            newColor = color
            colorProgress = 0f
        }

        fun update() {
            if (colorProgress <= 1f) {
                colorProgress += fadeRate
            } else {
                color = newColor
                colorProgress = 0f
            }
            currentColor = lerpColor(color, newColor, colorProgress)
        }

        fun draw() {
            shape.setFill(currentColor)
            shape(shape)
        }

        private fun createPolygonShape(shape: Shape): PShape {
            val s = createShape()
            with(s) {
                beginShape()
                noStroke()
                var angle = 0f
                while (angle <= TWO_PI) {
                    val x = radius * cos(angle)
                    val y = radius * sin(angle)
                    vertex(x, y)
                    angle += shape.innerAngle
                }
                beginContour()
                val contourRadius = radius - (stripeWidth - frameWidth)
                while (angle > 0) {
                    angle -= shape.innerAngle
                    val x = contourRadius * cos(angle)
                    val y = contourRadius * sin(angle)
                    vertex(x, y)
                }
                endContour()
                endShape(CLOSE)
            }
            return s
        }
    }

    @Suppress("unused")
    private enum class Shape(val innerAngle: Float) {
        TRIANGLE(2 * PI / 3f),
        SQUARE(PI / 2f),
        PENTAGON(TWO_PI / 5f),
        HEXAGON(PI / 3f),
        OCTAGON(PI / 4f),
    }

    @Suppress("unused")
    private val videoExport by lazy {
        VideoExport(this).apply {
            startMovie() // record
        }
    }
}
