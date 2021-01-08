package spirals

import BaseSketch
import Screen

class SquareSpiral : BaseSketch(Screen(400, 400, fullscreen = false)) {

    private val step = 10f
    private val strokeWeight = 5f
    private val lines by lazy {
        listOf(
        // 1 Pyramid
//            Line(step, step, Direction.RIGHT),
//            Line(width - step, step, Direction.DOWN),
//            Line(width - step, height - step, Direction.LEFT),
//            Line(step, height - step, Direction.UP)
        // 4 Pyramids
            Line(width/2 - step/2, height/2 - step/2, Direction.LEFT),
            Line(width/2 - step/2, height/2 + step/2, Direction.LEFT),
            Line(width/2 + step/2, height/2 - step/2, Direction.RIGHT),
            Line(width/2 + step/2, height/2 + step/2, Direction.RIGHT)
        )
    }

    override fun setup() {
        frameRate(20f)
        noFill()
        stroke(grey11)
        strokeWeight(strokeWeight)
    }

    override fun draw() {
        background(grey3)
        for (line in lines) {
            line.update()
            line.show()
        }
    }

    inner class Line(
        private val startX: Float,
        private val startY: Float,
        private var direction: Direction) {

        private var turnCount: Int = 0
        private var path = mutableListOf(Pair(startX, startY))
        private var negative: Boolean = false
        private var originalDirection = direction

        fun update() {
            if (negative) {
                if (path.isNotEmpty()) {
                    path = path.drop(1).toMutableList()
                    return
                } else {
                    negative = !negative
                    reset()
                }
            }
            val current = path.last()
            when (direction) {
                Direction.RIGHT -> {
                    val newX = current.first + step
                    val target = Pair(newX, current.second)
                    if (newX < width && !isCollision(target)) {
                        setNewTarget(target)
                    } else {
                        turn()
                    }
                }
                Direction.LEFT -> {
                    val newX = current.first - step
                    val target = Pair(newX, current.second)
                    if (newX > 0f && !isCollision(target)) {
                        setNewTarget(target)
                    } else {
                        turn()
                    }
                }
                Direction.UP -> {
                    val newY = current.second - step
                    val target = Pair(current.first, newY)
                    if (newY > 0f && !isCollision(target)) {
                        setNewTarget(target)
                    } else {
                        turn()
                    }
                }
                Direction.DOWN -> {
                    val newY = current.second + step
                    val target = Pair(current.first, newY)
                    if (newY < height && !isCollision(target)) {
                        setNewTarget(target)
                    } else {
                        turn()
                    }
                }
            }
        }

        private fun reset() {
            path.add(Pair(startX, startY))
            direction = originalDirection
        }

        private fun setNewTarget(target: Pair<Float, Float>) {
            path.add(target)
            turnCount = 0
        }

        private fun turn() {
            turnCount++
            if (turnCount >= 4) {
                // Nowhere to go
                onCannotMove()
                return
            }
            direction = when (direction) {
                Direction.RIGHT -> Direction.DOWN
                Direction.LEFT -> Direction.UP
                Direction.UP -> Direction.RIGHT
                Direction.DOWN -> Direction.LEFT
            }
            if (turnCount == 2) {
                // do one more turn (not to go backwards)
                turn()
                return
            }
            update()
        }

        private fun onCannotMove() {
            negative = true
        }

        private fun isCollision(target: Pair<Float, Float>): Boolean {
            for (line in lines) {
                if (line.path.contains(target)) {
                    return true
                }
            }
            return false
        }

        fun show() {
            for (index in 0 until path.lastIndex) {
                val start = path.elementAt(index)
                val target = path.elementAt(index + 1)
                line(start.first, start.second, target.first, target.second)
            }
        }
    }

    enum class Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;
    }
}