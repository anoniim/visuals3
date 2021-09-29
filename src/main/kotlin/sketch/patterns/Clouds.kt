package sketch.patterns

import BaseSketch
import processing.core.PApplet
import util.SimplexNoise

fun main() = PApplet.main(Clouds::class.java)

class Clouds : BaseSketch() {

    // config
    private val size = 15f // 1+
    private val exaggeratedNoiseThreshold = 0.7f // lower for bigger spots
    private val exaggeratedNoiseFactor = 5f
    private val noiseFactor = 50f

    private val maxNoiseValue = exaggeratedNoiseThreshold + ((1 - exaggeratedNoiseThreshold) * exaggeratedNoiseFactor)
    private val numOfCols = floor(screen.widthF / size) + 1
    private val numOfRows = floor(screen.heightF / size) + 1
    private val noise = SimplexNoise()
    private var time: Float = 0f

    override fun draw() {
//        translateToCenter()
        background(grey3)

        drawCircles()

        // config
//        time += 0.01f
        noLoop()
    }

    private fun drawCircles() {
        noStroke()
        fill(grey11)
        repeat(numOfCols) { col ->
            val x = col * size
            repeat(numOfRows) { row ->
                val y = row * size
                drawCircle(x, y)
            }
        }
    }

    private fun drawCircle(x: Float, y: Float) {
        val noiseValue = getNoiseValue(x, y)
        // config
//        if (noiseValue > -exaggeratedNoiseThreshold && noiseValue < exaggeratedNoiseThreshold) return
        val colorAmt = map(noiseValue, -maxNoiseValue, maxNoiseValue, 0f, 1f)
        val color = lerpColor(black, white, colorAmt)
        fill(color)
        circle(x, y, size)
    }

    private fun getNoiseValue(x: Float, y: Float): Float {
        val xNoise = x / noiseFactor
        val yNoise = y / noiseFactor
        val noiseAmt = noise.eval(xNoise, yNoise, time)
        return if (noiseAmt > exaggeratedNoiseThreshold) {
            val reminder = noiseAmt - exaggeratedNoiseThreshold
            exaggeratedNoiseThreshold + exaggeratedNoiseFactor * reminder
        } else if (noiseAmt < -exaggeratedNoiseThreshold) {
            val reminder = noiseAmt + exaggeratedNoiseThreshold
            -exaggeratedNoiseThreshold + exaggeratedNoiseFactor * reminder
        } else {
            noiseAmt
        }
    }
}