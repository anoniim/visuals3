package sketch.synchronization

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import util.*

fun main() = PApplet.main(SyncOrbits::class.java)

class SyncOrbits : BaseSketch(renderer = P2D) {

    private val speed = 0.01f // 0.01-0.04
    private val bodySize = 30f // 10 - 40
    private val pathLength = 20 // 0 - 100
    private val visibleDistance = 200f // 50f - 200f

    private val systems = listOf(
        System(PVector(-185f, 185f)),
        System(PVector(185f, 185f)),
        System(PVector(-185f, -185f)),
        System(PVector(185f, -185f)),
    )

    override fun draw() {
        background(grey3)
        translateToCenter()

        systems.forEach {
            it.draw()
        }
    }


    private inner class System(val origin: PVector) {

        private val orbitCount = 8

        val orbits = List(orbitCount) {
            val direction = 1f
//            val phase = it/2f * TWO_PI / orbitCount
            val phase = 0f
            val rotation = it * TWO_PI / orbitCount
            Orbit(origin, 300f, 170f, rotation, direction, phase)
        }

        fun draw() {
            orbits.forEach {
                it.update()
                it.drawPath()
//                it.drawFullPath()
                it.draw()
            }
        }
    }

    private inner class Orbit(
        private val origin: PVector,
        private val a: Float,
        private val b: Float,
        private val rotation: Float,
        private val direction: Float,
        private val phase: Float,
    ) {

        private val path = mutableListOf<Pair<PVector, Float>>()
        private val fullPath = mutableListOf<PVector>()
        private var angle = 0f
        private var alpha = 255f
        private var position = PVector()

        fun update() {
            angle += direction * speed
            recalculatePosition()
            recalculateAlpha()
            addToPath()
        }

        private fun recalculatePosition() {
            val x = a * cos(phase + angle)
            val y = b * sin(phase + angle)
            position = PVector(x, y)
        }

        private fun addToPath() {
            if (path.size > pathLength) {
                path.removeFirst()
            }
            val position = position.copy()
            path.add(Pair(position, alpha))
            fullPath.add(position)
        }

        fun draw() {
            strokeWeight(bodySize)
            stroke(orange, alpha)
            applyTransformation {
                point(position)
            }
        }

        fun drawPath() {
            applyTransformation {
                path.reversed().forEachIndexed { i, item ->
                    val amt = i / path.size.toFloat()
                    val color = lerpColor(orange, yellow, amt)
                    stroke(color, item.second)
                    strokeWeight(bodySize - bodySize * amt)
                    point(item.first)
                }
            }
        }

        fun drawFullPath() {
            applyTransformation {
                stroke(red)
                strokeWeight(2f)
                fullPath.forEach {
                    point(it)
                }
            }
        }

        private fun applyTransformation(drawFunction: () -> Unit) {
            pushMatrix()
            translate(origin)
            rotate(rotation)
            drawFunction()
            popMatrix()
        }

        private fun recalculateAlpha() {
            var minDist = Float.MAX_VALUE
            systems.forEach { otherSystem ->
                otherSystem.orbits.forEach { otherOrbit ->
                    if (otherOrbit != this) {
                        val otherPosition = otherSystem.origin.copy() + (otherOrbit.position.copy().rotate(otherOrbit.rotation))
                        val thisPosition = origin.copy() + (position.copy().rotate(rotation))
                        val dist = thisPosition.dist(otherPosition)
                        minDist = min(minDist, dist)
                    }
                }
            }
            alpha = map(minDist, bodySize * 2f, visibleDistance, 100f, 10f)
        }
    }
}
