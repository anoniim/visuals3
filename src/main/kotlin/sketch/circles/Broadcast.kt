package sketch.circles

import BaseSketch
import processing.core.PVector

class Broadcast : BaseSketch(
    smoothLevel = 8
) {

    private val numOfCircles = 20
    private val margin = 50f
    private val minRadius = 10F
    private val maxRadius = screen.heightF - 2 * margin

    private val circles = List(numOfCircles) {
        val spacing = (maxRadius - minRadius) / numOfCircles
        Circle(it * spacing)
    }

    override fun draw() {
        background(grey3)
        translate(screen.centerX, screen.centerY)

        circles.forEach {
            it.update()
            it.draw()
        }
    }

    private val speed = 1f

    private inner class Circle(
        private var radius: Float = minRadius
    ) {

        private var alpha = 210f
        private var center = PVector()

        fun update() {
            radius += speed
            if (alpha < 250f) alpha += 20f
            if (radius > maxRadius) {
                radius -= speed
                alpha -= 40f
                if (alpha < 0) {
                    radius = minRadius
                }
            }
            centerMouseControl()
        }

        private fun centerMouseControl() {
            val xControl = map(mouseXF, 0f, heightF, -1f, 1f)
            val yControl = map(mouseYF, 0f, heightF, -1f, 1f)
            moveCenter(xControl, yControl)
        }

        private fun moveCenter(xControl: Float, yControl: Float) {
            val x = xControl * (maxRadius - radius)
            val y = yControl * (maxRadius - radius)
            center = PVector(x, y)
        }

        fun draw() {
            noFill()
            stroke(grey9, alpha)
            strokeWeight((maxRadius-radius)/40f)
            circle(center.x, center.y, radius)
        }
    }
}


