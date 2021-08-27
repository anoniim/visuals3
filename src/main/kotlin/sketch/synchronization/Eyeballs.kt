package sketch.synchronization

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import util.*

fun main() = PApplet.main(Eyeballs::class.java)

class Eyeballs : BaseSketch() {

    private val speed = 0.05f
    private val straightSpeed = 0.5f
    private val radius = 15f
    private val rowCount = 58
    private val columnCount = 98

    private val outerDrawModif = {
        noStroke()
//        fill(white)
        noFill()
    }

    private val innerDrawModif = {
        noStroke()
        fill(red)
    }

    private val eyes = generateEyeballGrid()

    private fun generateEyeballGrid(): List<Part> {
        val eyes = mutableListOf<Part>()
        val yOffset = -(rowCount * radius * 2) / 2
        repeat(rowCount) {
            val direction = if (it % 2 == 0) 1f else -1f
            eyes.addAll(
                generateEyeballRow(
                    columnCount,
                    yOffset + it * radius * 2,
                    speed,
                    direction
                )
            )
        }
        return eyes
    }

    private fun generateEyeballRow(count: Int, yOffset: Float, speed: Float, direction: Float): List<Part> {
        val xOffset = -(count * radius * 2) / 2
        return List(count) {
            val phase = (it * PI) % TWO_PI
            generateEyeball(PVector(xOffset + radius + it * (radius * 2), yOffset), phase, speed, direction)
        }
    }

    private fun generateEyeball(position: PVector, phase: Float, speed: Float, direction: Float): Part {
        val inner = Core(
            position = PVector(radius / 2f, 0f),
            radius / 2f,
            innerDrawModif,
            phase = phase,
            speed = speed,
            direction = direction
        )
        return Shell(position, radius, outerDrawModif, inner)
    }

    override fun draw() {
        translateToCenter()
        background(grey3)

        eyes.forEach {
            it.update()
            it.draw()
        }
    }

    private abstract inner class Part(
        var position: PVector,
        val radius: Float,
        val drawModifiers: () -> Unit,
    ) {
        open fun update() {
            // no default update logic
        }

        open fun draw() {
            drawModifiers()
            circle(position, radius * 2)
        }
    }

    private inner class Core(
        position: PVector,
        radius: Float,
        drawModifiers: () -> Unit,
        val phase: Float = 0f,
        val speed: Float = 0f,
        val direction: Float = 1f,
    ) : Part(position, radius, drawModifiers) {

        private var xMovement: Float = 0f
        private var angle = phase
        private var moveMode = Mode.CIRCLE

        override fun update() {
            changeModeIfNeeded()
            when (moveMode) {
                Mode.CIRCLE -> moveInCircle()
                Mode.STRAIGHT -> moveInStraightLine()
            }
            position = (PVector.fromAngle(angle) * radius) + PVector(xMovement, 0f)
        }

        private fun changeModeIfNeeded() {
            if (changeMode) {
                moveMode = moveMode.change()
            }
        }

        private fun moveInStraightLine() {
            xMovement += direction * straightSpeed
        }

        private fun moveInCircle() {
            angle += direction * speed
        }

        private val changeMode: Boolean
            get() {
                val circleMoveAnimationLength = round(PI + HALF_PI / speed)
                val lineMoveAnimationLength = ceil((4*radius) / straightSpeed)
                return when (moveMode) {
                    Mode.CIRCLE -> {
                        frameCount % (3 * circleMoveAnimationLength) == 0
                    }
                    Mode.STRAIGHT -> {
                        (frameCount - circleMoveAnimationLength) % lineMoveAnimationLength == 0
                    }
                }
            }
    }

    private inner class Shell(
        position: PVector,
        radius: Float,
        drawModifiers: () -> Unit,
        val inner: Part? = null,
    ) : Part(position, radius, drawModifiers) {

        override fun draw() {
            super.draw()
            drawInner()
        }

        private fun drawInner() {
            pushMatrix()
            translate(position)
            inner?.update()
            inner?.draw()
            popMatrix()
        }
    }

    private enum class Mode {
        CIRCLE,
        STRAIGHT;

        fun change(): Mode {
            return values()[(ordinal + 1) % values().size]
        }
    }
}