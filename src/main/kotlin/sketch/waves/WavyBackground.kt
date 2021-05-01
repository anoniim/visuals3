package sketch.waves

import BaseSketch
import Screen
import util.SimplexNoise

class WavyBackground: BaseSketch(Screen(fullscreen = true)) {

    override fun setup() {
        screen.width = width
        screen.height = height
    }

    private val points = 10
    private val layers = 8
    private val noise = SimplexNoise()
    private val pronunciation = 50f
    private val xZoom = 80f
    private val yZoom = 400f
    private val colors = listOf(
        color(255, 184, 222),
        color(188, 184, 227),
        color(166, 221, 215),
        color(255, 249, 209),
        color(167, 214, 175))

    private var noisePointer = 0f

    override fun draw() {
        background(colors[1])
        for (l in layers downTo 0) {
            drawLayer(l)
        }
        noisePointer += 0.005f
    }

    private fun drawLayer(index: Int) {
        val layer = index * screen.widthF / layers
        val xOffset = layer / xZoom
        noStroke()
        fill(colors[index % colors.size])
        beginShape()
        vertex(0f, -0f)
        vertex(getX(layer, xOffset, 0f), -0f)
        for (p in 0..points) {
            val yVertex = p * screen.heightF / points
            curveVertex(getX(layer, xOffset, yVertex / yZoom), yVertex)
        }
        vertex(getX(layer, xOffset, screen.heightF / yZoom), screen.heightF)
        vertex(0f, screen.heightF)
        endShape(CLOSE)
    }

    private fun getX(layer: Float, xOffset: Float, yOffset: Float) =
        layer + noise.eval(xOffset + noisePointer, yOffset + noisePointer/5f) * pronunciation
}
