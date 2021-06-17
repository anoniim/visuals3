package sketch.physics

import BaseSketch
import Screen
import processing.core.PVector
import structures.QuadTree
import kotlin.random.Random

class SpaceSimulation : BaseSketch(
    Screen(1600, 900),
    fullscreen = false,
    renderer = P2D
) {

    private val collisionAge = 5000f
    private val explosionAcceleration = 10000f
    private val gravityConst = 0.02f
    private val gravityRange = 150f
    private val velocityLimit = 2f
    private val offScreenLimit = 200f
    private val minMass = 100f
    private val maxMass = 200f
    private val criticalMass = 1000f

    private val newParticles = mutableListOf<Particle>()

    private val particles = MutableList(100) {
        Particle(
            x = random(screen.widthF),
            y = random(screen.heightF),
            mass = random(minMass, maxMass)
        ).apply { applyForce(PVector(random(-5f, 5f), random(-5f, 5f))) }
    }

    override fun setup() {
//        smooth()
    }

    override fun draw() {
        background(grey3)

        val qTree = QuadTree<Particle>(screen.centerX, screen.centerY, screen.centerX).apply {
            insert(particles)
        }

        for (particle in particles) {
            particle.update()
            particle.gravity(qTree.query(particle.x, particle.y, gravityRange))
            particle.edges()
            particle.show()
        }
        particles.removeIf { it.isDying }
        particles.addAll(newParticles)
        newParticles.clear()
    }

    open inner class Particle(
        x: Float,
        y: Float,
        var mass: Float,
    ) : PVector(x, y) {

        private val velocity: PVector = PVector(0f, 0f)
        private val position = PVector(x, y)
        private var acceleration = PVector(0f, 0f)
        val x: Float
            get() = position.x
        val y: Float
            get() = position.y
        private val size = log(mass)
        var isDying = false
        var color = color(map(mass, 1f, criticalMass, 105f, 255f))
        var age = 0

        fun update() {
            if (mass > criticalMass) {
                explode(mass)
                return
            }
            if (age > 10000) {
                acceleration.mult(0.9f)
            }
            velocity.add(acceleration)
            velocity.limit(velocityLimit)
            position.add(velocity)
            age += Random.nextInt(100)
        }

        fun explode(mass: Float) {
            isDying = true
            val newParticleCount = ceil(mass / minMass)
            val angle = TWO_PI / newParticleCount
            for (i in 0 until newParticleCount) {
                val explosionAcc = position.copy().add(PVector.fromAngle(i * angle).mult(explosionAcceleration))
                val newParticle = Particle(position.x, position.y, minMass)
                newParticle.applyForce(explosionAcc)
                newParticles.add(newParticle)
            }
        }

        fun edges() {
//            bounce()
            wrap()
        }

        fun gravity(particles: List<Particle>) {
            if (isDying) return
            println(particles.size)
            for (other in particles) {
                if (this != other) {
                    val distance = distanceFrom(other)
                    if (distance < 0f && age > collisionAge && other.age > collisionAge) { // && isNotAccelerating(other)) {
                        merge(other)
                        break
                    } else if (distance < gravityRange) {
                        attract(other)
                    }
                }
            }
        }

        private fun isNotAccelerating(other: Particle) = acceleration.mag() < 100f && other.acceleration.mag() < 100f

        private fun distanceFrom(other: Particle): Float {
            val radiusSum = size / 2f + other.size / 2f
            return position.dist(other.position) - radiusSum
        }

        private fun merge(other: Particle) {
            isDying = true
            other.isDying = true
            val newPosition = position.add(other.position.copy().sub(position).mult(0.5f))
            val newMass = mass + other.mass
            val mergeAcceleration = velocity.add(other.velocity)
            val newParticle = Particle(newPosition.x, newPosition.y, 2 * newMass / 3f)
            newParticle.applyForce(mergeAcceleration)
            newParticles.add(newParticle)
            explode(newMass / 3f)
        }

        private fun attract(other: Particle) {
            val force = other.position.copy().sub(position)
            val distSq = constrain(force.magSq(), 1f, gravityRange)
            val strength = gravityConst * (mass * other.mass) / distSq
            force.setMag(strength)
            applyForce(force)
        }

        fun applyForce(force: PVector) {
            acceleration.add(force)
        }

        private fun bounce() {
            if (position.x < -0f) {
                velocity.set(-velocity.x, velocity.y)
                acceleration.set(-acceleration.x, acceleration.y)
            }
            if (position.x > screen.widthF + 0f) {
                velocity.set(-velocity.x, velocity.y)
                acceleration.set(-acceleration.x, acceleration.y)
            }
            if (position.y < -0f) {
                velocity.set(velocity.x, -velocity.y)
                acceleration.set(acceleration.x, -acceleration.y)
            }
            if (position.y > screen.heightF + 0f) {
                velocity.set(velocity.x, -velocity.y)
                acceleration.set(acceleration.x, -acceleration.y)
            }
        }

        private fun wrap() {
            if (position.x < -0f) {
                position.set(screen.widthF, position.y)
            }
            if (position.x > screen.widthF + 0f) {
                position.set(0f, position.y)
            }
            if (position.y < -0f) {
                position.set(position.x, screen.heightF)
            }
            if (position.y > screen.heightF + 0f) {
                position.set(position.x, 0f)
            }
        }

        fun show() {
            strokeWeight(if (isDying) size * 1.9f else size)
            stroke(if (isDying) red else color)
            point(position.x, position.y)
        }
    }
}


