package sketch.dance

import shapes.Circle

/**
 * Even the simplest shapes can create intricate patterns when they are repeated.
 *
 * In the same fashion, even the simples actions can give impressive results when they are repeated.
 * Don't underestimate simple actions.
 */
class CircleDance : Choreography() {

    // config
    private val shapeCount = 100 // 16
    private val shapeSize = 115f

    override val shapes by lazy {
        val angleSegment = TWO_PI / shapeCount
        List(shapeCount) {
            val shape = Circle(this, 0f, 0f, shapeSize) {
                noFill()
                stroke(grey11)
                strokeWeight(1.5f)
            }
            ShapeUnit(it, shape, it * angleSegment, R3)
        }
    }

    override fun setup() {
        start(10f) { shapes.forEvery { angle = -initialAngle } }
//            .spreadOut()
//            .spreadIn()
            .spreadOut()
            .waves()
            .then(cycle(1f)) {
            }
    }

    private fun Move.spreadOut() = then(240f) {
        shapes.forEvery {
            val newAngle = smerp(0f, TWO_PI)
            angle = if (newAngle < initialAngle) -initialAngle + newAngle else 0f
        }
    }

    private fun Move.spreadIn(): Move = then(240f) {
        shapes.forEvery {
            val newAngle = smerp(0f, TWO_PI)
            if (newAngle > initialAngle) angle = newAngle - initialAngle
        }
    }

    private fun Move.waves(): Move = then(60f) {
        shapes.forEvery {
            val newRadius = smerp(R3, R3 + 40 * sin(index / 3f)) - initialAngle
            radius = newRadius
        }
    }
        .then(400f) {
            shapes.forEvery {
                val newAngle = smerp(0f, 8 * TWO_PI + PI)
                radius = R3 + 40 * sin(index / 2f + newAngle) - initialAngle
            }
        }

    @Suppress("unused")
    companion object {
        private const val R0 = 0f
        private const val R1 = 100f
        private const val R2 = 200f
        private const val R3 = 300f
    }
}