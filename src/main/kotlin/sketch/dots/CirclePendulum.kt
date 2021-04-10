package sketch.dots

import BaseSketch
import processing.core.PVector
import util.Vectors

class CirclePendulum : BaseSketch(renderer = P2D) {

    private val numOfDots = 20
    private val circleRadius = 0f
    private val travelDistance = 400f
    private val size = 50f
    private val backgroundAlpha = 255f // 10f To see the dots are moving in a straight line

    private val dots = List(numOfDots) {
        val rotation = it * TWO_PI / numOfDots
        val initialPosition = PVector.fromAngle(rotation).setMag(circleRadius)
        Particle(initialPosition, it, rotation, white)
    }

    override fun setup() {
        background(grey3)
    }

    override fun draw() {
        fill(grey3, backgroundAlpha)
        rect(0f, 0f, screen.widthF, screen.heightF)
        translate(screen.centerX, screen.centerY)

        drawInitialPositionCircle()

        for (dot in dots) {
            dot.update()
            dot.show()
        }
    }

    private fun drawInitialPositionCircle() {
        fill(red, backgroundAlpha)
        noStroke()
        circle(0f, 0f, travelDistance*2 + size)
    }

    private inner class Particle(
        private val initialPosition: PVector,
        private val offset: Int,
        private val rotation: Float,
        private val color: Int = color(random(circleRadius, 255f), random(circleRadius, 255f), random(circleRadius, 255f))
    ) {

        private var position = Vectors.zero()

        fun update() {
            position.x = travelDistance * sin(frameCount / 50f + offset * TWO_PI / numOfDots)
        }

        fun show() {
            pushMatrix()
            translate(initialPosition.x, initialPosition.y)
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


