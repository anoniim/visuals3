package sketch.waves

import BaseSketch
import util.translateToCenter

class TileWave : BaseSketch() {

    private val particleFillAlpha = 80f
    private val minRadius = 100f
    private val maxRadius = screen.halfWidth + 200f
    private val radiusStep = 10f
    private val angleStart = QUARTER_PI
    private val angleEnd = 7.5f * QUARTER_PI

    override fun draw() {
        translateToCenter()
        background(white)

        var radius = minRadius
        while (radius < maxRadius) {
            drawParticleLayer(radius)
            radius += radiusStep
        }

        noLoop()
        saveFrame("export/tileWave/1.png")
    }

    private fun drawParticleLayer(radius: Float) {
        val angleOffset = QUARTER_PI / 2f
        var angle = angleStart + random(-angleOffset, angleOffset)
        val step = map(radius, minRadius, maxRadius, 0.1f, 0.15f)
        val angleEnd = angleEnd + random(-angleOffset, angleOffset)
        while (angle < angleEnd) {
            angle += random(step) - step
            Particle(radius, angle).draw()
            angle += step
        }
    }

    private inner class Particle(
        private val radius: Float,
        private val angle: Float
    ) {

        fun draw() {
            val x = radius * cos(angle)
            val y = radius * sin(angle)

            pushMatrix()
            translate(x, y)
            rotate(angle)
            rotate(getRandomAngleOffset())

            stroke(black)
            strokeWeight(0.5f)
            fill(getParticleColor(), getParticleFillAlpha())
            rectMode(CENTER)
            val widthModifier = map(radius, minRadius, maxRadius, 0.1f, 1f)
            val heightModifier = map(radius, minRadius, minRadius + 100f, 0.7f, 1f)
            val width = random(50f) * widthModifier
            val height = random(100f) * heightModifier
            rect(0f, 0f, width, height)
            popMatrix()
        }

        private fun getParticleFillAlpha() = if (random(1f) < 0.1f) 4 * particleFillAlpha else particleFillAlpha

        private fun getParticleColor(): Int {
            return if (random(1f) < 0.35f) {
                color(35, 25, 21)
            } else {
                val radiusComp = map(radius, minRadius, maxRadius, 0f, 1f)
                val angleComp = map(angle, 0f, TWO_PI, 0f, 1f)
                val amt = radiusComp + angleComp - 0.4f
                val color1 = color(198, 78, 63)
                val color2 = color(53, 114, 102)
//                val color2 = color(33, 98, 95)
                lerpColor(color1, color2, amt, HSB)
            }
        }

        private fun getRandomAngleOffset(): Float {
            return (random(QUARTER_PI) - QUARTER_PI) / 2f
        }
    }

}
