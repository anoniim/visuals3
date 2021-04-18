package sketch.gameoflife

import BaseSketch
import Screen
import kotlin.random.Random

typealias WorldState = IntArray

class GameOfLife : BaseSketch(Screen(1000, 800)) {

    private val cellSize = 5f
    private val numOfRows = ceil(screen.heightF / cellSize).also { println(it) }
    private val numOfCols = ceil(screen.widthF / cellSize).also { println(it) }
    private val dead = 0
    private val alive = 1

    private var worldState = createWorldState { Random.nextInt(-0, 2) }
//    private var worldState = createWorldState { 0 }

    override fun setup() {
//        frameRate(1f)
        background(grey3)
        noStroke()
    }

    override fun draw() {
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
            draw(x, y, cellValue)
        }
        worldState = newState
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
        val color = if (value > 0) grey11 else grey3
        fill(color)
        val cellX = x * cellSize
        val cellY = y * cellSize
        rect(cellX, cellY, cellX + cellSize, cellY + cellSize)
    }

    private fun createWorldState(value: () -> Int) = Array(numOfRows * numOfCols) { value() }
}