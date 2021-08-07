package sketch.shapes

import shapes.Polygon

/**
 * Even the simplest shapes can create intricate patterns when they are repeated.
 *
 * In the same fashion, even the simples actions can give impressive results when they are repeated.
 * Don't underestimate simple actions.
 */
class TriangleDance: Choreography() {

    // config
    private val shapeCount = 16
    private val shapeSize = 100f

    override val shapes by lazy {
        val angleSegment = TWO_PI / shapeCount
        List(shapeCount) {
            val shape = Polygon(this, Polygon.Type.TRIANGLE, shapeSize)
            ShapeUnit(shape, initialRotation = it * angleSegment)
        }
    }

    override fun setup() {
        start(140f) {
            // arrival to IN_P5
            globalRotation = PI
            shapes.forEvery { radius = smerp(halfWidthF * 1.4f, IN_P5) }
        }
            .thenPause(10f)
            .then(cycle(1 / 4f)) {
                // rotate OUT
                globalRotation = smerp(PI, TWO_PI / 3)
            }
            .showEvenAndOdd(IN_P5, 37f)
            .thenPause(10f)
            .gettingCloser()
            .thenPause(30f)
            .then(120f) {
                // Push even out
                shapes.forEvery(2) { radius = smerp(OUT_P5, OUT_P9) }
            }
            .thenPause(30f)
            .rotateOdd()
            .bringEvenBack()
            .thenPause(30f)
            .spiralRotation()
            .thenPause(600f)
            .then(60f) {
                // go to OUT_P4
                lerp(OUT_P5, OUT_P4).let {
                    shapes.forEvery { radius = it }
                    globalScale = scaleWithRadius(it)
                }
            }
            .then(60f) {
                // go to OUT_P3
                lerp(OUT_P4, OUT_P3).let {
                    shapes.forEvery { radius = it }
                    globalScale = scaleWithRadius(it)
                }
            }
            .thenPause(10f)
            .then(cycle(1 / 4f)) {
                shapes.forEvery(2, 0) { rotation = lerp(0f, HALF_PI) }
            }
            .then(cycle(1 / 2f)) {
                shapes.forEvery(2, 0) { rotation = lerp(HALF_PI, -PI) }
            }
            .then(cycle(1 / 2f)) {
                shapes.forEvery(2, 0) { rotation = lerp(-PI, 0f) }
            }
            .thenPause(10f)
            .then(60f) {
                // go to OUT_P2
                lerp(OUT_P3, OUT_P2).let {
                    shapes.forEvery { radius = it }
                    globalScale = scaleWithRadius(it)
                }
            }
            .then(60f) {
                // go to OUT_P1
                lerp(OUT_P2, OUT_P1).let {
                    shapes.forEvery { radius = it }
                    globalScale = scaleWithRadius(it)
                }
            }
            .then(60f) {
                // go to OUT_P0
                lerp(OUT_P1, OUT_P0).let {
                    shapes.forEvery { radius = it }
                    globalScale = scaleWithRadius(it)
                }
            }
            .thenPause(30f)
            .then(240f) {
                // crazy rotate IN
                globalRotation = smerp(0f, PI)
            }
            .thenPause(30f)
            .then(60f) {
                // go to IN_P1
                lerp(OUT_P0, IN_P1).let {
                    shapes.forEvery { radius = it }
                    globalScale = scaleWithRadius(it)
                }
            }
            .then(60f) {
                // go to IN_P2
                lerp(IN_P1, IN_P2).let {
                    shapes.forEvery { radius = it }
                    globalScale = scaleWithRadius(it)
                }
            }
            .then(60f) {
                // go to IN_P3
                lerp(IN_P2, IN_P3).let {
                    shapes.forEvery { radius = it }
                    globalScale = scaleWithRadius(it)
                }
            }
            .then(60f) {
                // go to IN_P4
                lerp(IN_P3, IN_P4).let {
                    shapes.forEvery { radius = it }
                    globalScale = scaleWithRadius(it)
                }
            }
            .then(60f) {
                // go to IN_P5
                lerp(IN_P4, IN_P5).let {
                    shapes.forEvery { radius = it }
                    globalScale = scaleWithRadius(it)
                }
            }
            .then(60f) {
                // go to IN_P5
                lerp(IN_P5, OUT_P9).let {
                    globalScale = scaleWithRadius(it)
                    shapes.forEvery { radius = it }
                }
            }
            .then(120f) {
                // rotate OUT
                globalRotation = lerp(PI, 0f)
            }
            .then(120f) {
                // rotate IN
                globalRotation = lerp(0f, PI)
            }
            .then(120f) {
                // rotate OUT
                globalRotation = lerp(PI, 0f)
            }
            .thenPause(10f)
            .then(60f) {
                // out of the screen
                lerp(OUT_P9, halfWidthF * 1.8f).let {
                    shapes.forEvery { radius = it }
                }
            }
    }

    private fun Choreography.Move.showEvenAndOdd(startingRadius: Float, jumpAmount: Float): Move {
        val jumpLength = 60f
        return then(jumpLength) {
            shapes.forEvery(2) { radius = smerp(startingRadius, startingRadius + jumpAmount) }
        }.then(jumpLength) {
            shapes.forEvery(2) { radius = smerp(startingRadius + jumpAmount, startingRadius) }
        }.then(jumpLength) {
            shapes.forEvery(2, 1) { radius = smerp(startingRadius, startingRadius + jumpAmount) }
        }.then(jumpLength) {
            shapes.forEvery(2, 1) { radius = smerp(startingRadius + jumpAmount, startingRadius) }
        }
    }

    private fun Choreography.Move.gettingCloser(): Move {
        return then(120f) {
            smerp(IN_P5, OUT_P9).let {
                shapes.forEvery { radius = it }
            }
        }
            .showEvenAndOdd(OUT_P9, 84f)
            .then(400f) {
                // go to OUT_P8
                smerp(OUT_P9, OUT_P5).let {
                    shapes.forEvery { radius = it }
                    globalScale = scaleWithRadius(it)
                }
            }
    }

    private fun Choreography.Move.rotateOdd(): Move {
        return then(120f) {
            shapes.forEvery(2, 1) { radius = smerp(OUT_P5, OUT_P7) }
        }
            .thenPause(10f)
            .then(120f) {
                shapes.forEvery(2, 1) { radius = smerp(OUT_P7, OUT_P5) }
            }
            .then(120f) {
                shapes.forEvery(2, 1) { rotation = smerp(0f, PI / 3) }
            }
            .thenPause(30f)
            .then(60f) {
                shapes.forEvery(2, 1) { radius = smerp(OUT_P5, IN_P3) }
            }
            .thenPause(10f)
            .then(60f) {
                shapes.forEvery(2, 1) { radius = smerp(IN_P3, IN_P4) }
            }
            .thenPause(10f)
            .then(60f) {
                shapes.forEvery(2, 1) { rotation = smerp(PI / 3, TWO_PI / 3) }
            }
            .then(120f) {
                shapes.forEvery(2, 1) {
                    radius = smerp(IN_P4, OUT_P5)
                    rotation = smerp(TWO_PI / 3, 0f)
                }
            }
            .then(360f) {
                shapes.forEvery(2, 1) { rotation = smerp(TWO_PI / 3, -TWO_PI / 3) }
            }
    }

    private fun Choreography.Move.bringEvenBack(): Move =
        then(300f) {
            shapes.forEvery(2) {
                radius = smerp(OUT_P9, IN_P4)
                rotation = smerp(0f, PI / 3)

            }
        }
            .thenPause(10f)
            .then(300f) {
                shapes.forEvery(2) {
                    radius = smerp(IN_P4, OUT_P9)
                    rotation = smerp(PI / 3, TWO_PI / 3)

                }
            }
            .thenPause(30f)
            .then(180f) {
                smerp(OUT_P9, OUT_P5).let {
                    shapes.forEvery(2) { radius = it }
                }
            }

    private fun Choreography.Move.spiralRotation(): Move {
        return then(cycle(2f)) {
            globalRotation = smerp(TWO_PI / 3, 2 * TWO_PI)
        }
            .then(cycle(2f)) {
                shapes.forEvery(2) { rotation = smerp(TWO_PI / 3, 2 * TWO_PI) }
                shapes.forEvery(2,1) {
                    rotation = smerp(TWO_PI / 3, 2 * TWO_PI)
                    radius = smerp(OUT_P5, OUT_P9)
                }
            }
            .thenPause(300f)
            .then(60f) {
                smerp(OUT_P9, OUT_P6).let {
                    shapes.forEvery(2) { radius = it }
                }
            }
    }

    private fun scaleWithRadius(radius: Float): Float {
        return 1 + (OUT_P9 - radius) / 300f
    }

    @Suppress("unused")
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