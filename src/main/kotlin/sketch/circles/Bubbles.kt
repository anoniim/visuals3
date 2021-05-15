package sketch.circles

import BaseSketch

class Bubbles : CirclePacking() {

    private val strokeColor = grey9
    private val fillColor = grey5
    private val backgroundColor = grey3

    override fun setup() {
//        smooth()
        noStroke()
        fill(fillColor)
    }

    override fun draw() {
        super.draw()
        circles.removeIf { touchesOther(it) }
    }

    private fun touchesOther(it: GrowingCircle): Boolean {
        val touches = (it as Bubble).touches
        return if (touches.size > 1) {
            for (other in it.touches) {
                other.touches.remove(it)
            }
            true
        } else {
            false
        }
    }

    override fun createCircle(x: Float, y: Float): GrowingCircle {
        return Bubble(this, x, y)
    }

    inner class Bubble(applet: BaseSketch, x: Float, y: Float) : GrowingCircle(applet, x, y) {

        var touches: MutableSet<Bubble> = mutableSetOf()

        override fun isOverlapping(): Boolean {
            for (other in circles) {
                if (other != this && dist(x, y, other.x, other.y) + 10 < r + other.r) {
                    touches.add(other as Bubble)
                    other.touches.add(this)
                    return true
                }
            }
            return touches.size > 0
        }
    }
}


