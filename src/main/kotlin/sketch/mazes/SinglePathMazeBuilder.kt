package sketch.mazes

import BaseSketch
import processing.core.PVector
import util.line
import util.point
import util.wtf
import kotlin.random.Random

/**
 * The idea was to create an algorithm that fills the whole screen with a single-path maze
 * (i.e. traverses grid-like graph and visits every node just once).
 * This turned out to be a difficult problem, comparable to Chinese Postman problem which is NP-hard.
 *
 * Another attempt was to do it brute force and employ [EdgeFinder]s that verify whether a new
 * position candidate creates a loop (which would prevent filling the maze completely).
 * This is not complete, the algorithm doesn't back off correctly if a loop is detected and it
 * doesn't take screen edges into account. Ideally, it should prevent touching the screen edge
 * where it would create an empty 'pocket' along one edge. Two edges would be fine, as that would
 * effectively just cut off a corner.
 *
 * In the end, I resorted to drawing the path manually, which is somewhat possible on lower resolution (high [stepSize]).
 * When the path is drawn, store it by pressing 's'. The file will get stored in 'data/input/maze_path/'.
 * Then, enable [loadPathFromFile] in [setup] to replay the path creation.
 */
class SinglePathMazeBuilder : BaseSketch() {

    // config
    private val stepSize = 60f

    private val numOfCols: Int by lazy { ceil(width / stepSize) }
    private val numOfRows: Int by lazy { ceil(height / stepSize) }
    private val path = mutableListOf(WalkerPosition(0, 0))
    private var pointer = WalkerPosition(0, 0)
    private var deadEndPath = mutableListOf(Position(0, 0))
    private var pause = true
    private lateinit var pathFromStrings: MutableList<WalkerPosition>

    override fun setup() {
        frameRate(20f)
//        loadPathFromFile() // config
    }

    override fun draw() {
        background(grey3)
        strokeWeight(2 * stepSize / 3f)
        translate(stepSize / 2f, stepSize / 2f)

        if (this::pathFromStrings.isInitialized) {
            if (pathFromStrings.size > 0) {
                addToPath(pathFromStrings.first())
                pathFromStrings.removeFirst()
            } else {
                noLoop()
            }
        }

        drawPath()

        if (!pause) pointer.explore()
    }

    override fun keyPressed() {
        when (key) {
            ' ' -> resumeSketch()
            's' -> savePath()
            'd' -> removeLast()
        }
    }

    override fun mouseDragged() {
        val k = floor(mouseX / stepSize)
        val l = floor(mouseY / stepSize)
        addToPath(WalkerPosition(k, l))
    }

    private fun resumeSketch() {
        pause = false
        loop()
    }

    private fun savePath() {
        val pathInStrings: Array<String> = path.map {
            "${it.k},${it.l}"
        }.toTypedArray()
        saveStrings("data/input/maze_path/lastPath.txt", pathInStrings)
    }

    private fun loadPathFromFile() {
        pathFromStrings = loadStrings("data/input/maze_path/1.txt").map {
            val klPair = it.split(',').map(Integer::parseInt)
            WalkerPosition(klPair[0], klPair[1])
        }.toMutableList()
    }

    private fun addToPath(position: WalkerPosition) {
        pointer = position
        if (!path.contains(pointer)) path.add(pointer)
    }

    private fun removeLast() {
        path.remove(pointer)
        pointer = path.last()
    }

    private fun drawPath() {
        stroke(grey11)
        path.forEachIndexed { i, current ->
            if (i < path.size - 1) {
                val next = path[i + 1]
                line(current, next)
            }
        }
    }

    private open inner class Position(val k: Int, val l: Int) : PVector(k * stepSize, l * stepSize) {

        fun up() = Position(k, l - 1)
        fun down() = Position(k, l + 1)
        fun right() = Position(k + 1, l)
        fun left() = Position(k - 1, l)
        fun upRight() = Position(k + 1, l - 1)
        fun downLeft() = Position(k - 1, l + 1)
        fun rightDown() = Position(k + 1, l + 1)
        fun leftUp() = Position(k - 1, l - 1)

        fun generateNewPosition(): Position {
            return when (Random.nextInt(4)) {
                0 -> up()
                1 -> down()
                2 -> right()
                3 -> left()
                else -> wtf()
            }
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

        fun isEdge(): Boolean {
            return k !in 0 until numOfCols ||
                    l !in 0 until numOfRows
        }

        fun getNeighbors(): List<Position> {
            return listOf(up(), upRight(), right(), rightDown(), down(), downLeft(), left(), leftUp())
        }

        fun sidesAround(neighbor: Position): List<Position> {
            println("this: [$k, $l]")
            val dk = neighbor.k - k
            val dl = neighbor.l - l
            println("neighbor: [$dk, $dl]")
            val clockwise = PVector(dk.toFloat(), dl.toFloat()).rotate(QUARTER_PI)
            val counterClockwise = PVector(dk.toFloat(), dl.toFloat()).rotate(-QUARTER_PI)
            println("- clockwise: [${round(clockwise.x)}, ${round(clockwise.y)}]")
            println("--> ${k + round(clockwise.x)}, ${l + round(clockwise.y)}")
            println("- counterClockwise: [${round(counterClockwise.x)}, ${round(counterClockwise.y)}]")
            println("--> ${k + round(counterClockwise.x)}, ${l + round(counterClockwise.y)}")
            println()
            return listOf(
                Position(k + round(clockwise.x), l + round(clockwise.y)),
                Position(k + round(counterClockwise.x), l + round(counterClockwise.y))
            )
        }
    }

    private inner class WalkerPosition(k: Int, l: Int) : Position(k, l) {

        private var checkingLoops: Boolean = false

        constructor(position: Position) : this(position.k, position.l)

        fun explore(attempts: Int = 0) {
            if (checkingLoops) return
            val newPosition = WalkerPosition(generateNewPosition())
            when {
                newPosition.isValid() && !createsLoop(newPosition) -> addNewPosition(newPosition)
                attempts < 20 -> explore(attempts + 1)
                else -> tryAlternative()
            }
        }

        private fun isValid(): Boolean {
            return !isEdge() &&
                    !path.contains(this) &&
                    !deadEndPath.contains(this)
        }

        private fun addNewPosition(newPosition: WalkerPosition): Position {
            deadEndPath.clear()
            path.add(newPosition)
            pointer = newPosition
            return newPosition
        }

        private fun createsLoop(newPosition: WalkerPosition): Boolean {
            if (path.size < 2) return false
            drawNewPosition(newPosition)
            var createsLoopWithAnyNeighbor = false
            newPosition.getNeighbors().forEach { neighbor ->
                if (path.dropLast(5).contains(neighbor)) {
                    if (sendEdgeFinder(newPosition, neighbor)) createsLoopWithAnyNeighbor = true
                }
            }
            return createsLoopWithAnyNeighbor
        }

        private fun drawNewPosition(newPosition: WalkerPosition) {
            stroke(grey5)
            point(newPosition)
        }

        private fun sendEdgeFinder(newPosition: WalkerPosition, neighbor: Position): Boolean {
            var createsLoop = false
            newPosition.sidesAround(neighbor).forEach {
                if (!path.contains(it)) {
                    if (EdgeFinder(it).createsLoop()) {
                        createsLoop = true
                        deadEndPath.add(newPosition)
                    }
                }
            }
            return createsLoop
        }

        private fun tryAlternative(): Position {
            deadEndPath.add(pointer)
            path.remove(pointer)
            if (path.size > 0) {
                pointer = path.last()
            } else {
                drawPath()
                noLoop()
            }
            return pointer
        }
    }

    private inner class EdgeFinder(k: Int, l: Int) {

        constructor(position: Position) : this(position.k, position.l)

        var foundEdge: Boolean = false
        var finderPointer = Position(k, l)
        private var counter = 0

        fun createsLoop(): Boolean {
            draw()
            noLoop()
            while (counter < 1000) {
                findNextPosition()
                if (foundEdge) return false
            }
            return true
        }

        private fun findNextPosition(attempts: Int = 0) {
            if (attempts > 10) {
                foundEdge = false
                // TODO there's something broken here, when EdgeFinger can't find a new position, it loops here
                println("Edge finder can't find another position")
                return
            }
            val newPosition = finderPointer.generateNewPosition()
            when {
                path.contains(newPosition) -> findNextPosition(attempts + 1)
                newPosition.isEdge() -> foundEdge = true
                else -> {
                    finderPointer = newPosition
                    counter++
                }
            }
        }

        fun draw() {
            stroke(red)
            point(finderPointer)
        }
    }
}
