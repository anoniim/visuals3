package sketch.patterns

import BaseSketch

class CuriosityBoxSummer21 : BaseSketch() {

    private val size = 190f
    private val numOfCols by lazy { width / size }
    private val numOfRows by lazy { height / size }

    override fun draw() {
        background(grey3)

        repeat(ceil(numOfCols)) { i ->
            repeat(ceil(numOfRows)) { j ->
                val boxFill = getRandomBoxFill()
                drawBox(i, j, boxFill)
                drawCircles(i, j, boxFill.opposite())
            }
        }
        noLoop()
    }

    private fun drawCircles(i: Int, j: Int, direction: BoxFill) {
        val x = size * i
        val y = size * j
        val emptyColor = grey3
        val fillColor = orange
        when (direction) {
            BoxFill.NONE -> drawFullCircle(x, y, emptyColor)
            BoxFill.LEFT -> {
                drawHalfCircle(x, y, 0f, emptyColor)
                drawHalfCircle(x, y, PI, fillColor)
            }
            BoxFill.TOP -> {
                drawHalfCircle(x, y, HALF_PI, emptyColor)
                drawHalfCircle(x, y, -HALF_PI, fillColor)
            }
            BoxFill.RIGHT -> {
                drawHalfCircle(x, y, PI, emptyColor)
                drawHalfCircle(x, y, 0f, fillColor)
            }
            BoxFill.BOTTOM -> {
                drawHalfCircle(x, y, -HALF_PI, emptyColor)
                drawHalfCircle(x, y, HALF_PI, fillColor)
            }
            BoxFill.FULL -> drawFullCircle(x, y, orange)
        }

    }

    private fun drawFullCircle(x: Float, y: Float, fillColor: Int) {

    }

    private fun drawHalfCircle(x: Float, y: Float, rotation: Float, fillColor: Int) {
        fill(fillColor)
        noStroke()
        pushMatrix()
        translate(x, y)
        when (rotation) {
            PI -> translate(size, size)
            -HALF_PI -> translate(0f, size)
            HALF_PI -> translate(size, 0f)
        }
        rotate(rotation)
        beginShape()
        vertex(0f + size / 2f, 0f)
        bezierVertex(0f + (3 / 4f * size), 0f, 0f + size, 0f + (size / 4), 0f + size, 0f + size / 2f)
        bezierVertex(0f + size, 0f + (3 * size / 4), 0f + (3 * size / 4), 0f + size, 0f + size / 2f, 0f + size)
        endShape(CLOSE)
        popMatrix()
    }

    private fun getRandomBoxFill(): BoxFill {
        return when (Math.random()) {
            in 0f..0.25f -> BoxFill.BOTTOM
            in 0.25f..0.5f -> BoxFill.LEFT
            in 0.5f..0.75f -> BoxFill.TOP
            in 0.75f..1.0f -> BoxFill.RIGHT
            else -> BoxFill.FULL
        }
    }

    private fun drawBox(i: Int, j: Int, boxFill: BoxFill = BoxFill.FULL) {
        val x1 = i * size
        val y1 = j * size
//        val x1 = i * size
//        val y1 = j * size
        noStroke()
        when (boxFill) {
            BoxFill.NONE -> {
                fill(grey3)
                rect(x1, y1, size, size)
            }
            BoxFill.LEFT -> {
                fill(orange)
                rect(x1, y1, size / 2f, size)
                fill(grey3)
                rect(x1 + size / 2f, y1, size / 2f, size)
            }
            BoxFill.TOP -> {
                fill(orange)
                rect(x1, y1, size, size / 2f)
                fill(grey3)
                rect(x1, y1 + size / 2f, size, size / 2f)
            }
            BoxFill.RIGHT -> {
                fill(grey3)
                rect(x1, y1, size / 2f, size)
                fill(orange)
                rect(x1 + size / 2f, y1, size / 2f, size)
            }
            BoxFill.BOTTOM -> {
                fill(grey3)
                rect(x1, y1, size, size / 2f)
                fill(orange)
                rect(x1, y1 + size / 2f, size, size / 2f)
            }
            BoxFill.FULL -> {
                fill(orange)
                rect(x1, y1, size, size)
            }
        }
    }

    enum class BoxFill {
        NONE,
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        FULL;

        fun opposite(): BoxFill {
            return when (this) {
                NONE -> FULL
                LEFT -> RIGHT
                TOP -> BOTTOM
                RIGHT -> LEFT
                BOTTOM -> TOP
                FULL -> NONE
            }
        }
    }
}
