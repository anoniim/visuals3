package sketch.dots

import BaseSketch
import processing.core.PVector
import util.translate

class CirclePendulum2 : BaseSketch(renderer = P2D) {

    private val backgroundAlpha = 255f // 10f To see the dots are moving in a straight line

    private var time = 0f
    private val leftPendulum = CirclePendulum(PVector(screen.centerX -150f, screen.centerY), 0f)
    private val rightPendulum = CirclePendulum(PVector(screen.centerX +150f, screen.centerY), PI)

    override fun setup() {
        background(grey3)
    }

    override fun draw() {
        drawBackground()
        leftPendulum.show()
        rightPendulum.show()
        if (!keyPressed) {
            time += 0.02f
        }
    }

    private fun drawBackground() {
        fill(grey3, backgroundAlpha)
        rect(0f, 0f, screen.widthF, screen.heightF)
    }

    private inner class CirclePendulum(val translationVector: PVector, offset: Float) {

        private val numOfDots = 5 // Overlapping when even
        private val travelDistance = 300f
        private val size = 40f

        private val dots = List(numOfDots) {
            val segment = TWO_PI / numOfDots
            val rotation = it * segment
            Particle(offset + rotation, size, rotation + segment/4f, travelDistance, white)
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
        private val color: Int = color(
            random(100f, 200f),
            random(100f, 200f),
            random(100f, 200f)
        )
    ) {

        private var position = PVector()

        fun update() {
            position.x = travelDistance * sin(time + offset)
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


