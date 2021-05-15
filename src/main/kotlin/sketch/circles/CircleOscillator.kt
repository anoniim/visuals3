package sketch.circles

import BaseSketch
import shapes.Circle
import util.SimplexNoise
import util.translateToCenter

class CircleOscillator : BaseSketch() {

    // config
    private val initialRadius = 200f

    private val circles = List(40) {
        // config
//        NoiseStringyCircle(this, 0f, 0f, initialRadius, it)
        SineStringyCircle(this, 0f, 0f, initialRadius, it)
    }

    override fun draw() {
        translateToCenter()
        background(grey3)
        circles.forEach {
            it.update()
            it.draw()
        }
    }

    private abstract class StringyCircle(
        applet: BaseSketch, x: Float, y: Float,
        val initR: Float,
        val index: Int
    ) : Circle(applet, x, y, initR) {

        protected open val sizeFactor = 200f

        fun update() {
            r = initR + sizeFactor * radiusFunction()
        }

        protected abstract fun radiusFunction(): Float

        fun draw() {
            super.draw {
                stroke(grey11, 200f)
                noFill()
            }
        }
    }

    private class NoiseStringyCircle(
        applet: BaseSketch, x: Float, y: Float,
        initR: Float,
        index: Int
    ): StringyCircle(applet, x, y, initR, index) {

        // config
        override val sizeFactor: Float = 200f
        private val noiseSpeed = 200f

        override fun radiusFunction(): Float {
            return with(applet) {
                val sequenceDiff = 6f * index
                val noiseOffset = frameCount + sequenceDiff
                // config
                openSimplex(noiseOffset)
                perlin(noiseOffset)
            }
        }

        private fun BaseSketch.perlin(noiseOffset: Float) =
            map(noise(noiseOffset / noiseSpeed), 0f, 1f, -2f, 2f)

        val noise = SimplexNoise()

        private fun openSimplex(noiseOffset: Float) =
            noise.eval(noiseOffset / noiseSpeed, 0f)
    }

    private class SineStringyCircle(
        applet: BaseSketch, x: Float, y: Float,
        initR: Float,
        index: Int
    ): StringyCircle(applet, x, y, initR, index) {

        override fun radiusFunction(): Float {
            return with(applet) {
                val noiseOffset = (frameCount + 4 * index) / 50f
                sin(noiseOffset)
            }
        }
    }
}
