package sketch.triangles

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import util.triangle
import kotlin.random.Random
import kotlin.random.nextInt

fun main() {
    PApplet.main(TriangularBackground::class.java)
}

class TriangularBackground : BaseSketch(
//    fullscreen = true,
) {

    private val size = 60f
    private val numOfWalkers = 40

    private val triangleWidth by lazy { sqrt(sq(size) - sq(size / 2f)) }
    private val numOfCols by lazy { 2 * ceil(widthF / triangleWidth) + 1 }
    private val numOfRows by lazy { ceil(heightF / size) + 1 }
    private val numOfTriangles by lazy { numOfRows * numOfCols }
    private val triangles by lazy { List(numOfTriangles) { Triangle(it) } }
    private val selected by lazy {
        MutableList(numOfWalkers) {
            numOfTriangles - 1 - Random.nextInt(numOfCols)
        }
    }

    override fun setup() {
        super.setup()
        selected.forEach {
            triangles[it].activate()
        }
    }

    override fun draw() {
        background(grey3)

        triangles.forEach {
            it.draw()
            it.update()
        }
        activateNew()
    }

    private fun activateNew() {
        if (frameCount % 4 == 0) {
            selected.forEachIndexed { index, it ->
                val newSelected = activateNew(it)
                selected[index] = newSelected
            }
        }
    }

    private fun activateNew(selectedIndex: Int): Int {
        val neighbors = triangles[selectedIndex].neighbors
        return if (neighbors.isNotEmpty()) {
            val random = random(2.2f)
            val randomNeighbor = triangles[neighbors[floor(random) % neighbors.size]]
            randomNeighbor.activate()
            if (randomNeighbor.index > numOfCols) {
                randomNeighbor.index
            } else {
                numOfTriangles - 1 - Random.nextInt(2 * numOfCols)
            }
        } else {
            selectedIndex - 1
        }
    }

    private inner class Triangle(
        val index: Int,
    ) {

        private val column = index % numOfCols
        private val row = index / numOfCols
        private val odd = column % 2 == 1
        private val oddCouple = column % 4 == 2 || column % 4 == 3
        private val originX = column / 2 * triangleWidth + if (odd) triangleWidth else 0f
        private val originY = row * size + (if (odd) -size / 2f else 0f) + (if (oddCouple) -size / 2f else 0f)
        private val direction = if (odd) 1 else 0
        private val origin = PVector(originX, originY)
        val neighbors = calculateNeighbors()

        private var color: Int = color(Random.nextInt(50..150))
        private var alpha: Float = 200f
        private var state = State.NORMAL

        fun activate() {
            state = State.FADE_IN
        }

        fun update() {
            when (state) {
                State.FADE_IN -> fadeIn()
                State.FADE_OUT -> fadeOut()
                else -> {}
            }
        }

        private fun fadeOut() {
            alpha -= 1f
            if (alpha <= 50) state = State.NORMAL
        }

        private fun fadeIn() {
            alpha += 5f
            if (alpha >= 255f) state = State.FADE_OUT
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
            val upNeighbor = if (odd) index - numOfCols - 1 else index + 1
            val downNeighbor = if (odd) index - 1 else index + numOfCols + 1
            val sideNeighbor = if (odd) {
                if (oddCouple) {
                    index - numOfCols + 1
                } else {
                    index + 1
                }
            } else {
                if (oddCouple) {
                    index - 1
                } else {
                    index + numOfCols - 1
                }
            }
            return listOf(upNeighbor, sideNeighbor, downNeighbor).filter { it in 0 until numOfTriangles }
        }
    }

    enum class State {
        FADE_IN,
        FADE_OUT,
        NORMAL
    }
}