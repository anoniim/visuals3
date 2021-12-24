package session.winter21

import BaseSketch
import Screen
import processing.core.PApplet
import processing.core.PImage
import show.first.MidiController

fun main() {
    PApplet.main(RevealingStripes::class.java)
}

class RevealingStripes : BaseSketch(Screen(1430, 950)) {

    // config
    private val minAlpha = 100f
    private val numOfSegments = 44
    private val middleStripeDelayFrames = 1200
    private val revealBackgroundImage = true

    private val segmentSize by lazy { ceil(widthF / numOfSegments) }
    private val stripes = mutableListOf<Stripe>()
    private val background by lazy { loadImage("input/winter21/surfer_scaled.png") } // sunset, cliff
    private val controller by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        controller.on(
            MidiController.PAD_1..MidiController.PAD_16,
            triggerAction = { pitch, velocity ->
                generateNewStripe()
            })
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
        stripes.forEach {
            it.update()
            it.draw()
        }
    }

    private fun generateNewStripe() {
        val segment = random(numOfSegments).toInt()
        if(revealBackgroundImage) {
            addCutoutImage(segment)
        } else {
//        addColorSquare(segment)
        }
    }

    @Synchronized
    private fun addColorSquare(segment: Int) {
        stripes.add(segment, Stripe(segment, color = colors.random))
    }

    @Synchronized
    private fun addCutoutImage(segment: Int) {
        if (segment+1 == numOfSegments / 2 && frameCount < middleStripeDelayFrames) {
            // Do not show middle stripe in first [middleStripeDelay] frames
            println("Should have shown but didn't")
            return
        } else {
            val cutout = getCutout(segment)
            stripes.add(Stripe(segment, image = cutout))
        }
    }

    private fun getCutout(segment: Int): PImage? {
        return background.get((segment * segmentSize), 0, segmentSize, height)
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
        }
    }
}