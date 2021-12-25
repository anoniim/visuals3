package session.winter21

import BaseSketch
import Screen
import processing.core.PApplet
import processing.core.PImage
import show.first.MidiController

fun main() {
    PApplet.main(RevealingStripes::class.java)
}

class RevealingStripes : BaseSketch(
//    screen = Screen.EPSON_PROJECTOR
) {

    // config
    private val minAlpha = 100f
    private val numOfSegments = 44
    private val middleSegment = 21
    private val revealBackgroundImage = true
    private val showMiddleSegmentLast = true
    private val showStripeNumbers = false

    private val segmentSize by lazy { ceil(widthF / numOfSegments) }
    private val stripes = mutableMapOf<Int, Stripe>()
    private val background by lazy { loadImage("input/winter21/surfer_scaled.png") } // sunset, cliff
    private val controller by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        super.setup()
        controller.on(
            MidiController.PAD_1..MidiController.PAD_16,
            triggerAction = { pitch, velocity ->
                generateNewStripe()
            })
        background.resize(width, 0)
    }

    override fun draw() {
        background(grey3)

        updateStripes()
    }

    override fun keyTyped() {
        if (key == ' ') {
            generateNewStripe()
        }
    }

    @Synchronized
    private fun updateStripes() {
        stripes.values.forEach {
            it.update()
            it.draw()
        }
    }

    private fun generateNewStripe() {
        val segment = random(numOfSegments).toInt()
        if(revealBackgroundImage) {
            addCutoutImage(segment)
        } else {
//        addColorStripe(segment)
        }
    }

    @Synchronized
    private fun addColorStripe(segment: Int) {
        stripes[segment] = Stripe(segment, color = colors.random)
    }

    @Synchronized
    private fun addCutoutImage(segment: Int) {
        println("showing $segment")
        if (showMiddleSegmentLast && segment == middleSegment && !haveAllOthersBeenShown()) {
            println("Should have shown but didn't")
            return
        } else {
            val cutout = getCutout(segment)
            stripes[segment] = Stripe(segment, image = cutout)
        }
    }

    private fun haveAllOthersBeenShown(): Boolean {
        return stripes.keys.size >= numOfSegments - 1
    }

    private fun getCutout(segment: Int): PImage? {
        return background.get((segment * segmentSize), background.height - height, segmentSize, height)
    }

    private inner class Stripe(
        val index: Int,
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
                rect(index * segmentSize.toFloat(), 0f, segmentSize.toFloat(), heightF)
            } else if (image != null) {
                tint(255, alpha)
                image(image, index * segmentSize.toFloat(), 0f)
            }
            showStripeNumbers()
        }

        private fun showStripeNumbers() {
            if(showStripeNumbers)
             text("$index", index * segmentSize.toFloat() + segmentSize / 2f, heightF - 20f)
        }
    }
}