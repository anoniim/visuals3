package sketch.walkers

import BaseSketch
import Screen
import processing.core.PVector
import kotlin.math.sign
import kotlin.random.Random


class GrowingTreeBranch : BaseSketch(Screen(1200, 800), longClickClear = true) {

    private val maxThickness = 15f
    private val maxAge = 5000f
    private var tree: Tree = Tree(PVector(0f, 0f), 0f)
    private var time = 0

    override fun setup() {
//        frameRate(1f)
    }

    override fun draw() {
        translate(screen.centerX, screen.heightF)
        rotate(PI + HALF_PI)
        background(grey3)
        tree.update()
        tree.draw()
        time += 1
//        noLoop()

        drawLongPressOverlay {
            rotate((HALF_PI))
            translate(-screen.centerX, -screen.heightF)
        }
    }

    override fun reset() {
        super.reset()
        tree = Tree(PVector(0f, 0f), 0f)
        time = 0
        noiseSeed(Random.nextLong())
    }

    inner class Tree(
        val startingPoint: PVector,
        var growingDirection: Float,
        var length: Float = 5f
    ) {
        private val branches = mutableListOf<Tree>()
        private val growth = mutableListOf(Segment(startingPoint, growingDirection, length))
        private var age = 70f
        private var previousGrowingDirection = 1f

        fun update() {
            if (time % floor(pow(2f, age / 20)) == 0) {
                grow()
                growBranch()
            }
            branches.forEach { it.update() }
            growth.forEach { it.update() }
        }

        private fun grow() {
            val last = growth.last()
//            val directionVariation = (noise(startingPoint.x + age / 200f) - 0.5f) / 8f
            val directionVariation = random(-1f, 1f) / 4f
            growth.add(
                Segment(
                    last.endPoint,
                    last.growingDirection + directionVariation,
                    last.length
                )
            )
            age++
        }

        private fun growBranch() {
            val growChance = map(age, 1f, maxAge, 0.96f, 0.99f)
            if (random(1f) > growChance) {
                val last = growth.last()
                val lastGrowingDirection = last.growingDirection
                val newGrowingDirection = if(random(1f) > 0.7) {
                    if(lastGrowingDirection > 0) {
                        lastGrowingDirection - QUARTER_PI
                    } else {
                        lastGrowingDirection + QUARTER_PI
                    }
                } else {
                    previousGrowingDirection *= -1
                    lastGrowingDirection + sign(previousGrowingDirection) * QUARTER_PI
                }
                branches.add(Tree(
                    last.startingPoint,
                    newGrowingDirection,
                    last.length))
            }
        }

        fun draw() {
            growth.forEach { it.draw() }
            branches.forEach { it.draw() }
        }

    }

    private inner class Segment(
        val startingPoint: PVector,
        val growingDirection: Float,
        var length: Float
    ) {

        val endPoint: PVector = startingPoint.copy().add(PVector.fromAngle(growingDirection).setMag(length))
        private var age = 1f

        fun draw() {
            strokeWeight(constrain(map(age, 1f, maxAge, 1f, maxThickness), 1f, maxThickness))
            stroke(darkGreen)
            line(startingPoint.x, startingPoint.y, endPoint.x, endPoint.y)
        }

        fun update() {
            age++
        }
    }
}