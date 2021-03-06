package sketch.circles

import BaseSketch
import kotlin.random.Random

open class CirclePacking : BaseSketch() {

    private val strokeColor = grey9
    private val fillColor = grey5
    private val backgroundColor = grey3

    private val itemSize: Float = 20F
    private val itemHorizontalCount: Int = (screen.width / itemSize).toInt() + 3
    private val itemVerticalCount: Int = (screen.height / itemSize).toInt() + 3
    private val itemTotalCount: Int = itemHorizontalCount * itemVerticalCount

    protected val circles = mutableListOf<GrowingCircle>()

    override fun setup() {
        smooth()
        stroke(strokeColor)
        noFill()
    }

    override fun draw() {
        background(backgroundColor)

//        if (frameCount % 100 == 0) {
            newCircle()?.let { circles.add(it) }
//        }

        for (circle in circles) {
            circle.grow()
            circle.draw()
        }
    }

    protected fun newCircle(): GrowingCircle? {
        val x = map(Random.nextFloat(), 0F, 1F, 0F, width.toFloat())
        val y = map(Random.nextFloat(), 0F, 1F, 0F, height.toFloat())
        var isInside = false
        for (circle in circles) {
            if (dist(x, y, circle.x, circle.y) < circle.r) {
                isInside = true
                break
            }
        }
        return if (!isInside) {
            createCircle(x, y)
        } else {
            null
        }
    }

    protected open fun createCircle(x: Float, y: Float) = GrowingCircle(this, x, y)

    open inner class GrowingCircle(
        applet: BaseSketch,
        x: Float,
        y: Float,
        r: Float = 0f): shapes.Circle(applet, x, y, r) {

        private var shouldGrow = true

        fun grow() {
            if (!isAtEdge() && !isOverlapping()) {
                r += 1
            }
        }

        protected open fun isOverlapping(): Boolean {
            for (other in circles) {
                if (other != this && dist(x, y, other.x, other.y) - 2 < r + other.r) {
                    return true
                }
            }
            return false
        }

        private fun isAtEdge(): Boolean {
            return x + r > width || x - r < 0 || y + r > height || y - r < 0
        }
    }

}


