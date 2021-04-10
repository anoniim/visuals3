package sketch.dots

import BaseSketch
import processing.core.PVector
import util.Vectors
import util.translate

class CirclePendulum3 : BaseSketch(renderer = P2D) {

    private val backgroundAlpha = 255f // 10f To see the dots are moving in a straight line

    private var time = 0f
    private val topLeftPendulum = CirclePendulum(PVector(screen.centerX -150f, screen.centerY-150f), PI, -1)
    private val topRightPendulum = CirclePendulum(PVector(screen.centerX +150f, screen.centerY-150f), PI, 1)
    private val bottomLeftPendulum = CirclePendulum(PVector(screen.centerX -150f, screen.centerY+150f), TWO_PI, -1)
    private val bottomRightPendulum = CirclePendulum(PVector(screen.centerX +150f, screen.centerY+150f), TWO_PI, 1)

    override fun setup() {
        background(grey3)
    }

    override fun draw() {
        drawBackground()
        topLeftPendulum.show()
        topRightPendulum.show()
        bottomLeftPendulum.show()
        bottomRightPendulum.show()
        if (!keyPressed) {
            time += 0.02f
        }
    }

    private fun drawBackground() {
        fill(grey3, backgroundAlpha)
        rect(0f, 0f, screen.widthF, screen.heightF)
    }

    private inner class CirclePendulum(val translationVector: PVector, offset: Float, direction: Int) {

        private val numOfDots = 16 // Overlapping when even
        private val travelDistance = 300f
        private val size = 20f

        private val dots = List(numOfDots) {
            val segment = TWO_PI / numOfDots
            val rotation = it * segment
            Particle(offset + rotation, size, rotation + segment/2f, travelDistance, direction, grey7)
        }

        fun show() {
            pushMatrix()
            translate(translationVector)
            for (dot in dots) {
                dot.update()
                dot.show()
            }
            popMatrix()
        }
    }

    private inner class Particle(
        private val offset: Float,
        private val size: Float,
        private val rotation: Float,
        private val travelDistance: Float,
        private val direction: Int,
        private val color: Int = color(
            random(100f, 200f),
            random(100f, 200f),
            random(100f, 200f)
        )
    ) {

        private var position = Vectors.zero()

        fun update() {
            position.x = travelDistance * sin(direction * time + offset)
        }

        fun show() {
            pushMatrix()
            rotate(rotation)
            draw()
            popMatrix()
        }

        private fun draw() {
            noStroke()
            fill(color)
            circle(position.x, position.y, size)
        }
    }
}


