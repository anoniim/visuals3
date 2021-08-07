package sketch.shapes

import BaseSketch
import shapes.Polygon
import util.Interpolation
import util.translateToCenter

abstract class PolygonDance : BaseSketch() {

    private val circleRadius = 200f
    private val repeatCount = 16
    private val shapeSize = 100f

    abstract val shape: Polygon
    abstract val scaleBaseRadius: Float

    protected var globalScale: Float = 1f
    protected var globalRotation: Float = 0f

    private val moves = mutableListOf<Move>()
    private val angleSegment = TWO_PI / repeatCount
    protected val shapes by lazy {
        List(repeatCount) {
            ShapeUnit(shape, angle = it * angleSegment)
        }
    }

    override fun draw() {
        background(grey3)

        drawFrameCount()
//        text(mouseXF.toString(), 20f, 70f)

        translateToCenter()
        rotate(-HALF_PI)

//        test()
        executeMoves()
        scale(globalScale)
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
        private val polygon: Polygon,
        var size: Float = shapeSize,
        var radius: Float = circleRadius,
        var rotation: Float = 0f,
        var angle: Float,
    ) {

        private val pShape by lazyShapeCreation()

        fun update(updateFun: ShapeUnit.() -> Unit) {
            updateFun(this)
        }

        fun draw() {
            pushMatrix()
            val x = radius * cos(angle)
            val y = radius * sin(angle)
            translate(x, y)
            rotate(angle)
            rotate(globalRotation + rotation)
            shape(pShape)
            popMatrix()
        }

        private fun lazyShapeCreation() = lazy {
            createShape().apply {
                beginShape()
                stroke(grey11)
                strokeWeight(1.5f)
                noFill()
                var angle = 0f
                while (angle <= TWO_PI) {
                    val x = size * cos(angle)
                    val y = size * sin(angle)
                    vertex(x, y)
                    angle += polygon.innerAngle
                }
                endShape(CLOSE)
            }
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

    protected fun scaleWithRadius(radius: Float): Float {
        return 1 + (scaleBaseRadius - radius) / 300f
    }
}

