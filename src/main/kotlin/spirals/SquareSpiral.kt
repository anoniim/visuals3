package spirals

import BaseSketch
import Screen
import processing.core.PVector

@ExperimentalStdlibApi
class SquareSpiral : BaseSketch(Screen(400, 400, fullscreen = false)) {

    private val step = 10f
    private val strokeWeight = 5f
    private val lines by lazy {
        listOf(
        // 1 line
//            Line(step, step, Direction.RIGHT)
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
    private val obstacles by lazy {
        emptyList<PVector>()
//        listOf(
//            PVector(width - 11 * step, step),
//            PVector(width - step, height - 11f * step),
//            PVector(0f + 11 * step, height - step),
//            PVector(0f + step, 0f + 12 * step)
//        )
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
        for (obstacle in obstacles) {
            point(obstacle.x, obstacle.y)
        }
    }

    @ExperimentalStdlibApi
    inner class Line(
        private val startX: Float,
        private val startY: Float,
        private var direction: Direction) {

        private var turnCount: Int = 0
        private var path = mutableListOf(PVector(startX, startY))
        private var negative: Boolean = false
        private var hasTriedToReturn: Boolean = false
        private var originalDirection = direction
        private var returnDirections = mutableListOf<Direction>()

        fun update() {
            if (negative) {
                if (removeFromTail()) return
            }

            val current = path.last().copy()
//            if (!hasTriedToReturn && returnDirections.isNotEmpty()) {
//                // try to return
//                hasTriedToReturn = true
//                val target = current.add(returnDirections.first().vector.mult(step))
//                if (!target.isOutOfBounds() && !target.isCollision()) {
//                    if (turnCount == 0 && returnDirections.isNotEmpty()) {
//                        direction = returnDirections.first()
//                        returnDirections.removeFirst()
//                    }
//                    setNewTarget(target)
//                }
//            } else {
                // go straight
                val target = current.add(direction.vector.mult(step))
                if (!target.isOutOfBounds() && !target.isCollision()) {
                    setReturnDirections()
                    setNewTarget(target)
                } else {
                    turn()
                }
//            }
        }

        private fun removeFromTail(): Boolean {
            return if (path.isNotEmpty()) {
                path = path.drop(1).toMutableList()
                true
            } else {
                negative = !negative
                reset()
                false
            }
        }

        private fun reset() {
            path.add(PVector(startX, startY))
            direction = originalDirection
        }

        private fun setNewTarget(target: PVector) {
            path.add(target)
            turnCount = 0
            hasTriedToReturn = false
        }

        private fun turn() {
            turnCount++
            if (cannotMove()) return
            direction = direction.turnRight()
            if (goesBackwards()) return
//            update()
        }

        private fun setReturnDirections() {
            if (turnCount == 1) {
                returnDirections = mutableListOf(direction.turnLeft(), direction.turnLeft().turnLeft())
            } else if (turnCount == 3) {
                returnDirections = mutableListOf(direction.turnRight(), direction.turnRight().turnRight())
            }
        }

        private fun goesBackwards(): Boolean {
            return if (turnCount == 2) {
                // do one more turn (not to go backwards)
                turn()
                true
            } else {
                false
            }
        }

        private fun cannotMove(): Boolean {
            return if (turnCount >= 4) {
                // Nowhere to go
                onCannotMove()
                true
            } else {
                false
            }
        }

        private fun onCannotMove() {
            negative = true
        }

        private fun PVector.isCollision(): Boolean {
            for (obstacle in obstacles) {
                if (obstacle == this) {
                    return true
                }
            }
            for (line in lines) {
                if (line.path.contains(this)) {
                    return true
                }
            }
            return false
        }

        private fun PVector.isOutOfBounds(): Boolean {
            if (this.x < step
                || this.y < step
                || this.x > width - step
                || this.y > height - step) {
                return true
            }
            return false
        }

        fun show() {
            for (index in 0 until path.lastIndex) {
                val start = path.elementAt(index)
                val target = path.elementAt(index + 1)
                line(start.x, start.y, target.x, target.y)
            }
        }
    }

    enum class Direction(_vector: PVector) {
        LEFT(-1f, 0f),
        RIGHT(1f, 0f),
        UP(0f, -1f),
        DOWN(0f, 1f);

        val vector: PVector = _vector
            get() = field.copy()

        constructor(x: Float, y: Float): this(PVector(x, y))

        fun turnRight(): Direction {
            return when (this) {
                RIGHT -> DOWN
                LEFT -> UP
                UP -> RIGHT
                DOWN -> LEFT
            }
        }

        fun turnLeft(): Direction {
            return when (this) {
                RIGHT -> UP
                LEFT -> DOWN
                UP -> LEFT
                DOWN -> RIGHT
            }
        }

        fun opposite(): Direction {
            return when (this) {
                RIGHT -> LEFT
                LEFT -> RIGHT
                UP -> DOWN
                DOWN -> UP
            }
        }
    }
}