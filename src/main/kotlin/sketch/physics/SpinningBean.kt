package sketch.physics

import BaseSketch
import processing.core.PVector

class SpinningBean : BaseSketch(renderer = P2D) {

    private val numOfDots = 70
    private val dots = List(numOfDots) {
        val rotation = it * TWO_PI / numOfDots
        val initialPosition = PVector.fromAngle(rotation).setMag(150f)
//        val color = color(noise(100f+it, 255f)*100, random(100f, 255f), random(100f, 255f))
        Particle(initialPosition, it, rotation)
    }

    override fun draw() {
        background(grey3)
        translate(screen.centerX, screen.centerY)

        for (dot in dots) {
            dot.spring()
            dot.update()
            dot.draw()
        }
    }

    private inner class Particle(
        private val initialPosition: PVector,
        offset: Int,
        rotation: Float,
        private val color: Int = color(random(100f, 255f), random(100f, 255f), random(100f, 255f))
    ) {

        private val size = 10f
        private val velocity = PVector.fromAngle(rotation).setMag(10f)
        private var acceleration = PVector()
        private var position = initialPosition.copy()

        init {
            repeat(round(map(offset, 0, numOfDots,0, 64))) {
                spring()
                update()
            }
        }

        fun spring() {
            acceleration.add(initialPosition.copy().sub(position).div(100f))
        }

        fun update() {
            velocity.add(acceleration)
            position.add(velocity)
            acceleration = PVector()
        }

        fun draw() {
            noStroke()
            fill(color)
            circle(position.x, position.y, size)
        }
    }
}


