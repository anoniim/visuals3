package sketch.shapes

import BaseSketch
import processing.core.PVector
import util.translateToCenter

class Flower1 : BaseSketch() {

    private var numOfTips = 5f
    private var angle = TWO_PI / numOfTips

    private val extRadius = 480f
    private val extRadiusFactor = 0.4f
    private val intRadius = 100f

    private val points = mutableListOf<PVector>()

    override fun draw() {
        background(grey3)
        translateToCenter()
//        mouseControlledNumOfPoints() // config
        drawStarShape()
//        drawPoints()
//        noLoop()
    }

    private fun mouseControlledNumOfPoints() {
        numOfTips = map(mouseXF, 0f, widthF, 2f, 20f)
        angle = TWO_PI / numOfTips
    }

    private fun drawPoints() {
        for (point in points) {
            stroke(red)
            strokeWeight(4f)
            point(point.x, point.y)
        }
        points.clear()
    }

    private fun drawStarShape() {
        fill(grey11)
        noStroke()
        beginShape()
        exteriorVertices()
        interiorVertices()
        endShape()
    }

    private fun exteriorVertices() {
        repeat(ceil(numOfTips)) {
            addVertex(angle * (it - 1 / 2f), extRadius * extRadiusFactor)
            addFlareVertices(angle * (it - 1 / 2f), 1)
            addVertex(angle * it, extRadius)
            addFlareVertices(angle * (it + 1 / 2f), -1)
            addVertex(angle * (it + 1 / 2f), extRadius * extRadiusFactor)
        }
    }

    private fun addFlareVertices(baseAngle: Float, direction: Int) {
        val radius = extRadius - extRadius * extRadiusFactor
        val intRange = (1..3)
        val vertexRange = if (direction == -1) intRange.reversed() else intRange
        for (i in vertexRange) {
            addVertex(baseAngle + direction * (i * QUARTER_PI / 4f), (1 + (i - 1) * 1 / 4f) * radius)
        }
    }

    private fun addVertex(angle: Float, radius: Float) {
        val x = radius * cos(angle)
        val y = radius * sin(angle)
//        curveVertex(x, y) // config
        vertex(x, y)
        points.add(PVector(x, y))
    }

    private fun addCurveVertex(angle: Float, radius: Float) {
        val x = radius * cos(angle)
        val y = radius * sin(angle)
        curveVertex(x, y)
        points.add(PVector(x, y))
    }

    private fun interiorVertices() {
        beginContour()
        for (i in ceil(numOfTips) + 1 downTo -1) {
            addCurveVertex(angle * i, intRadius)
        }
        endContour()
    }
}