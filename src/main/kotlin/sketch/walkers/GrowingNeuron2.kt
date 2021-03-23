package sketch.walkers

import BaseSketch
import processing.core.PVector
import util.OpenSimplexNoise


class GrowingNeuron2 : BaseSketch(renderer = P2D) {

    // config
    private val speed: Int = 2 // 1-5
    private val activeCount: Int = 500 // < 500
    private val zoom = 0.02f // 0.01 - 0.001
    private val maxLength = 1000
    private val offset = 80f

    private val activeCountFraction = activeCount/20
    private var toIndex: Int = activeCount
    private var fromIndex: Int = 0
    private val dendrites = List(6000) { Dendrite() }
    private var activeDendrites = dendrites.subList(fromIndex, toIndex)

    override fun setup() {
        background(grey3)
    }

    override fun draw() {
        for (dendrite in activeDendrites) {
            dendrite.show()
        }

        if (frameCount % activeCountFraction == 0) {
            if (toIndex < dendrites.lastIndex) {
                toIndex += 20
                recalculateActive()
            }
        }
    }

    private fun recalculateActive() {
        activeDendrites = dendrites.subList(fromIndex, toIndex)
    }

    inner class Dendrite(
        private val startX: Float = random(screen.widthF),
        private val startY: Float = random(screen.heightF)
    ) {
        val path = generatePath()
        private var complete: Boolean = false
        private var growth = 0

        private fun generatePath(): List<PVector> {
            val shape = mutableListOf<PVector>()
            var x = startX
            var y = startY
            for (i in 0..maxLength) {
                val zoom = zoom.toDouble()
                val a: Float =
                    PI * (OpenSimplexNoise().eval(offset + x * zoom, offset + y * zoom, 0.0)).toFloat()
                x += cos(a)
                y += sin(a)
                shape.add(PVector(x, y))
            }
            return shape
        }

        fun show() {
            strokeWeight(1f)
            stroke(pink, 40f)
            noFill()
            beginShape()
            val lastIndex = path.lastIndex
            for (i in 0 until growth) {
                val currentVector = path[lastIndex - i]
                vertex(currentVector.x, currentVector.y)
            }
            endShape()
            if (growth < path.size - speed) {
                growth += speed
            } else {
                complete = true
                if (fromIndex < dendrites.lastIndex) {
                    fromIndex++
                    recalculateActive()
                }
            }
        }
    }
}