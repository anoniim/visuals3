package sketch.walkers

import BaseSketch
import processing.core.PVector
import util.line
import util.point
import util.wtf
import kotlin.random.Random

class Explorer : BaseSketch() {

    private val stepSize = 20f

    private val numOfCols: Int by lazy { ceil(width / stepSize) }
    private val numOfRows: Int by lazy { ceil(height / stepSize) }
    private val path = mutableListOf(Position(0, 0))
    private var pointer = Position(0, 0)
    private var deadEnds = mutableListOf<Position>()
    private var deadEndPath = mutableListOf(Position(0, 0))

    override fun draw() {
        background(grey3)
        translate(stepSize / 2f, stepSize / 2f)

        drawPath()
        drawDeadEnds()
        findNew()
    }

    private fun drawDeadEnds() {
        deadEnds.forEach {
            stroke(grey7)
            point(it)
        }
    }

    private fun findNew(attempts: Int = 0) {
        val newPosition: Position = generateNewPosition()
        when {
            newPosition.isValid() -> addNewPosition(newPosition)
            attempts < 20 -> findNew(attempts + 1)
            else -> tryAlternative()
        }
    }

    private fun tryAlternative() {
        deadEnds.add(pointer)
        deadEndPath.add(pointer)
        path.remove(pointer)
        if (path.size > 0) {
            pointer = path.last()
        } else {
            drawPath()
            noLoop()
        }
    }

    private fun addNewPosition(newPosition: Position) {
        path.add(newPosition)
        pointer = newPosition
    }

    private fun generateNewPosition(): Position {
        return when (Random.nextInt(4)) {
            0 -> pointer.up()
            1 -> pointer.down()
            2 -> pointer.right()
            3 -> pointer.left()
            else -> wtf()
        }
    }

    private fun drawPath() {
        strokeWeight(10f)
        stroke(grey11)
        path.forEachIndexed { i, current ->
            if (i < path.size - 1) {
                val next = path[i + 1]
                line(current, next)
            }
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
                    !path.contains(this) &&
                    !deadEndPath.contains(this)
        }
    }
}
