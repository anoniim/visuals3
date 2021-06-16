package sketch.mazes

import BaseSketch
import processing.core.PVector
import util.line
import util.wtf
import kotlin.random.Random

/**
 * This uses a simple recursive backtracker algorithm but stores path data as a pair of nodes to be able to animate
 * the pass through.
 *
 * TODO make the algorithm less/more likely to turn
 */
class ExplorerMazeBuilder : BaseSketch() {

    // config
    private val stepSize = 20f

    private val numOfCols by lazy { ceil(width / stepSize) }
    private val numOfRows by lazy { ceil(height / stepSize) }
    private val path: MutableList<Pair<Position, Position>> = mutableListOf()
    private val exploreStack: MutableList<Position> = mutableListOf()

    override fun setup() {
        frameRate(20f)
        val start = Position(numOfCols / 2, numOfRows / 2)
        path.add(Pair(start, start))
        exploreStack.add(start)
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
        if (exploreStack.size > 0) {
            exploreStack.removeLast()
        } else {
            drawPath()
            noLoop()
        }
    }

    private fun addNewPosition(newPosition: Position) {
        path.add(Pair(exploreStack.last(), newPosition))
        exploreStack.add(newPosition)
    }

    private fun generateNewPosition(): Position? {
        if (exploreStack.isEmpty()) return null
        val last = exploreStack.last()
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
        exploreStack.forEachIndexed { i, current ->
            if (i < exploreStack.size - 1) {
                val color = getColor(i)
                stroke(color)
                val next = exploreStack[i + 1]
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
