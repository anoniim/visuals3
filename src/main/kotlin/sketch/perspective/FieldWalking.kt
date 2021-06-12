package sketch.perspective

import BaseSketch
import Screen

class FieldWalking : BaseSketch(
    Screen(),
    fullscreen = false,
    renderer = P3D,
    smoothLevel = 8
) {

    // config
    private val spacing: Float = 40f
    private val distance = 5000f
    private val speed = 4f
    private val lineWidth = 4f

    private val horizonHeight = -2 * heightF / 3f

    private val lines: MutableList<Line> by lazy {
        val numOfLines = ceil(3 * widthF / spacing)
        MutableList(numOfLines) { Line(it * spacing) }
    }

    override fun draw() {
        translate(0f, 3 * heightF / 4)
        background(grey3)

        drawHorizon()
        drawField()
    }

    private fun drawHorizon() {
        fill(blue)
        noStroke()
        beginShape()
        vertex(-3 * widthF,100 * horizonHeight, -distance)
        vertex(-3 * widthF, 100 * horizonHeight, -distance)
        vertex(-3 * widthF, horizonHeight, -distance)
        repeat(100) {
            val y = horizonHeight - 500 * noise(it/10f)
            val x = map(it.toFloat(), 0f, 99f, -3 * widthF, 4 * widthF)
            curveVertex(x, y, -distance)
        }
        vertex(4 * widthF,  horizonHeight, -distance)
        vertex(4 * widthF, 100 * horizonHeight, -distance)
        vertex(4 * widthF, 100 * horizonHeight, -distance)
        endShape(CLOSE)
//        beginShape()
//        vertex(- widthF, 0f, 0f)
//        vertex(- widthF, horizonHeight, -distance)
//        vertex(2 * widthF, horizonHeight, -distance)
//        vertex(2 * widthF, 0f, 0f)
//        endShape(CLOSE)
    }

    private fun drawField() {
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
            strokeWeight(lineWidth)
            stroke(darkGreen)
            noFill()
            line(x, 0f, 0f, x, horizonHeight, -distance)
        }
    }
}
