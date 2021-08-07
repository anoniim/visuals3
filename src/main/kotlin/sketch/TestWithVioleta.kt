package sketch

import BaseSketch
import Screen
import util.Interpolation

class TestWithVioleta: BaseSketch(Screen(800, 400)) {

    override fun setup() {
    }

    override fun draw() {
        translate(50f, halfHeightF)
        background(grey3)
        drawStart()
        drawEnd()
        drawMovingPoint()
    }

    private val animationLength = 200f
    private val maxAmt = calculateMaxAmt(animationLength)
    private val interpolation = Interpolation(animationLength)

    private fun interpolationFormula(animationProgress: Float) = -1 * cos(animationProgress) + 1

    private fun calculateMaxAmt(animationLength: Float): Float {
        val increment = TWO_PI / animationLength
        var x = 0f
        var sum = 0f
        while (x < TWO_PI) {
            sum += interpolationFormula(x)
            x += increment
        }
        println("The sum is $sum")
        return sum
    }

    private var xProgress = 0f
    private fun drawMovingPoint() {
        stroke(red)
        strokeWeight(10f)

//        val animationProgress = map(frameCountF % animationLength, 0f, animationLength, 0f, TWO_PI)
//        xProgress += interpolationFormula(animationProgress)
//        val x = map(xProgress, 0f, maxAmt, 0f, 300f)

        val x = interpolation.interpolate(0f, 300f, frameCountF % animationLength)
        point(x, 0f)
    }


    private fun drawStart() {
        stroke(grey11)
        strokeWeight(15f)
        point(0f, 0f)
    }

    private fun drawEnd() {
        stroke(grey11)
        strokeWeight(15f)
        point(300f, 0f)
    }
}
