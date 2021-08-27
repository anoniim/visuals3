package sketch.synchronization

import BaseSketch
import processing.core.PApplet
import processing.core.PGraphics.G
import processing.core.PVector
import sketch.circles.Incircles
import util.minus
import util.point
import util.translateToCenter

fun main() = PApplet.main(SyncOrbits::class.java)

class SyncOrbits : BaseSketch() {

    companion object {
        private const val G = 0.5f
    }

    private val movers = mutableListOf(
        Mover(PVector(500f, 300f)).apply { applyForce(PVector(0f, 0f)) },
        Mover(PVector(300f, 200f)).apply { applyForce(PVector(10f, 5f)) },
        Mover(PVector(-300f, -300f)).apply { applyForce(PVector(0f, -15f)) },
    )
    private val attractors = mutableListOf(
        Attractor(PVector())
    )

    override fun draw() {
        background(grey3)
        translateToCenter()

        movers.forEach {
            it.update()
            it.draw()
            attractors.forEach { attractor ->
                it.applyForce(attractor.attract(it))
            }
        }

        attractors.forEach {
            it.draw()
        }
    }

    private inner class Mover(position: PVector) : Body(position, grey11, 20f)
    private inner class Attractor(position: PVector) : Body(position, red, 50f)

    private open inner class Body(var position: PVector, val color: Int, val size: Float) {

        private val mass: Float = 1f
        private var velocity = PVector()
        private var acceleration = PVector()

        fun applyForce(force: PVector) {
            acceleration.add(force)
        }

        fun update() {
            velocity.add(acceleration)
            position.add(velocity)
            acceleration.mult(0f)
        }

        fun draw() {
            strokeWeight(size)
            stroke(color)
            point(position)
        }

        fun attract(other: Body): PVector {
            val force = position.copy() - other.position
            val distance = force.mag()
//            val distance = constrain(force.mag(), 10f, 50f)
            force.normalize()
            val forceStrength = G * mass * other.mass / distance * distance
            return force.setMag(forceStrength)
        }
    }

}
