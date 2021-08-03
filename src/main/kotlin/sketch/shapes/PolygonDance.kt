package sketch.shapes

import BaseSketch
import util.Polygon
import util.translateToCenter

class PolygonDance : BaseSketch() {

    private val circleRadius = 200f
    private val repeatCount = 16
    private val shapeSize = 100f
    private val shape = Polygon.TRIANGLE

    private val moves = mutableListOf<Move>()
    private val angleSegment = TWO_PI / repeatCount
    private val shapes by lazy {
        List(repeatCount) {
            ShapeUnit(angle = it * angleSegment)
        }
    }

    // TODO global variable for scaling so that it's persisted between moves
    // TODO shapes.forEvery() out of move()

    override fun setup() {
        start(240f) {
            // arrival to IN_P5
            shapes.forEvery {
                rotation = PI
                radius = map(moveFrameCount, 0f, moveLength, halfWidthF * 1.8f, IN_P5)
            }}
//            .thenPause(30f)
            .then(cycle(1 / 3f)) {
                // rotate OUT
                shapes.forEvery {
                    rotation = PI + map(moveFrameCount, 0f, moveLength, 0f, PI / 3)
                }
            }
//            .thenPause(30f)
            .then(60f) {
                // go to OUT_P10
                shapes.forEvery {
                    radius = map(moveFrameCount, 0f, moveLength, IN_P5, OUT_P10)
                }

            }
//            .thenPause(30f)
            .then(60f) {
                // go to OUT_P9
                move(OUT_P10, OUT_P9)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to OUT_P8
                val newRadius = move(OUT_P9, OUT_P8)
                scaleWithRadius(newRadius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to OUT_P7
                val newRadius = move(OUT_P8, OUT_P7)
                scaleWithRadius(newRadius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to OUT_P6
                val newRadius = move(OUT_P7, OUT_P6)
                scaleWithRadius(newRadius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to OUT_P5
                val newRadius = move(OUT_P6, OUT_P5)
                scaleWithRadius(newRadius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to OUT_P4
                val newRadius = move(OUT_P5, OUT_P4)
                scaleWithRadius(newRadius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to OUT_P3
                val newRadius = move(OUT_P4, OUT_P3)
                scaleWithRadius(newRadius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to OUT_P2
                val newRadius = move(OUT_P3, OUT_P2)
                scaleWithRadius(newRadius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to OUT_P1
                val newRadius = move(OUT_P2, OUT_P1)
                scaleWithRadius(newRadius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to OUT_P0
                val newRadius = move(OUT_P1, OUT_P0)
                scaleWithRadius(newRadius)
            }
            .thenPause(30f)
            .then(240f) {
                // crazy rotate IN
                shapes.forEvery {
                    rotation = map(moveFrameCount, 0f, moveLength, 0f, TWO_PI + PI / 3)
                }
                scaleWithRadius(shapes.first().radius)
            }
            .thenPause(30f)
            .then(60f) {
                // go to IN_P1
                shapes.forEvery {
                    rotation = PI
                    radius = map(moveFrameCount, 0f, moveLength, OUT_P0, IN_P1)
                }
                scaleWithRadius(shapes.first().radius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to IN_P2
                shapes.forEvery {
                    rotation = PI
                    radius = map(moveFrameCount, 0f, moveLength, IN_P1, IN_P2)
                }
                scaleWithRadius(shapes.first().radius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to IN_P3
                shapes.forEvery {
                    rotation = PI
                    radius = map(moveFrameCount, 0f, moveLength, IN_P2, IN_P3)
                }
                scaleWithRadius(shapes.first().radius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to IN_P4
                shapes.forEvery {
                    rotation = PI
                    radius = map(moveFrameCount, 0f, moveLength, IN_P3, IN_P4)
                }
                scaleWithRadius(shapes.first().radius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to IN_P5
                shapes.forEvery {
                    rotation = PI
                    radius = map(moveFrameCount, 0f, moveLength, IN_P4, IN_P5)
                }
                scaleWithRadius(shapes.first().radius)
            }
//            .thenPause(30f)
            .then(60f) {
                // go to IN_P5
                shapes.forEvery {
                    rotation = PI
                    radius = map(moveFrameCount, 0f, moveLength, IN_P5, OUT_P9)
                }
                scaleWithRadius(shapes.first().radius)
            }
//            .thenPause(30f)
            .then(120f) {
                // rotate OUT
                shapes.forEvery {
                    rotation = PI + map(moveFrameCount, 0f, moveLength, 0f, (PI / 3))
                }
            }
//            .thenPause(30f)
            .then(60f) {
                // out of the screen
                shapes.forEvery {
                    radius = map(moveFrameCount, 0f, moveLength, OUT_P9, halfWidthF * 1.8f)
                }
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
        drawShapes()
    }

    private fun test() {
        shapes.forEach {
            // mouse control, TODO find all scales at which the triangles cross and animate gradually from one to another
            it.update {
                rotation = PI
                radius = mouseXF
            }
        }
        if (mouseXF < 100) {
            scale(1 + (100 - mouseXF) / OUT_P3)
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

    private inner class ShapeUnit(
        val polygon: Polygon = shape,
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
            rotate(rotation)
            shape(pShape)
            popMatrix()
        }

        private fun lazyShapeCreation() = lazy {
            createShape().apply {
                beginShape()
                stroke(grey11)
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

    private fun start(forFrames: Float, action: Move.() -> Unit): Move {
        val move = Move(0f, forFrames, action)
        moves.add(move)
        return move
    }

    private fun Move.then(forFrames: Float, action: Move.() -> Unit): Move {
        val thenMove = Move(this.startFrame + this.moveLength, forFrames, action)
        moves.add(thenMove)
        return thenMove
    }

    private fun Move.thenPause(forFrames: Float): Move {
        return then(forFrames) {}
    }

    private inner class Move(val startFrame: Float, val moveLength: Float, val action: Move.() -> Unit) {
        val moveFrameCount: Float
            get() = frameCountF - startFrame

        fun move(from: Float, to: Float): Float {
            val delta = map(moveFrameCount, 0f, moveLength, from, to)
            shapes.forEvery {
                radius = delta
            }
            return delta
        }

        fun scaleWithRadius(newRadius: Float) {
            scale(1 + (OUT_P9 - newRadius) / 350f)
        }
    }

    private fun List<ShapeUnit>.forEvery(nth: Int = 1, start: Int = 0, function: ShapeUnit.() -> Unit) {
        if (nth == 1) {
            forEach {
                it.update {
                    function()
                }
            }
        } else {
            // TODO
        }
    }

    private fun cycle(times: Float) = times * TWO_PI * 100

    companion object {
        private const val IN_P0 = 0f
        private const val IN_P1 = 37f
        private const val IN_P2 = 80f
        private const val IN_P3 = 100f
        private const val IN_P4 = 160f
        private const val IN_P5 = 388f
        private const val OUT_P10 = 486f
        private const val OUT_P9 = 260f
        private const val OUT_P8 = 179f
        private const val OUT_P7 = 137f
        private const val OUT_P6 = 108f
        private const val OUT_P5 = 86f
        private const val OUT_P4 = 67f
        private const val OUT_P3 = 50f
        private const val OUT_P2 = 33f
        private const val OUT_P1 = 14f
        private const val OUT_P0 = 0f
    }
}

