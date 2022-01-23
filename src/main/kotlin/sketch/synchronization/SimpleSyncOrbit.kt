package sketch.synchronization

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import util.*

fun main() = PApplet.main(SimpleSyncOrbit::class.java)

class SimpleSyncOrbit : BaseSketch() {

    companion object {
        private const val G = 0.4f
    }

    private val radius = 300f
    private val initForce = 5f
    private val moverCount = 8

    private val movers = MutableList(moverCount) {
        val angle = TWO_PI / moverCount
        val position = (PVector.fromAngle(it * angle) * radius)
        val force = PVector.fromAngle((it * angle) + HALF_PI) * initForce
        Mover(position).apply { applyForce(force) }
    }
    private val attractors = mutableListOf(
        Attractor(PVector())
    )

    override fun draw() {
        background(grey3)
        translateToCenter()

        movers.forEach {
            it.update()
            attractors.forEach { attractor ->
                it.applyForce(attractor.attract(it))
            }
            // config
            it.draw()
//            it.drawPath()
        }

        attractors.forEach {
            // config
//            it.draw()
        }
    }

    private inner class Mover(position: PVector) : Body(position, yellow, 20f)
    private inner class Attractor(position: PVector) : Body(position, red, 50f)

    private open inner class Body(var position: PVector, val color: Int, val size: Float) {

        private val mass: Float = 1f
        private val path = mutableSetOf<PVector>()
        private var velocity = PVector()
        private var acceleration = PVector()

        fun applyForce(force: PVector) {
            acceleration.add(force)
        }

        fun update() {
            velocity.add(acceleration)
            position.add(velocity)
            acceleration.mult(0f)

            path.add(position.copy())
        }

        fun draw() {
            strokeWeight(size)
            stroke(color)
            point(position)
        }

        fun drawPath() {
            strokeWeight(1f)
            stroke(red)
            noFill()
            beginShape()
            path.forEach {
                vertex(it)
            }
            endShape()
        }

        fun attract(other: Body): PVector {
            val force = position - other.position
            val distance = force.mag()
//            val distance = constrain(force.mag(), 10f, 50f)
            force.normalize()
            val forceStrength = G * mass * other.mass / distance * distance
            return force.setMag(forceStrength)
        }
    }

}
