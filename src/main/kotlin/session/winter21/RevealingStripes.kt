package session.winter21

import BaseSketch
import Screen
import processing.core.PApplet
import processing.core.PImage
import input.MidiController
import kotlin.random.Random


fun main() {
    PApplet.main(RevealingStripes::class.java)
}

class RevealingStripes : BaseSketch(
    screen = Screen.LG_ULTRAWIDE,
) {

    private val keyRange = MidiController.PAD_24..MidiController.PAD_72

    // config
    private val segmentSize = 60
    private val subsegmentWidth = 20 // 2-10
    private val maxSubsegmentOffset = 100 // 10-300
    private val revealProgressSpeed = 10f // 1-10
    private val middleStrips = listOf(16, 17)
    private val showStripeNumbers = false
    private val vignetteSpeed = 4
    private val maxAlpha = 555f
    private val maxVignetteFrames = vignetteSpeed * maxAlpha

    private val stripes = mutableMapOf<Int, Stripe>()
    private val numOfSegments by lazy { ceil(widthF / segmentSize) }
    private val segmentOrder by lazy { generateSegmentOrder() }
    private var segmentPointer = 0
    private val originalImage by lazy { loadImage("input/winter21/surfer_scaled.png") }
    private val background by lazy { originalImage.copy() }
    private val vignetteIndexMiddle by lazy { width * height / 2f }
    private val controller by lazy { MidiController(this, 1, 2) }
    private val isRevealed
        get() = segmentPointer >= segmentOrder.size

    override fun setup() {
        super.setup()
        MidiController.printDevices()
        controller.on(
            keyRange,
            triggerAction = { pitch, velocity ->
                generateNewStripe()
            })
        originalImage.resize(width, 0)
        background.filter(BLUR, 50f)
    }

    override fun keyTyped() {
        if (key == ' ') {
            generateNewStripe()
        }
    }

    override fun draw() {
        drawBlurredBackground()
        applyVignette()
        updateStripes()
    }

    private fun drawBlurredBackground() {
        if (isRevealed) return
        image(background, 0f, 0f)
    }

    private fun applyVignette() {
        if (frameCount > maxVignetteFrames) return
        val alphaOffset = vignetteSpeed * frameCountF
        val vignette = createImage(width, height, ARGB)
        vignette.loadPixels()
        for (i in vignette.pixels.indices) {
            val alpha = map(
                abs(vignetteIndexMiddle - i),
                0f,
                vignetteIndexMiddle,
                255f - alphaOffset,
                maxAlpha - alphaOffset
            )
            vignette.pixels[i] = color(255f, alpha)
        }
        vignette.updatePixels()
        image(vignette, 0f, 0f)
    }

    @Synchronized
    private fun updateStripes() {
        stripes.values.forEach {
            it.update()
            it.draw()
        }
    }

    private fun generateNewStripe() {
        if (isRevealed) return
        val segment = segmentOrder[segmentPointer++]
        addCutoutImage(segment)
    }

    @Synchronized
    private fun addCutoutImage(segment: Int) {
        println("showing $segment")
        val cutout = getCutout(segment)
        stripes[segment] = Stripe(segment, image = cutout)
    }

    private fun getCutout(segment: Int): PImage {
        return originalImage.get((segment * segmentSize), 0, segmentSize, height)
    }

    private fun generateSegmentOrder(): List<Int> {
        return MutableList(numOfSegments) { it }
            .filter { !middleStrips.contains(it) }
            .shuffled()
            .toMutableList()
            .plus(middleStrips)
    }

    private inner class Stripe(
        val stripeIndex: Int,
        val image: PImage
    ) {

        private var isRevealed = false
        private var progress = 0f
        private val offsets = List(segmentSize / subsegmentWidth) {
            // config
//            (noise(stripeIndex + it / 2f) * maxSubsegmentOffset).toInt()
            Random.nextInt(maxSubsegmentOffset)
        }

        fun update() {
            if (!isRevealed && progress < 1f) progress += revealProgressSpeed / 1000f else isRevealed = true
        }

        fun draw() {
            if (isRevealed) {
                image(image, stripeIndex * segmentSize.toFloat(), 0f)
            } else revealImage(image)

            showStripeNumbers()
        }

        private fun revealImage(image: PImage) {
            val yProgress = map(progress, 0f, 1f, 0f, heightF).toInt()
            offsets.forEachIndexed { index, offset ->
                val y = constrain(height - yProgress - offset, 0, height)
                val progressImage =
                    image.get(index * subsegmentWidth, y, subsegmentWidth, yProgress + offset)
                image(
                    progressImage,
                    stripeIndex * segmentSize.toFloat() + index * subsegmentWidth,
                    y.toFloat()
                )
            }
        }

        private fun showStripeNumbers() {
            if (showStripeNumbers)
                text("$stripeIndex", stripeIndex * segmentSize.toFloat() + segmentSize / 2f, heightF - 20f)
        }
    }
}