package sketch.spirals

import BaseSketch
import processing.core.PVector

class VortexGroups : BaseSketch(renderer = P3D) {

    private val numOfDots = 15 // 1-25
    private val strokeWeight = 4f
    private val strokeAlpha = 240f

    private var yTranslation: Float = 55.5f
    private var zTranslation: Float = -8.5f
    private var scale: Float = 0.95f

    private val dots = List(numOfDots) {
        val strokeColor = color(random(100f, 200f), random(100f, 200f), random(100f, 200f))
        Particle(200f + it, strokeColor)
    }

    override fun draw() {
        background(grey3)
        updateCamera()

        for (dot in dots) {
            dot.update()
            dot.draw()
        }
    }

    private fun updateCamera() {
        translate(screen.centerX, screen.centerY * 1.8f)
        translate(0f, yTranslation, zTranslation)
        scale(scale)
        yTranslation *= 1.0033f
        zTranslation = constrain(zTranslation * 1.006f, -1500f, 1f)
        scale *= 0.9985f
    }

    inner class Particle(
        private val spiralCoef: Float,
        val strokeColor: Int
    ) {

        private var position: PVector = PVector(0f, -100f)
        private val path = mutableListOf<PVector>()

        fun update() {
            val y = position.y
            val x = sin(y)
            val z = cos(y)
            val spiral = PVector(x, 0f, z).setMag(y/10f)
            position.add(spiral)
            val up = PVector(0f, (y/ spiralCoef), 0f)
            position.add(up)
            path.add(position.copy())
        }

        fun draw() {
            strokeWeight(strokeWeight)
            stroke(strokeColor, strokeAlpha)
            noFill()
            beginShape()
            for (point in path) {
                curveVertex(point.x, point.y, point.z)
            }
            endShape()
        }
    }
}


