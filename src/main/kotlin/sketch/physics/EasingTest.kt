package sketch.physics

import BaseSketch
import Screen
import org.gicentre.utils.move.Ease
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
    private val ballY = -margin / 4f

    private val graphs = listOf(
        Graph(PVector(margin, margin)) { i, max -> max * i },
        Graph(PVector(2 * margin + graphSize, margin)) { i, max -> Easing.map2(i, 0f, 1f, 0f, max, Easing.QUADRATIC, Easing.EASE_IN) },
        Graph(PVector(3 * margin + 2 * graphSize, margin)) { i, max -> max * Ease.quinticBoth(i) },
        Graph(PVector(margin, 2 * margin + graphSize)) { i, max -> max * Ease.elasticOut(i) },
        Graph(PVector(2 * margin + graphSize, 2 * margin + graphSize)) { i, max -> Easing.map2(i, 0f, 1f, 0f, max, Easing.EXPONENTIAL, Easing.EASE_IN) },
        Graph(PVector(3 * margin + 2 * graphSize, 2 * margin + graphSize)) { i, max -> max * Ease.bounceOut(i) },
    )
    private var time = 0f
    private var progress = 0f

    override fun draw() {
        background(grey3)

        graphs.forEach {
            it.draw()
        }

        time += speed
        progress = 1f - abs(((time) % 2f) - 1f)
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
            drawBallTrail()
            drawBall()
            popMatrix()
        }

        private fun drawBallTrail() {
            stroke(red, 150f)
            strokeWeight(1f)
            line(0f, ballY, size, ballY)
        }

        private fun drawBall() {
            noStroke()
            fill(red)
            val x = function(progress, size)
            circle(PVector(x, ballY), ballSize)
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