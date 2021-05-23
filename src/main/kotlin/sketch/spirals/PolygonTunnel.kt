package sketch.spirals

import BaseSketch
import Screen
import processing.core.PVector
import util.line
import util.translateToCenter

class PolygonTunnel : BaseSketch(Screen(fullscreen = false)) {

    // config
    private val shape = Shape.SQUARE
    private val speed = 0.08f // 0.01 - 1
    private val initSize = 50f // 10 - 100
    private val spin = 0 // (0.01) 0 - 0.1f
    private val perspectiveEnabled = false

    private val dissolveFactor = 1f + speed / if (spin > 0) 100f else 50f
    private var zoomingIn = false
    private var animationProgress = 0f
    private var angle = shape.innerAngle
    private val path = mutableListOf(vectorFromAngle(0f), vectorFromAngle(shape.innerAngle))

    override fun keyPressed() {
        when (key) {
            ' ' -> noLoop()
            ENTER -> zoomingIn = true
            else -> loop()
        }
    }

    override fun draw() {
        background(grey3)
        stroke(grey11)
        translateToCenter()

        drawPath()
        removeOutOfScreenSegments()
    }

    private fun drawPath() {
        drawNonAnimatedPath()
        drawAnimatedSegment()
        addNewIfAnimationFinished()
        departAnimationIfOn()
    }

    private fun drawNonAnimatedPath() {
        val lastSegmentIndex = path.size - if (zoomingIn) 0 else 1
        for (i in 1 until lastSegmentIndex) {
            val secondToLastNonAnimatedSegment = path[i - 1]
            val lastNonAnimatedSegment = path[i]
            if (perspectiveEnabled) strokeWeight(1f + secondToLastNonAnimatedSegment.mag() / 100f)
            line(secondToLastNonAnimatedSegment, lastNonAnimatedSegment)
            increaseMagnitude(secondToLastNonAnimatedSegment)
        }
        if (path.size > 0) increaseMagnitude(path.last())
    }

    private fun drawAnimatedSegment() {
        if (animationProgress > 1f) return
        val secondToLast = path[path.size - 2]
        increaseMagnitude(secondToLast)
        val animatedSegment = secondToLast.copy().lerp(path.last(), animationProgress)
        line(secondToLast, animatedSegment)
        animationProgress += speed
    }

    private fun addNewIfAnimationFinished() {
        if (!zoomingIn && animationProgress >= 1f) {
            addNewPoint()
            animationProgress = 0f
        }
    }

    private fun addNewPoint() {
        angle += shape.innerAngle + spin
        val newPoint = vectorFromAngle(angle)
        path.add(newPoint)
    }

    private fun departAnimationIfOn() {
        if (zoomingIn && animationProgress >= 1f) {
            scale(animationProgress)
        }
    }

    private fun vectorFromAngle(angle: Float) = PVector.fromAngle(angle).setMag(initSize)

    private fun increaseMagnitude(vector: PVector) = vector.setMag(vector.mag() * dissolveFactor)

    private fun removeOutOfScreenSegments() {
        path.removeIf { it.mag() > widthF }
    }

    @Suppress("unused")
    private enum class Shape(val radiusFactor: Int, val innerAngle: Float) {
        TRIANGLE(5, 2 * PI / 3f),
        SQUARE(4, PI / 2f),
        PENTAGON(6, TWO_PI / 5f),
        HEXAGON(5, PI / 3f),
    }
}
