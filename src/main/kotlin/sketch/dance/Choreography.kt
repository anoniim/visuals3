package sketch.dance

import BaseSketch
import processing.core.PVector
import shapes.Shape
import util.Interpolation
import util.translateToCenter

abstract class Choreography : BaseSketch() {

    // config
    private val testMode = false

    protected abstract val shapes: List<ShapeUnit>

    protected var globalScale: Float = 1f
    protected var globalRotation: Float = 0f

    private val moves = mutableListOf<Move>()

    override fun draw() {
        background(grey3)

        drawFrameCount()
        if (testMode) text(mouseXF.toString(), 20f, 70f)

        translateToCenter()
        rotate(-HALF_PI)
        scale(globalScale)

        if (!testMode) executeMoves() else test()

        drawShapes()
    }

    private fun test() {
        shapes.forEach {
            // mouse control
            it.update {
                rotation = PI
                radius = mouseXF
            }
        }
        if (mouseXF < 100) {
            scale(1 + (100 - mouseXF) / 300f)
        }
    }

    private fun executeMoves() {
        moves.forEach {
            if (frameCount >= it.startFrame && frameCount <= it.startFrame + it.moveLength) {
                it.action(it)
            }
        }
//        moves.removeIf { frameCount > it.startFrame + it.moveLength } // XXX Probably not a great performance improvement
    }

    private fun drawShapes() {
        shapes.forEach {
            it.draw()
        }
    }

    private fun drawFrameCount() {
        textSize(30f)
        fill(grey7)
        text(frameCount.toString(), 20f, 40f)
    }

    protected inner class ShapeUnit(
        val index: Int,
        var shape: Shape,
        var initialAngle: Float = 0f,
        var radius: Float = 0f
    ) {

        var angle: Float = 0f
        var rotation: Float = 0f
        val position: PVector
            get() {
                val currentAngle = initialAngle + angle
                val x = radius * cos(currentAngle)
                val y = radius * sin(currentAngle)
                return PVector(x, y)
            }

        fun update(updateFun: ShapeUnit.() -> Unit) {
            updateFun(this)
        }

        fun draw() {
            pushMatrix()
            translate(position.x, position.y)
            rotate(initialAngle + angle + globalRotation + rotation)
            shape.draw()
            popMatrix()
        }
    }

    protected fun start(forFrames: Float, action: Move.() -> Unit): Move {
        val move = Move(0f, forFrames, action)
        moves.add(move)
        return move
    }

    protected fun Move.then(forFrames: Float, action: Move.() -> Unit): Move {
        val thenMove = Move(this.startFrame + this.moveLength, forFrames, action)
        moves.add(thenMove)
        return thenMove
    }

    protected fun Move.thenPause(forFrames: Float): Move {
        return then(forFrames) {}
    }

    protected inner class Move(val startFrame: Float, val moveLength: Float, val action: Move.() -> Unit) {
        private val moveFrameCount: Float
            get() = frameCountF - startFrame

        private val interpolation = Interpolation(moveLength)

        fun lerp(from: Float, to: Float): Float {
            return map(moveFrameCount, 0f, moveLength, from, to)
        }

        fun smerp(from: Float, to: Float): Float {
            return interpolation.interpolate(from, to, moveFrameCount)
        }
    }

    protected fun List<ShapeUnit>.forEvery(nth: Int = 1, start: Int = 0, function: ShapeUnit.() -> Unit) {
        if (nth == 1) {
            forEach {
                it.update { function() }
            }
        } else {
            drop(start).filterIndexed { i, _ ->
                i % nth == 0
            }.forEach {
                it.update { function() }
            }
        }
    }

    protected fun cycle(times: Float = 1f) = times * TWO_PI * 100
}

