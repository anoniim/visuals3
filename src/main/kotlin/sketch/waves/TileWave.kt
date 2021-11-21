package sketch.waves

import BaseSketch
import com.hamoid.VideoExport
import util.translateToCenter

class TileWave : BaseSketch() {

    private val particleFillAlpha = 80f
    private val minRadius = 100f
    private val maxRadius = screen.halfWidth + 200f
    private val radiusStep = 10f
    private val angleStart = QUARTER_PI
    private val angleEnd = 7.5f * QUARTER_PI
    private val particles = generateParticleLayers()

    private val videoExport by lazy {
        VideoExport(this).apply {
            startMovie()
        }
    }

    override fun draw() {
        translateToCenter()
        background(white)

        particles.forEach {
            it.update()
            it.draw()
        }

        // record
//        videoExport.saveFrame()
//        noLoop()
//        saveFrame("export/tileWave/1.png")
    }

    private fun generateParticleLayers(): List<Particle> {
        val particles = mutableListOf<Particle>()
        var radius = minRadius
        while (radius < maxRadius) {
            particles.addAll(generateParticleLayer(radius))
            radius += radiusStep
        }
        return particles
    }

    private fun generateParticleLayer(radius: Float): List<Particle> {
        val angleOffset = QUARTER_PI / 2f
        var angle = angleStart + random(-angleOffset, angleOffset)
        val step = map(radius, minRadius, maxRadius, 0.1f, 0.15f)
        val angleEnd = angleEnd + random(-angleOffset, angleOffset)
        val particles = mutableListOf<Particle>()
        while (angle < angleEnd) {
            angle += random(step) - step
            particles.add(Particle(radius, angle))
            angle += step
        }
        return particles
    }

    private inner class Particle(
        private val radius: Float,
        private val angleOrigin: Float
    ) {

        private val rotationOffset = (random(QUARTER_PI) - QUARTER_PI) / 2f
        private val fillColor = generateParticleColor()
        private val fillAlpha = generateParticleFillAlpha()
        private val width: Float
        private val height: Float
        private var progress = random(PI)
        private val moveDistance = random(PI)
        private val isMoving = random(1f) < 0.9f
        private var angle = getInitialAngle()

        private fun getInitialAngle(): Float {
            return if (isMoving && angleOrigin > TWO_PI - moveDistance) {
                angleOrigin - moveDistance
            } else angleOrigin
        }

        init {
            val widthModifier = map(radius, minRadius, maxRadius, 0.1f, 1f)
            val heightModifier = map(radius, minRadius, minRadius + 100f, 0.7f, 1f)
            width = random(50f) * widthModifier
            height = random(100f) * heightModifier
        }

        fun update() {
            progress += 0.01f
        }

        fun draw() {
            val angle = if (isMoving) {
                angle + map((progress % PI) - HALF_PI, -HALF_PI, HALF_PI, 0f, moveDistance)
            } else angle
            val x = radius * cos(angle)
            val y = radius * sin(angle)

            pushMatrix()
            translate(x, y)
            rotate(angle)
            rotate(rotationOffset)
            stroke(black, valueByProgress(255f))
            strokeWeight(0.5f)
            fill(fillColor, valueByProgress(fillAlpha))
            rectMode(CENTER)
            rect(0f, 0f, valueByProgress(width), valueByProgress(height))
            popMatrix()
        }

        private fun valueByProgress(originalValue: Float): Float {
            return if (isMoving) sin(progress % PI) * originalValue else originalValue
        }

        private fun valueByAngle(originalValue: Float): Float {
            val progress = angle % TWO_PI
            val progressThreshold = QUARTER_PI
            return if (progress < progressThreshold) {
                map(progress, 0f, progressThreshold, 0f, originalValue)
            } else if (progress > TWO_PI - progressThreshold) {
                map(progress, TWO_PI - progressThreshold, TWO_PI, originalValue, 0f)
            } else {
                originalValue
            }
        }

        private fun generateParticleFillAlpha() = if (random(1f) < 0.3f) 4 * particleFillAlpha else particleFillAlpha

        private fun generateParticleColor(): Int {
            return if (random(1f) < 0.25f) {
                color(35, 25, 21)
            } else {
                val radiusComp = map(radius, minRadius, maxRadius, 0f, 1f)
                val angleComp = map(angleOrigin, 0f, TWO_PI, 0f, 1f)
                val amt = radiusComp + angleComp - 0.4f
                val color1 = color(198, 78, 63)
                val color2 = color(53, 114, 102)
//                val color2 = color(33, 98, 95)
                lerpColor(color1, color2, amt, HSB)
            }
        }
    }
}
