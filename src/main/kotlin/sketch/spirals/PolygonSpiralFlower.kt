package sketch.spirals

import BaseSketch
import Screen
import processing.core.PVector
import util.line
import util.translateToCenter

class PolygonSpiralFlower : BaseSketch(Screen(fullscreen = true)) {

    // config
    private val speed = 1
    private val shape = Shape.HEXAGON
    private val baseAngle = shape.angle
    private val angleStep = baseAngle + 0.006f
    private val radiusFactor = shape.radiusFactor

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
    private enum class Shape(val radiusFactor: Int, val angle: Float) {
        TRIANGLE(5, 2 * PI / 3f),
        SQUARE(4, PI / 2f),
        PENTAGON(5, PI / 3f),
        HEXAGON(6, TWO_PI / 5f),
    }
}
