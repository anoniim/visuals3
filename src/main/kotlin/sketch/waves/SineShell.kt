package sketch.waves

import BaseSketch
import processing.core.PApplet

fun main() {
    PApplet.main(SineShell::class.java)
}

class SineShell : BaseSketch() {

    // config
    private val numOfWaves = 35
    private val amplitude = 50f
    private val minFrequency = 5f
    private val maxFrequency = 10f
    private val resolution = 400
    private val topMargin = 50f
//    private val frequency
//        get() = map(mouseXF, 0f, widthF, minFrequency, maxFrequency)

    private val segmentLength by lazy { widthF / resolution }

    private val waves by lazy {
        List(numOfWaves) {
            val yPosition = topMargin + it * heightF / numOfWaves
            val frequency = minFrequency + it * maxFrequency / numOfWaves
            SineWave(yPosition, frequency)
        }
    }

    override fun draw() {
        background(grey3)
        translate(halfWidthF, 0f)

        waves.forEach {
            it.draw()
        }
    }

    private inner class SineWave(val y: Float, val frequency: Float) {

        fun draw() {
            noFill()
            stroke(grey11)
            strokeWeight(1f)
            pushMatrix()
            translate(0f, y)
            beginShape()
            repeat(resolution + 1) {
                val x = -halfWidthF + it * segmentLength
                val t = x * TWO_PI * frequency / widthF
                val y = amplitude * sin(t)
                vertex(x, y)
            }
            endShape()
            popMatrix()
        }

    }
}