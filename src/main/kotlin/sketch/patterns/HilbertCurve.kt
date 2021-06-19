package sketch.patterns

import BaseSketch
import Screen
import processing.core.PVector
import util.point
import util.vertex

/**
 * Space filling curve with transformation animation and optional scaling animation.
 */
class HilbertCurve : BaseSketch(
    Screen(800, 800),
) {

    // config
    private val strokeWeight = 1f

    private var quadrants = mutableListOf(Quadrant(screen.center, screen.halfWidth, QuadrantType.DOWN))
    private var scale = 1f

    override fun draw() {
        background(grey3)
        scaleAnimation() // config

        val path = getFullPath()
        drawPath(path)
//        drawPoints(path)
    }

    override fun keyPressed() {
        nextLevel()
    }

    private fun nextLevel() {
        // Drop tail only if scale animation is on
        val tail = if (scale > 1f) quadrants.size / 4 else 0
        val newQuadrants = mutableListOf<Quadrant>()
        quadrants.drop(tail).forEach {
            newQuadrants.addAll(it.nextLevel())
        }

        quadrants = newQuadrants
    }

    private fun scaleAnimation() {
        if (quadrants[0].isAnimating()) {
            scale += 0.005f
        }
        scale(scale)
    }

    private fun drawPath(path: MutableList<PVector>) {
        noFill()
        strokeWeight( strokeWeight / scale)
        stroke(grey11)
        beginShape()
        path.forEach {
            vertex(it)
        }
        endShape()
    }

    private fun drawPoints(path: MutableList<PVector>) {
        stroke(red)
        strokeWeight(8f)
        path.forEach {
            point(it)
        }
    }

    private fun getFullPath(): MutableList<PVector> {
        val path = mutableListOf<PVector>()
        quadrants.forEach { quadrant ->
            path.addAll(quadrant.getPath())
        }
        return path
    }

    private inner class Quadrant(
        val center: PVector,
        val size: Float,
        val type: QuadrantType,
    ) {

        private val halfSize = size / 2f
        private val path = when (type) {
            QuadrantType.UP -> getUpPath()
            QuadrantType.RIGHT -> getRightPath()
            QuadrantType.DOWN -> getDownPath()
            QuadrantType.LEFT -> getLeftPath()
        }
        private var animationProgress = 0f
        private var animationSpeed = 0.01f

        fun nextLevel(): List<Quadrant> {
            val newSize = size / 2f
            return path.zip(productionSequence(type)) { newCenter, newType ->
                Quadrant(newCenter, newSize, newType)
            }
        }

        private fun productionSequence(type: QuadrantType): List<QuadrantType> {
            return when (type) {
                QuadrantType.UP -> listOf(QuadrantType.RIGHT, QuadrantType.UP, QuadrantType.UP, QuadrantType.LEFT)
                QuadrantType.RIGHT -> listOf(QuadrantType.UP, QuadrantType.RIGHT, QuadrantType.RIGHT, QuadrantType.DOWN)
                QuadrantType.DOWN -> listOf(QuadrantType.LEFT, QuadrantType.DOWN, QuadrantType.DOWN, QuadrantType.RIGHT)
                QuadrantType.LEFT -> listOf(QuadrantType.DOWN, QuadrantType.LEFT, QuadrantType.LEFT, QuadrantType.UP)
            }
        }

//        private fun transition() {
//            val newPath = mutableListOf<PVector>()
//            path.windowed(2).forEachIndexed { i, segment ->
//                val start = segment[0]
//                val end = segment[1]
//                val oneThird = PVector.lerp(start, end, 1/3f)
//                val twoThirds = PVector.lerp(start, end, 2/3f)
//                newPath.add(start)
//                newPath.add(oneThird)
//                newPath.add(twoThirds)
//                newPath.add(end)
//            }
//            path = newPath
//        }

        fun getPath(): List<PVector> {
            return if (animationProgress < 1f) {
                getAnimatedPath()
            } else {
                path
            }
        }

        private fun getAnimatedPath(): List<PVector> {
            animationProgress += animationSpeed
            val animatedPath = mutableListOf<PVector>()
            path.forEach {
                animatedPath.add(PVector.lerp(center, it, animationProgress))
            }
            return animatedPath
        }

        fun isAnimating(): Boolean {
            return animationProgress < 1f
        }

        private fun getLeftPath(): List<PVector> {
            return mutableListOf(
                upperRight(),
                upperLeft(),
                lowerLeft(),
                lowerRight(),
            )
        }

        private fun getDownPath(): List<PVector> {
            return mutableListOf(
                upperRight(),
                lowerRight(),
                lowerLeft(),
                upperLeft(),
            )
        }

        private fun getRightPath(): MutableList<PVector> {
            return mutableListOf(
                lowerLeft(),
                lowerRight(),
                upperRight(),
                upperLeft(),
            )
        }

        private fun getUpPath(): List<PVector> {
            return mutableListOf(
                lowerLeft(),
                upperLeft(),
                upperRight(),
                lowerRight(),
            )
        }

        private fun upperLeft() = center.copy().add(-halfSize, -halfSize)
        private fun lowerLeft() = center.copy().add(-halfSize, halfSize)
        private fun lowerRight() = center.copy().add(halfSize, halfSize)
        private fun upperRight() = center.copy().add(halfSize, -halfSize)
    }

    enum class QuadrantType {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }
}