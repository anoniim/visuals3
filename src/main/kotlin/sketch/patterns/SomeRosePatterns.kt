package sketch.patterns

import BaseSketch
import util.translateToCenter

class SomeRosePatterns : BaseSketch() {

    private val revolutions = 80
    private val resolution = .1f
    private val maxNumOfPoints: Int = ceil(revolutions * TWO_PI / resolution)
    private val radius: Float = 300f

    private var numOfPoints: Int = 0

    override fun setup() {
        frameRate(10f)
    }

    override fun draw() {
        translateToCenter()
        background(white)
        drawRose()

        increaseNumOfPoints()
        printNumOfPoints()
    }

    private fun printNumOfPoints() {
        text("max: $maxNumOfPoints", -halfWidthF + 20f, -halfHeightF + 30f)
        text("i: $numOfPoints", -halfWidthF + 20f, -halfHeightF + 50f)
    }

    private fun increaseNumOfPoints() {
        numOfPoints += if (key == ' ') 10 else 1
        if (numOfPoints > maxNumOfPoints + 2) {
//            numOfPoints = 0
            noLoop()
        }
    }

    private fun drawRose() {
        noFill()
        stroke(grey3)
        drawShape()
    }

    private fun drawShape() {
        beginShape()
        for (i in 1..numOfPoints) {
//            val radius = sunFlower(i)
//            val radius = new1(i)
            val radius = new2(i)
            val angle = i * resolution
            val x = radius * cos(angle)
            val y = radius * sin(angle)
            curveVertex(x, y)
        }
        endShape()
    }

    private fun sunFlower(i: Int): Float {
        val radiusCoef = i * 1.5f
        return radius * (sin(13 * i / 20f) + sin(sqrt(radiusCoef)))
    }

    private fun new1(i: Int): Float {
        return radius * sin(i * 2f) * cos(i*3f)
    }

    private fun new2(i: Int): Float {
        return radius * sin(i * 23.7f) - sin(250f/i) * 44.5f/i
    }
}