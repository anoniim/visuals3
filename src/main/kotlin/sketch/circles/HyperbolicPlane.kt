package sketch.circles

import BaseSketch

class HyperbolicPlane : BaseSketch() {

    private val strokeColor = grey9
    private val fillColor = grey5
    private val backgroundColor = grey3

    private val itemSize: Float = 20F
    private val itemHorizontalCount: Int = (screen.width / itemSize).toInt() + 3
    private val itemVerticalCount: Int = (screen.height / itemSize).toInt() + 3
    private val itemTotalCount: Int = itemHorizontalCount * itemVerticalCount

    override fun setup() {
//        smooth()
        background(backgroundColor)
        stroke(strokeColor)
        noFill()
        translate(width/2F, height/2F)

        // main circle
        val diameter: Float = (screen.height * 5/6).toFloat()
        ellipse(0F, 0F, diameter, diameter)

        // inner circles
        val lines = 5

        val angle = 360 / lines
        for (i in 1..lines) {
            pushMatrix()
            val currentRotation = (i * angle).toFloat()
            rotate(radians(currentRotation))

            line(0F, 0F, 0F, -1F * height)
            ellipse(0F, -3 / 4F * diameter, diameter, diameter)

            popMatrix()
        }

        val lines2 = 2*lines
        val diameter2 = diameter * 1/4
        val angle2 = 360 / lines2
        val edge = -1/2F * diameter
        drawCircles(lines2, angle2, diameter2, edge)

        val lines3 = 4*lines
        val diameter3 = diameter * 2/13
        val angle3 = 360 / lines3
        drawCircles(lines3, angle3, diameter3, edge)

        val lines4 = 8*lines
        val diameter4 = diameter * 2/25
        val angle4 = 360 / lines3
        drawCircles(lines4, angle4, diameter4, edge)


        // cover main circle outside
        stroke(backgroundColor)
        for (i in diameter.toInt() + 1 .. width) {
            ellipse(0F, 0F, i.toFloat(), i.toFloat())
        }

    }

    private fun drawCircles(lines: Int, angle: Int, diameter: Float, edge: Float) {
        for (i in 1 .. lines) {
            pushMatrix()
            val currentRotation = (i * angle).toFloat()
            rotate(radians(currentRotation))
            ellipse(0F, edge, diameter, diameter)

            popMatrix()
        }
    }
}


