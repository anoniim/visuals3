package sketch.gameoflife

import BaseSketch
import Screen
import kotlin.random.Random

private typealias WorldState = IntArray

class GameOfLife : BaseSketch(Screen(1600, 900), renderer = P2D) {

    private val cellSize = 35f
    private val numOfRows = ceil(screen.heightF / cellSize).also { println(it) }
    private val numOfCols = ceil(screen.widthF / cellSize).also { println(it) }
    private val dead = 0
    private val alive = 1

    private var worldState = createWorldState { Random.nextInt(-0, 2) }
//    private var worldState = createWorldState { 0 }

    override fun settings() {
        super.settings()
        smooth(8)
    }

    override fun setup() {
        frameRate(20f)
        background(grey3)
    }

    override fun draw() {
        fill(grey3, 55f)
        rect(0f, 0f, screen.widthF, screen.heightF)
        drawState()
        if (frameCount % 15 != 0) { // config 2-15
            return
        }
        val newState = worldState.clone()
        for (i in 0 until numOfRows * numOfCols) {
            val x = i % numOfCols
            val y = i / numOfCols
            val numOfNeighbors = countNeighbors(x, y)
            val cellValue = worldState[i]
            val isAlive = cellValue.isAlive()
            when {
                isAlive && numOfNeighbors < 2 -> newState[i] = dead
                isAlive && numOfNeighbors > 3 -> newState[i] = dead
                !isAlive && numOfNeighbors == 3 -> newState[i] = alive
//                else -> newState[i] = cellValue
            }
        }
        worldState = newState
//        background(gre3)
//        drawState()
    }

    private fun drawState() {
        for (i in 0 until numOfRows * numOfCols) {
            val x = i % numOfCols
            val y = i / numOfCols
            val cellValue = worldState[i]
            draw(x, y, cellValue)
        }
    }

    private fun countNeighbors(x: Int, y: Int): Int {
        var count = 0
        for (xn in -1..1) {
            for (yn in -1..1) {
                if (xn == 0 && yn == 0) continue
                var adjustedX = x + xn
                var adjustedY = y + yn
                if (adjustedX < 0) {
                    adjustedX = numOfCols - 1
                }
                if (adjustedX == numOfCols) {
                    adjustedX = 0
                }
                if (adjustedY < 0) {
                    adjustedY = numOfRows - 1
                }
                if (adjustedY == numOfRows) {
                    adjustedY = 0
                }
                val index = adjustedY * numOfCols + adjustedX
                count += if (worldState[index].isAlive()) 1 else 0
            }
        }
        return count
    }

    private fun Int.isAlive(): Boolean {
        return this > 0
    }

    private fun draw(x: Int, y: Int, value: Int) {
        val cellX = x * cellSize
        val cellY = y * cellSize
        // config
//        drawRectangles(value, cellX, cellY)
        drawWaves(value, cellX, cellY)
    }

    private fun drawWaves(value: Int, cellX: Float, cellY: Float) {
        noFill()
        stroke(grey11, 150f)
        strokeWeight(1.5f)
        if (value > 0) drawAlive(cellX, cellY) else drawDead(cellX, cellY)
    }

    private fun drawRectangles(value: Int, cellX: Float, cellY: Float) {
        noStroke()
        val color = if (value.isAlive()) grey11 else grey3
        val alpha = 250f // 150f config
        fill(color, alpha)
        rect(cellX, cellY, cellX + cellSize, cellY + cellSize)
    }

    private fun drawAlive(x: Float, y: Float) {
        arc(x, y, cellSize, cellSize, 0f, HALF_PI)
        arc(x + cellSize, y + cellSize, cellSize, cellSize, PI, PI + HALF_PI)
    }

    private fun drawDead(x: Float, y: Float) {
        arc(x + cellSize, y, cellSize, cellSize, HALF_PI, PI)
        arc(x, y + cellSize, cellSize, cellSize, PI + HALF_PI, TWO_PI)
    }

    private fun createWorldState(value: () -> Int) = Array(numOfRows * numOfCols) { value() }
}