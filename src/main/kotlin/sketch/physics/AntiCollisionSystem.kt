package sketch.physics

import BaseSketch
import processing.core.PApplet
import processing.core.PApplet.constrain
import processing.core.PVector
import util.minus
import util.translateToCenter
import java.awt.Color.green
import java.awt.Color.red

fun main() = PApplet.main(AntiCollisionSystem::class.java.name)

/**
 * An attempt to implement a particle system that evolves into a state where there are no collisions,
 * similar to https://github.com/KonradLinkowski/AntiCollision/blob/main/src/main.ts
 * Not working yet.
 */
class AntiCollisionSystem : BaseSketch() {

    private val attractor = PVector(0f, 0f)
    private val gravityConst = 50f

    private var speed = 1
    private val particles = List(5) {
        Particle(random(-400f, 400f), random(-40f, 40f))
    }

    override fun draw() {
        background(grey3)
        translateToCenter()
        drawAttractor()
        repeat(speed) {
            updateAndDrawParticles()
        }
    }

    override fun keyPressed() {
        when {
            keyCode == UP -> {
                speed++
            }
            keyCode == DOWN -> {
                speed--
            }
            key == ' ' -> {
                speed = 0
            }
        }
    }

    private fun updateAndDrawParticles() {
        particles.forEach {
            it.attract()
            it.collision()
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
        var velocity = PVector(0.1f, 0.3f)
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
            val strength = (gravityConst / distanceSq) * 0.1f
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
            val collide_vec = position.copy() - other.position
            val dist = sqrt(collide_vec.x * collide_vec.x + collide_vec.y * collide_vec.y)
            val collide_axe = collide_vec.div(dist);

            val minDist = size + other.size
            position = position.add(collide_axe.mult(0.5f * (minDist - dist)))
            other.position = other.position.sub(
                collide_axe.mult(0.5f * (minDist - dist))
            )
        }

        private fun isCollidingWith(other: Particle): Boolean {
            return position.dist(other.position) < size + other.size
        }
    }
}
