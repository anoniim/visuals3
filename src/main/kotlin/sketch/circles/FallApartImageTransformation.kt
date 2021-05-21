package sketch.circles

import BaseSketch
import Screen
import com.hamoid.VideoExport
import processing.core.PImage
import processing.core.PVector
import util.circle

class FallApartImageTransformation : BaseSketch(Screen(620, 860)) {

    private val particleSize = 4f
    private val randomAreaRadius = 20f
    private val animationStep = 0.01f
    private var revealSpeed = 1
    private val revealAcceleration = 50

    private val macaImg by lazy { loadImage("data/input/maca.jpg") }
    private val macaOldImg by lazy { loadImage("data/input/maca_old.jpg") }
    private val images by lazy { listOf(macaImg, macaOldImg) }
    private val xOffset by lazy { widthF / 2f - macaImg.width / 2f }
    private val yOffset by lazy { heightF / 2f - macaImg.height / 2f }
    private val particles = mutableListOf<Particle>()
    private val videoExport: VideoExport by lazy {
        VideoExport(this).apply {
            setFrameRate(60f)
        }
    }

    private var imagePointer = 0
    private var state: State = Stopped
    private var animationProgress = 0f

    override fun setup() {
        macaImg.loadPixels()
//        videoExport.startMovie() // record
    }

    override fun draw() {
        background(grey3)
        translate(xOffset, yOffset)
//        image(macaImg, 0f, 0f)
//        image(macaOldImg, 570f, 0f)

        if (state is Revealing) revealParticles()
        drawParticles()
        updateAnimationAndState()
//        videoExport.saveFrame() // record
    }

    private fun revealParticles() {
        repeat(revealSpeed) {
            val x = random(0f, macaImg.width.toFloat())
            val y = random(0f, macaImg.height.toFloat())
            addParticle(x, y)
        }
        revealSpeed += revealAcceleration
    }

    private fun drawParticles() {
        particles.forEach {
            it.draw()
        }
    }

    private fun updateAnimationAndState() {
        if (state is FallingApart && animationProgress < 1f) {
            animationProgress += animationStep
        } else if (state is FallingApart) {
            transformImage()
            state = FallingApiece
        } else if (state is FallingApiece && animationProgress > 0f) {
            animationProgress -= animationStep
        } else if (state is FallingApiece) {
            state = Stopped
        }
    }

    private fun transformImage() {
        val targetImage = images[++imagePointer % images.size]
        particles.forEach {
            val newColor = getColor(targetImage, it.origin.x, it.origin.y)
            it.color = newColor
        }
    }

    override fun mouseDragged() {
        noStroke()
        val x = mouseXF - xOffset
        val y = mouseYF - yOffset
        addParticle(x, y)
    }

    override fun keyPressed() {
        if (key == ' ') {
            state = state.switch()
        }
    }

    private fun addParticle(x: Float, y: Float) {
        val xParticle = random(x - randomAreaRadius, x + randomAreaRadius)
        val yParticle = random(y - randomAreaRadius, y + randomAreaRadius)
        val origin = PVector(xParticle, yParticle)

        var canBeAdded = true
        particles.forEach {
            if (it.origin.dist(origin) < particleSize * 2) canBeAdded = false
        }

        if (canBeAdded) {
            val color = getColor(macaImg, xParticle, yParticle)
            particles.add(Particle(origin, color = color))
        }
    }

    private fun getColor(image: PImage, xParticle: Float, yParticle: Float) =
        image.get(floor(xParticle), floor(yParticle))

    private inner class Particle(
        val origin: PVector,
        val size: Float = particleSize,
        var color: Int
    ) {

        val destination = PVector(macaImg.width / 2f, macaImg.height / 2f)

        fun draw() {
            fill(color)
            noStroke()
            val position = when (state) {
                is Revealing -> origin
                is FallingApart -> lerpLocation()
                is FallingApiece -> lerpLocation()
                is Stopped -> lerpLocation()
            }
            circle(position, size * 2f)
        }

        private fun lerpLocation() = origin.copy().lerp(destination, animationProgress)
    }

    private sealed class State {
        init {
            println("state: ${this::class.simpleName}")
        }

        fun switch(): State {
            return when (this) {
                is Stopped -> Revealing
                is Revealing -> FallingApart
                is FallingApart -> FallingApiece
                is FallingApiece -> Stopped
            }
        }
    }

    private object Revealing : State()
    private object FallingApart : State()
    private object FallingApiece : State()
    private object Stopped : State()
}
