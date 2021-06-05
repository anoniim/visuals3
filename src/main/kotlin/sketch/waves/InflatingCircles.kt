package sketch.waves

import BaseSketch
import Screen
import processing.core.PVector
import util.circle
import util.translateToCenter

class InflatingCircles : BaseSketch(Screen(fullscreen = false)) {

    private val maxVelocity = 50f

    private val circles = MutableList(2) {
        Circle()
    }

    override fun keyPressed() {
        circles.add(Circle())
    }

    override fun draw() {
        translateToCenter()
        background(grey3)
        stroke(grey11)
        noFill()

        circles.forEach { circle ->
            circle.repelFromOthers()
            circle.repelFromEdge()
            circle.repelFromCenter()
            circle.update()
            circle.draw()
        }
    }

    private inner class Circle(
        var extent: Float = random(5f, 50f)
    ) {

        private val color = colors.random
        private val origin = PVector()
        private val edgePadding = 1f

        private var acceleration: Float = 0f
        private var velocity: Float = 0f

        fun update() {
            velocity += acceleration
            velocity *= 0.99f
            constrain(velocity, 0f, maxVelocity)
            extent += velocity
            extent = constrain(extent, edgePadding, widthF - edgePadding)
            acceleration = 0f
        }

        fun repelFromOthers() {
            circles.forEach { other ->
                if (this != other) {
                    repelFrom(other.extent)
                }
            }
        }

        fun repelFromCenter() {
            repeat(1) {
                repelFrom(0f)
            }
        }

        fun repelFromEdge() {
            repeat(1) {
                repelFrom(widthF)
            }
        }

        fun repelFrom(otherExtent: Float) {
            val diff = extent - otherExtent
            val force = when {
                abs(diff) < 1f -> diff * 10f
                abs(diff) > 100f -> 0f
                else -> 10 / diff
            }
            acceleration += force / 2f
        }

        fun draw() {
            stroke(color)
            strokeWeight(2f)
            circle(origin, extent)
        }
    }
}
