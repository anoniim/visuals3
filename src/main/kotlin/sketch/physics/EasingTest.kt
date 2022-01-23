package sketch.physics

import BaseSketch
import Screen
import processing.core.PApplet
import processing.core.PVector
import util.Easing
import util.circle
import util.translate

fun main() {
    PApplet.main(EasingTest::class.java)
}

class EasingTest : BaseSketch(
    Screen(1200, 750)
) {

    private val speed = 0.01f
    private val graphSize = 200f
    private val ballSize = 40f
    private val margin = 150f

    private val graphs = listOf(
        Graph(PVector(margin, margin)) { i, max -> Easing.map2(i, 0f, 1f, 0f, max, Easing.LINEAR, Easing.EASE_IN) },
        Graph(PVector(2 * margin + graphSize, margin)) { i, max -> Easing.map2(i, 0f, 1f, 0f, max, Easing.QUADRATIC, Easing.EASE_IN) },
        Graph(PVector(3 * margin + 2 * graphSize, margin)) { i, max -> Easing.map2(i, 0f, 1f, 0f, max, Easing.EXPONENTIAL, Easing.EASE_IN) },
        Graph(PVector(margin, 2 * margin + graphSize)) { i, max -> Easing.map2(i, 0f, 1f, 0f, max, Easing.SQRT, Easing.EASE_IN) },
        Graph(PVector(2 * margin + graphSize, 2 * margin + graphSize)) { i, max -> Easing.map2(i, 0f, 1f, 0f, max, Easing.SINUSOIDAL, Easing.EASE_IN) },
        Graph(PVector(3 * margin + 2 * graphSize, 2 * margin + graphSize)) { i, max -> Easing.map2(i, 0f, 1f, 0f, max, Easing.CIRCULAR, Easing.EASE_IN) },
    )
    private var time = 0f
    private var progress = 0f

    override fun draw() {
        background(grey3)

        graphs.forEach {
            it.draw()
        }

        time += speed
        progress = abs(((time) % 2f) - 1f)
    }

    private inner class Graph(
        val position: PVector,
        val size: Float = graphSize,
        val function: (Float, Float) -> Float
    ) {

        fun draw() {
            pushMatrix()
            translate(position)
//            drawAxes()
            plotGraph()
            plotTime()
            drawBall()
            popMatrix()
        }

        private fun drawBall() {
            noStroke()
            fill(red)
            val x = function(progress, size)
            circle(PVector(x, - margin/2f), ballSize)
        }

        private fun plotTime() {
            strokeWeight(1f)
            stroke(grey11)
            noFill()
            val x = progress * size
            line(x, 0f, x, size)
        }

        private fun plotGraph() {
            strokeWeight(3f)
            stroke(yellow)
            noFill()
            beginShape()
            var i = 0f
            while (i <= 1f) {
                val x = size * i
                val y = size - function(i, size)
                curveVertex(x, y)
                i += 0.01f
            }
            endShape()
        }

        private fun drawAxes() {
            stroke(grey7)
            strokeWeight(2f)
            noFill()
            line(0f, size, size, size)
            line(0f, 0f, 0f, size)
        }
    }
}