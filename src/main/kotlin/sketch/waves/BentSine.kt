package sketch.waves

import BaseSketch
import Screen
import com.hamoid.VideoExport
import processing.core.PApplet
import util.translateToCenter

fun main() {
    PApplet.main(BentSine::class.java)
}

class BentSine : BaseSketch(
    screen = Screen.LG_ULTRAWIDE,
    renderer = P2D,
    smoothLevel = 8
) {

    // config
    private val rotate90degrees = false
    private val speed = 0.1f
    private val alphaSpeed = 10f
    private val numOfWaves = 125
    private val amplitude = 50f
    private val minFrequency = 1f
    private val maxFrequency = 3f
    private val resolution = 400
    private val topMargin = 0f

    private val segmentLength by lazy { 2 * widthF / resolution }
    private val waves by lazy {
        MutableList(numOfWaves) {
            SineWave(it.toFloat())
        }
    }
    private val videoExport by lazy {
        VideoExport(this)
    }

    override fun setup() {
        super.setup()
        videoExport.startMovie()
    }

    override fun draw() {
        background(grey3)
        if (rotate90degrees) {
            translateToCenter()
            rotate(HALF_PI)
            translate(0f, -halfHeightF)
        } else translate(halfWidthF, 0f)

        waves.forEach {
            it.update()
            it.draw()
        }
        val anythingRemoved = waves.removeIf { it.state == State.NOT_ACTIVE }
        if (anythingRemoved) {
            waves.add(SineWave(1f, 0f))
        }
        videoExport.saveFrame()
    }

    private inner class SineWave(
        index: Float,
        private var alpha: Float = 255f
    ) {

        // config
        private val maxRotation: Float = TWO_PI
//            get() = mouseXF * 2 * TWO_PI / widthF
        private val rotation: Float
            get() = indexProgress * maxRotation / numOfWaves
        private val yPosition
            get() = topMargin + indexProgress * heightF / numOfWaves
        private val frequency
            get() = minFrequency + indexProgress * maxFrequency / numOfWaves
        private val color
            get() = lerpColor(purple, red, indexProgress / numOfWaves)

        var state = State.APPEARING
        private var indexProgress = index

        fun update() {
            indexProgress += speed

            if (state == State.APPEARING && alpha < 255f) alpha += alphaSpeed
            if (state == State.APPEARING && alpha >= 255) state = State.ACTIVE

            if (indexProgress > numOfWaves) state = State.DISAPPEARING
            if (state == State.DISAPPEARING && alpha > 0f) alpha -= alphaSpeed
            if (state == State.DISAPPEARING && alpha <= 0f) state = State.NOT_ACTIVE
        }

        fun draw() {
            if (state == State.NOT_ACTIVE) return
            noFill()
            stroke(color, alpha)
            strokeWeight(1f)
            pushMatrix()
            translate(0f, yPosition)
            rotate(rotation)
            beginShape()
            repeat(resolution + 1) {
                val x = -widthF + it * segmentLength
                val t = x * TWO_PI * frequency / widthF
                val y = amplitude * sin(t)
                vertex(x, y)
            }
            endShape()
            popMatrix()
        }
    }

    private enum class State {
        APPEARING,
        ACTIVE,
        DISAPPEARING,
        NOT_ACTIVE
    }
}