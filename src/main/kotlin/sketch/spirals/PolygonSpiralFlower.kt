package sketch.spirals

import BaseSketch
import Screen
import processing.core.PVector
import util.line
import util.translateToCenter

class PolygonSpiralFlower : BaseSketch(Screen(fullscreen = true)) {

    // config
    private val speed = 10 // 1-20
    private val shape = Shape.SQUARE
    private val angleStep = shape.baseAngle + 0.006f // lower for finer shape
    private val radiusFactor = shape.radiusFactor + 0 // can be adjusted to zoom in/out

    private var scale = 0.1f
    private var angle = 0f
    private var numPathPoints = 1
    private val path = mutableListOf(vectorFromAngle(0f))

    override fun setup() {
        background(grey3)
    }

    override fun draw() {
        stroke(grey11)
        translateToCenter()
        scale(scale)

        repeat(speed) {
            addNewPoint()
            drawPath()
        }
    }

    private fun addNewPoint() {
        angle += angleStep
        val newPoint = vectorFromAngle(angle)
        path.add(newPoint)
    }

    private fun vectorFromAngle(angle: Float) = PVector.fromAngle(angle).setMag(getRadiusMagnitude())

    private fun getRadiusMagnitude(): Float {
        return 7000f - (radiusFactor * numPathPoints)
    }

    private fun drawPath() {
        line(path[numPathPoints - 1], path[numPathPoints])
        numPathPoints++
    }

    override fun keyPressed() {
        if (key == ' ') {
            noLoop()
        } else {
            loop()
        }
    }

    @Suppress("unused")
    private enum class Shape(val radiusFactor: Int, val baseAngle: Float) {
        TRIANGLE(5, 2 * PI / 3f),
        SQUARE(4, PI / 2f),
        PENTAGON(6, TWO_PI / 5f),
        HEXAGON(5, PI / 3f),
    }
}
