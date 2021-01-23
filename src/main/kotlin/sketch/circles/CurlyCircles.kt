package sketch.circles

import BaseSketch
import Screen

class CurlyCircles : BaseSketch(Screen(800, 800)) {

    private val initialDiameter = 200f

    private val angleStep = 0.01f
    private val circles = mutableListOf(
        Arc(
            diameter = initialDiameter,
            startAngle = HALF_PI,
            primary = true
        )
    )

    override fun draw() {
        background(grey3)
        translate(screen.centerX, screen.centerY)
        for (i in 0 until circles.size) {
            circles[i].update()
            circles[i].show()
        }
    }

    inner class Arc(
        val x: Float = 0f,
        val y: Float = 0f,
        val diameter: Float,
        var startAngle: Float,
        val parent: Arc? = null,
        val primary: Boolean = false
    ) {

        private val level: Int = (parent?.level ?: 0) + 1
        private val minThreshold = 7
        private val maxThreshold = 9
        private val fullCircle = TWO_PI
        private val segment = fullCircle / if (primary) 8 else 16
        private var angle = 0f
        private var nextThresholdIndex = if (primary) 1 else minThreshold


        fun update() {
            if (angle > fullCircle) return
            angle += angleStep

            if (shouldSpawnNew()) {
                nextThresholdIndex++
                spawnNew()
            }
        }

        private fun spawnNew() {
            val newDiameter = 3f * diameter / 5f
            val distanceRadius = diameter / 2f + newDiameter / 2f
            circles.add(
                Arc(
                    x = cos(startAngle + angle) * distanceRadius,
                    y = sin(startAngle + angle) * distanceRadius,
                    diameter = newDiameter,
                    startAngle = startAngle + angle - PI,
                    parent = this
                )
            )
        }

        private fun shouldSpawnNew(): Boolean {
            val isBelowMaxLevel = level < 5
            if (!isBelowMaxLevel) return false

            val isWithinThresholdLimit = primary || (!primary && nextThresholdIndex <= maxThreshold)
            if (!isWithinThresholdLimit) return false

            val skip = primary || (!primary && nextThresholdIndex != 10)
            if(!skip) return false

            val nextThreshold = nextThresholdIndex * segment
            val hasReachedThreshold = angle > nextThreshold
            return hasReachedThreshold
        }

        fun show() {
            noFill()
            pushMatrix()
            translateToOrigin()

            rotate(startAngle)

            stroke(grey9)
            strokeWeight(3f)
            arc(0f, 0f, diameter, diameter, 0f, angle)
            popMatrix()
        }

        private fun translateToOrigin() {
            if (parent == null) return
            parent.translateToOrigin()
            translate(x, y)
        }
    }
}