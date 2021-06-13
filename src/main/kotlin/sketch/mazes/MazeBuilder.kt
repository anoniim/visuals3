package sketch.mazes

import BaseSketch
import processing.core.PVector
import util.line
import util.wtf
import kotlin.random.Random

/**
 * TODO make the algorithm less likely to turn
 * TODO Make the algorithm as simple as possible
 * TODO Implement some algorithms from https://en.wikipedia.org/wiki/Maze_generation_algorithm
 */
class MazeBuilder : BaseSketch() {

    private val stepSize = 20f

    private val numOfCols by lazy { ceil(width / stepSize) }
    private val numOfRows by lazy { ceil(height / stepSize) }
    private val path: MutableList<Pair<Position, Position>> = mutableListOf()
    private val exploreQueue: MutableList<Position> = mutableListOf()

    override fun setup() {
        frameRate(20f)
        val start = Position(numOfCols / 2, numOfRows / 2)
        path.add(Pair(start, start))
        exploreQueue.add(start)
    }

    override fun draw() {
        background(grey3)
        translate(stepSize / 2f, stepSize / 2f)

        drawPath()
        drawExplorer()
        findNew()
    }

    private fun findNew(attempts: Int = 0) {
        val newPosition: Position? = generateNewPosition()
        if (newPosition == null) {
            noLoop()
            return
        }
        when {
            newPosition.isValid() -> addNewPosition(newPosition)
            attempts < 20 -> findNew(attempts + 1)
            else -> tryAlternative()
        }
    }

    private fun tryAlternative() {
        if (path.size > 0) {
            exploreQueue.removeLast()
        } else {
            drawPath()
            noLoop()
        }
    }

    private fun addNewPosition(newPosition: Position) {
        path.add(Pair(exploreQueue.last(), newPosition))
        exploreQueue.add(newPosition)
    }

    private fun generateNewPosition(): Position? {
        if (exploreQueue.isEmpty()) return null
        val last = exploreQueue.last()
        return when (Random.nextInt(4)) {
            0 -> last.up()
            1 -> last.down()
            2 -> last.right()
            3 -> last.left()
            else -> wtf()
        }
    }

    private fun drawPath() {
        strokeWeight(10f)
        stroke(grey11)
        path.forEach {
            line(it.first, it.second)
        }
    }

    private fun drawExplorer() {
        strokeWeight(3f)
        exploreQueue.forEachIndexed { i, current ->
            if (i < exploreQueue.size - 1) {
                val color = getColor(i)
                stroke(color)
                val next = exploreQueue[i + 1]
                line(current, next)
            }
        }
    }

    private fun getColor(i: Int): Int {
        return when {
            i <= 400 -> lerpColor(white, yellow, i / 400f)
            i <= 800 -> lerpColor(yellow, pink, (i - 400) / 400f)
            i <= 1200 -> lerpColor(pink, orange, (i - 800) / 400f)
            else -> lerpColor(orange, red, (i - 1200) / 400f)
        }
    }

    private inner class Position(val k: Int, val l: Int) : PVector(k * stepSize, l * stepSize) {

        fun up(): Position {
            return Position(k, l - 1)
        }

        fun down(): Position {
            return Position(k, l + 1)
        }

        fun right(): Position {
            return Position(k + 1, l)
        }

        fun left(): Position {
            return Position(k - 1, l)
        }

        override fun equals(other: Any?): Boolean {
            return other is Position && other.k == k && other.l == l
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + k
            result = 31 * result + l
            return result
        }

        fun isValid(): Boolean {
            return k in 0 until numOfCols &&
                    l in 0 until numOfRows &&
                    !alreadyIncluded()
        }

        private fun alreadyIncluded(): Boolean {
            var alreadyIncluded = false
            path.forEach {
                if (it.first == this || it.second == this) {
                    alreadyIncluded = true
                }
            }
            return alreadyIncluded
        }
    }
}
