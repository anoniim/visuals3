package sketch.waves

import BaseSketch
import Screen
import util.SimplexNoise

class WavyBackground: BaseSketch(Screen(fullscreen = true)) {

    // CONFIG
    private val orientation = Orientation.VERTICAL
    private val points = 10
    private val layers = 8
    private val pronunciation = 50f
    private val xZoom = 80f
    private val yZoom = 400f
    private val colors = listOf(
        color(255, 184, 222),
        color(188, 184, 227),
        color(166, 221, 215),
        color(255, 249, 209),
        color(167, 214, 175))

    private val noise = SimplexNoise()
    private var noisePointer = 0f

    override fun setup() {
        screen.width = width
        screen.height = height
    }

    override fun draw() {
        background(colors[1])

        val layerRange = when (orientation) {
            Orientation.HORIZONTAL -> 0..layers
            Orientation.VERTICAL -> layers downTo 0
        }
        for (l in layerRange) {
            drawLayer(l)
        }
        noisePointer += 0.005f
    }

    private fun drawLayer(index: Int) {
        when (orientation) {
            Orientation.HORIZONTAL -> {
                val layer = index * screen.heightF / layers
                val xOffset = layer / xZoom
                noStroke()
                fill(colors[index % colors.size])
                beginShape()
                vertex(0f, screen.heightF)
                vertex(0f, getNoisyDimension(layer, xOffset, 0f))
                for (p in 0..points) {
                    val xVertex = p * screen.widthF / points
                    curveVertex(xVertex, getNoisyDimension(layer, xOffset, xVertex / yZoom))
                }
                vertex(screen.widthF, getNoisyDimension(layer, xOffset, screen.widthF / yZoom))
                vertex(screen.widthF, screen.heightF)
                endShape(CLOSE)
            }
            Orientation.VERTICAL -> {
                val layer = index * screen.widthF / layers
                val xOffset = layer / xZoom
                noStroke()
                fill(colors[index % colors.size])
                beginShape()
                vertex(0f, -0f)
                vertex(getNoisyDimension(layer, xOffset, 0f), -0f)
                for (p in 0..points) {
                    val yVertex = p * screen.heightF / points
                    curveVertex(getNoisyDimension(layer, xOffset, yVertex / yZoom), yVertex)
                }
                vertex(getNoisyDimension(layer, xOffset, screen.heightF / yZoom), screen.heightF)
                vertex(0f, screen.heightF)
                endShape(CLOSE)
            }
        }
    }

    private fun getNoisyDimension(layer: Float, xOffset: Float, yOffset: Float) =
        layer + noise.eval(xOffset + noisePointer, yOffset + noisePointer/5f) * pronunciation

    private enum class Orientation {
        HORIZONTAL,
        VERTICAL
    }
}
