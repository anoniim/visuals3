package sketch.waves

import BaseSketch
import Screen
import processing.core.PVector
import util.SimplexNoise

class WavyBackgroundDrops : BaseSketch(fullscreen = true) {

    // CONFIG
    private val pronunciation = 5f
    private val xZoom = 80f
    private val yZoom = 400f
    private val margin = 150f
    private val layers = MutableList(1) {
        generateDrop()
    }

    private val noise = SimplexNoise()
    private var noisePointer = 0f

    override fun setup() {
        screen.width = width
        screen.height = height
    }

    override fun draw() {
        background(cream)
        for (layer in layers) {
            layer.update()
            layer.show()
        }
        if (frameCount % 120 == 0) {
            layers.add(generateDrop())
        }
        layers.removeIf {
            it.isNotVisible()
        }
        noisePointer += 0.005f
    }

    private fun generateDrop(): Drop {
        val randomColor = colors.pastel.random()
//        val origin = PVector(random(margin, screen.widthF - margin), random(margin, screen.heightF - margin))
        val origin = PVector(screen.centerX, screen.centerY)
        return Drop(origin, randomColor)
    }

    private inner class Drop(val origin: PVector, val color: Int) {
        private val points = 20
        private var diameter: Float = 0f
        private var alpha: Float = 355f
        private var age = 0f

        fun update() {
            val expansionRate = 0.5f + sin(age / 2) * 0.3f
            diameter += expansionRate
            alpha -= 0.2f
            age += 0.05f
        }

        fun show() {
            noStroke()
            fill(color, alpha)
            beginShape()
            val firstPoints = mutableListOf<PVector>()
            for (i in 0..points) {
                val theta = i * TWO_PI / points
                val rx = cos(theta)
                val ry = sin(theta)
                val x = origin.x + diameter * rx
                val y = origin.y + diameter * ry
                val radialNoise = diameter / pronunciation * abs(noise.eval(noisePointer + rx, noisePointer + ry))
                val vertex = PVector(x + radialNoise, y + radialNoise)
                if (firstPoints.size <= 4) {
                    firstPoints.add(vertex)
                }
                curveVertex(vertex.x, vertex.y)
            }
            // to make the shape nicely round
            for (point in firstPoints.slice(1..4)) {
                curveVertex(point.x, point.y)
            }
            endShape()
        }

        fun isNotVisible(): Boolean {
            return alpha < 0f || diameter > screen.widthF
        }
    }
}
