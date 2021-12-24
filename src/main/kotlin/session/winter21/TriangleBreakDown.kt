package session.winter21

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import show.first.MidiController
import util.triangle
import kotlin.random.Random

fun main() {
    PApplet.main(TriangleBreakDown::class.java)
}

class TriangleBreakDown : BaseSketch(renderer = P2D) {

    // config
    private val minAlpha = 20f
    private val fadeOutSpeed = 1f
    private val squareSize = 40f
    private val neighborJumpMaxDelay = 10
    private val maxNeighborJumpAttempts = 10

    private val numOfRows by lazy { ceil(heightF / squareSize) }
    private val numOfCols by lazy { ceil(widthF / squareSize) }
    private val triangles by lazy { generateGrid() }
    private val midiController by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        connectNeighbors()
        midiController.on(MidiController.PAD_1..MidiController.PAD_16, { _, _ ->
            startNewTrail()
        })
    }

    override fun draw() {
        background(grey3)

        // debug
//        val trianglesToShow = listOf(
//            triangles[numOfCols * 2 + 1],
//            triangles[numOfCols * 2 + 5],
//            triangles[numOfCols * 2 + 11],
//            triangles[numOfCols * 2 + 19],
//        )
//        trianglesToShow.forEach { it.showNeighbors() }

        triangles.forEach {
            it.draw()
            it.update()
        }
    }

    override fun keyTyped() {
        if (key == ' ') {
            startNewTrail()
        }
    }

    private fun startNewTrail() {
        val haventBeenActive = triangles.filter { it.state == State.INITIAL }
        if (haventBeenActive.size > triangles.size / 10) {
            haventBeenActive.random().activate(0)
        } else {
            triangles.forEach { it.deactivate() }
        }
    }

    private fun connectNeighbors() {
        val itemsInRow = numOfCols * 2
        triangles.forEachIndexed { index, it ->
            val indexInRow = index % itemsInRow
            if (index != triangles.lastIndex && indexInRow != itemsInRow - 1) {
                it.addNeighbor(triangles[index + 1])
            }
            if (it.row % 2 == 0 && it.row != 0 && indexInRow % 2 == 0 && indexInRow % 4 != 0) {
                val topNeighborIndex = index - itemsInRow
                it.addNeighbor(triangles[topNeighborIndex])
            }
            if (it.row % 2 == 0 && it.row != 0 && indexInRow % 2 == 1 && indexInRow % 4 != 3) {
                val topNeighborIndex = index - itemsInRow
                it.addNeighbor(triangles[topNeighborIndex])
            }
            if (it.row % 2 == 1 && indexInRow % 2 == 0 && indexInRow % 4 == 0) {
                val topNeighborIndex = index - itemsInRow
                it.addNeighbor(triangles[topNeighborIndex])
            }
            if (it.row % 2 == 1 && indexInRow % 2 == 1 && indexInRow % 4 == 3) {
                val topNeighborIndex = index - itemsInRow
                it.addNeighbor(triangles[topNeighborIndex])
            }
        }
    }

    private fun generateGrid(): List<Triangle> {
        val grid = mutableListOf<Triangle>()
        for (row in 0 until numOfRows) {
            for (col in 0 until numOfCols) {
                val yTop = row * squareSize
                val yBottom = row * squareSize + squareSize
                val xLeft = col * squareSize
                val xRight = col * squareSize + squareSize
                if (row % 2 == 0 && col % 2 == 0) {
                    grid.addAll(addTrianglesInEvenCol(col, row, xLeft, yTop, yBottom, xRight))
                } else if (row % 2 == 0) {
                    grid.addAll(addTrianglesInOddCol(col, row, xLeft, yTop, yBottom, xRight))
                } else if (row % 2 == 1 && col % 2 == 0) {
                    grid.addAll(addTrianglesInOddCol(col, row, xLeft, yTop, yBottom, xRight))
                } else {
                    grid.addAll(addTrianglesInEvenCol(col, row, xLeft, yTop, yBottom, xRight))
                }
            }
        }
        return grid
    }

    private fun addTrianglesInEvenCol(
        col: Int,
        row: Int,
        xLeft: Float,
        yTop: Float,
        yBottom: Float,
        xRight: Float
    ): List<Triangle> {
        return Pair(
            Triangle(
                col = col, row = row,
                a = PVector(xLeft, yTop),
                b = PVector(xLeft, yBottom),
                c = PVector(xRight, yBottom),
            ),
            Triangle(
                col = col, row = row,
                a = PVector(xLeft, yTop),
                b = PVector(xRight, yBottom),
                c = PVector(xRight, yTop),
            )
        ).toList()
    }

    private fun addTrianglesInOddCol(
        col: Int,
        row: Int,
        xLeft: Float,
        yTop: Float,
        yBottom: Float,
        xRight: Float
    ): List<Triangle> {
        return Pair(
            Triangle(
                col = col, row = row,
                a = PVector(xLeft, yTop),
                b = PVector(xLeft, yBottom),
                c = PVector(xRight, yTop),
            ),
            Triangle(
                col = col, row = row,
                a = PVector(xLeft, yBottom),
                b = PVector(xRight, yBottom),
                c = PVector(xRight, yTop),
            )
        ).toList()
    }

    private inner class Triangle(
        val col: Int,
        val row: Int,
        val a: PVector,
        val b: PVector,
        val c: PVector
    ) {

        private var fillColor: Int? = null
        private var alpha: Float = 250f
        var state = State.INITIAL
        private var timeToNeighbor: Int = 0
        val neighbors = mutableListOf<Triangle>()
        private lateinit var colorCollection: List<Int>

        fun showNeighbors() {
            // for debug purposes
            fillColor = red
            neighbors.forEach {
                it.fillColor = orange
            }
        }

        fun activate(
            jumpToNeighborIn: Int = Random.nextInt(neighborJumpMaxDelay),
            fromCollection: List<Int> = colors.collections.random()
        ) {
            state = State.ACTIVE
            timeToNeighbor = getNeighborJumpDelay(jumpToNeighborIn)
            colorCollection = fromCollection
            fillColor = colorCollection.random()
        }

        private fun getNeighborJumpDelay(jumpToNeighborIn: Int) =
            if (jumpToNeighborIn != 0 && jumpToNeighborIn < neighborJumpMaxDelay / 3) neighborJumpMaxDelay * 6 else jumpToNeighborIn

        fun update() {
            updateAlpha()
            jumpToNeighbor()
        }

        private fun updateAlpha() {
            if (state != State.INITIAL && alpha > minAlpha) alpha -= fadeOutSpeed
            if (state == State.DEACTIVATING) alpha -= fadeOutSpeed
            if (alpha < 0f) {
                state = State.INITIAL
                alpha = 250f
                fillColor = null
            }
        }

        private fun jumpToNeighbor(count: Int = 0) {
            if (count >= maxNeighborJumpAttempts) {
                state = State.HAS_BEEN_ACTIVE
                return
            }
            if (state == State.ACTIVE) {
                timeToNeighbor--
                if (timeToNeighbor < 0f) {
                    activateRandomNeighbor(count)
                    state = State.HAS_BEEN_ACTIVE
                }
            }
        }

        private fun activateRandomNeighbor(count: Int) {
            val randomNeighbor = neighbors.random()
            if (randomNeighbor.state == State.INITIAL)
                randomNeighbor.activate(fromCollection = colorCollection) else jumpToNeighbor(count + 1)
        }

        fun draw() {
            setFillColor(fillColor)
            stroke(grey3)
            strokeWeight(3f)
            triangle(a, b, c)
        }

        private fun setFillColor(fillColor: Int?) {
            if (fillColor != null) {
                fill(fillColor, alpha)
            } else {
                noFill()
            }
        }

        fun addNeighbor(triangle: Triangle) {
            neighbors.add(triangle)
            triangle.neighbors.add(this)
        }

        fun deactivate() {
            state = State.DEACTIVATING
        }
    }

    enum class State {
        INITIAL,
        ACTIVE,
        HAS_BEEN_ACTIVE,
        DEACTIVATING,
    }
}