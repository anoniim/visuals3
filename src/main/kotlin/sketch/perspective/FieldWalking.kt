package sketch.perspective

import BaseSketch
import Screen

class FieldWalking : BaseSketch(
    Screen(fullscreen = false),
    renderer = P3D,
    smoothLevel = 8
) {

    // config
    private val spacing: Float = 40f
    private val distance = 5000f
    private val speed = 4f

    private val lines: MutableList<Line> by lazy {
        val numOfLines = ceil(3 * widthF / spacing)
        MutableList(numOfLines) { Line(it * spacing) }
    }

    override fun draw() {
        translate(0f, 3 * heightF / 4)
        background(grey3)
        strokeWeight(2f)
        stroke(darkGreen)
        noFill()

        lines.forEach {
            it.update()
            it.draw()
        }
    }

    private inner class Line(var x: Float = 0f) {

        fun update() {
            x += speed
            if (x > 2 * widthF) {
                x -= lines.size * spacing
            }
        }

        fun draw() {
            line(x, 0f, 0f, x, -2 * heightF / 3f, -distance)
        }
    }
}
