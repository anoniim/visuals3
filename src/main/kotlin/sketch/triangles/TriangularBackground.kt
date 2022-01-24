package sketch.triangles

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import util.triangle

fun main() {
    PApplet.main(TriangularBackground::class.java)
}

class TriangularBackground : BaseSketch() {

    private val size = 800f

    private val triangleWidth by lazy { sqrt(sq(size) - sq(size/2f)) }
    private val numOfCols by lazy { 2 * round(width / triangleWidth) + 1 }
    private val numOfRows by lazy { round(heightF / size) + 1 }
    private val triangles by lazy { List(numOfRows * numOfCols) {
        val row = it % numOfRows
        val column = it / numOfRows
        val even = column % 2 == 0
        val originX = row * size + if (even) size/2f else 0f
        val originY = column / 2 * triangleWidth
        val direction = if (even) 1 else 0
        Triangle(PVector(originX, originY), direction)
    }}

    override fun draw() {
        background(grey3)

        triangles.forEach {
            it.draw()
        }
    }

    private inner class Triangle(
        val origin: PVector,
        val direction: Int = 0,
        val color: Int = grey11
    ) {

        fun draw() {
//            noStroke()
            stroke(red)
            fill(color)
            val v1 = origin
            val v2 = origin.copy().add(0f, size)
            val v3 = origin.copy().add(PVector.fromAngle(radians(30f + direction * 150f)).mult(size))
            triangle(v1, v2, v3)
        }
    }
}