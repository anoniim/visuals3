package sketch.walkers

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import util.lerpColors
import util.triangle
import kotlin.random.Random

fun main() {
    PApplet.main(TriangularWalker::class.java)
}

class TriangularWalker : BaseSketch() {

    private val size = 60f

    private val triangleWidth by lazy { sqrt(sq(size) - sq(size/2f)) }
    private val numOfCols by lazy { 2 * ceil(widthF / triangleWidth) + 1 }
    private val numOfRows by lazy { ceil(heightF / size) + 1 }
    private val numOfTriangles by lazy { numOfRows * numOfCols }
    private val triangles by lazy { List(numOfTriangles) { Triangle(it) }}
    private val colorGradient = colors.collections.flatten().toIntArray()
    private var selected: Int = 0
    private var colorProgress: Float = 0f

    override fun setup() {
        super.setup()
        frameRate(36f)

        selected = Random.nextInt(numOfTriangles)
        triangles[selected].activate(lerpColors(colorProgress, *colorGradient))
    }

    override fun draw() {
        background(grey3)

        triangles.forEach {
            it.draw()
            it.update()
        }

        selected = activateNew(selected)
    }

    private fun activateNew(selectedIndex: Int): Int {
        colorProgress = (colorProgress + 0.001f) % 1
        val newColor = lerpColors(colorProgress, *colorGradient)
        val neighbors = triangles[selectedIndex].neighbors
        return if (neighbors.isNotEmpty()) {
            val randomNeighbor = triangles[neighbors.random()]
            randomNeighbor.activate(newColor)
            randomNeighbor.index
        } else {
            selectedIndex - 1
        }
    }

    private inner class Triangle(
        val index: Int,
    ) {

        val column = index % numOfCols
        val row = index / numOfCols
        val odd = column % 2 == 1
        val oddCouple = column % 4 == 2 || column % 4 == 3
        val originX = column / 2 * triangleWidth + if (odd) triangleWidth else 0f
        val originY = row * size + (if (odd) -size/2f else 0f) + (if (oddCouple) -size/2f else 0f)
        val direction = if (odd) 1 else 0
        val origin = PVector(originX, originY)
        val neighbors = calculateNeighbors()

        var color: Int = grey3
        var alpha: Float = 250f
        var activated = false

        fun activate(newColor: Int) {
            activated = true
            color = newColor
            alpha = 255f
        }

        fun update() {
            if (activated) {
                alpha = constrain(alpha - 1f, 0f, 255f)
            }
        }

        fun draw() {
            noStroke()
            fill(color, alpha)
            val v1 = origin
            val v2 = origin.copy().add(0f, size)
            val v3 = origin.copy().add(PVector.fromAngle(radians(30f + direction * 120f)).mult(size))
            triangle(v1, v2, v3)
        }

        private fun calculateNeighbors(): List<Int> {
            val neighbor1 = if (!odd && !oddCouple) index + numOfCols + 1 else index - 1
            val neighbor2 = if (odd && oddCouple) index - numOfCols - 1 else index + 1
            val neighbor3 = if (odd) {
                if (oddCouple) {
                    index - numOfCols + 1
                } else {
                    index - numOfCols - 1
                }
            } else {
                if (oddCouple) {
                    index + numOfCols + 1
                } else {
                    index + numOfCols - 1
                }
            }
            return listOf(neighbor1, neighbor2, neighbor3).filter { it in 0 until numOfTriangles }
        }
    }
}