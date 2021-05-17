package sketch.physics

import BaseSketch
import processing.core.PVector
import util.minus
import util.translateToCenter

class AntiCollisionSystem: BaseSketch() {

    private val attractor = PVector(0f, 0f)
    private val gravityConst = 50f

    private val particles = List(5) {
        Particle(random(-400f, 400f), random(-400f, 400f))
    }

    override fun draw() {
        background(grey3)
        translateToCenter()
        drawAttractor()
        particles.forEach {
            it.collision()
            it.attract()
            it.update()
            it.draw()
        }
    }

    private fun drawAttractor() {
        stroke(green)
        strokeWeight(10f)
        point(attractor.x, attractor.y)
    }

    private inner class Particle(
        xInit: Float,
        yInit: Float,
        val size: Float = random(5f, 20f)
    ) {

        var position = PVector(xInit, yInit)
        var velocity = PVector(0.1f ,0.3f)
        var acceleration = PVector()
        var isInCollision = false

        fun update() {
            velocity.add(acceleration)
            position.add(velocity)
            acceleration = PVector(0f, 0f)
        }

        fun applyForce(force: PVector) {
            acceleration.add(force)
        }

        fun attract() {
            val force = attractor.copy() - position
            // Constrain distance squared to avoid particles shooting away
            val distanceSq = constrain(force.magSq(), 20f, 500f)
            val strength = gravityConst / distanceSq
            applyForce(force.setMag(strength))
        }

        fun draw() {
            stroke(if (isInCollision) red else grey11)
            strokeWeight(size * 2)
            point(position.x, position.y)
        }

        fun collision() {
            isInCollision = false
            for (other in particles) {
                if (this != other) {
                    if (isCollidingWith(other)) {
                        repelFrom(other)
                        isInCollision = true
                    }
                }
            }
        }

        private fun repelFrom(other: Particle) {
            // TODO Change their position instead
            applyForce((position.copy() - other.position).setMag(0.09f))
        }

        private fun isCollidingWith(other: Particle): Boolean {
            return position.dist(other.position) < size + other.size
        }
    }
}
