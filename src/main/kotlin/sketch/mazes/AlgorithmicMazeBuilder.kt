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
class AlgorithmicMazeBuilder : BaseSketch() {

    // config
    private val stepSize = 20f

    private val numOfCols by lazy { ceil(width / stepSize) }
    private val numOfRows by lazy { ceil(height / stepSize) }
    private val visited: MutableList<Position> = mutableListOf()
    private val exploreStack: MutableList<Position> = mutableListOf()

    override fun setup() {
        frameRate(20f)
        val start = Position(numOfCols / 2, numOfRows / 2)
        visited.add(start)
        exploreStack.add(start)
    }

    override fun draw() {
        background(grey3)
        translate(stepSize / 2f, stepSize / 2f)


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
            visited.forEach {
                if (it == this) {
                    alreadyIncluded = true
                }
            }
            return alreadyIncluded
        }
    }
}
