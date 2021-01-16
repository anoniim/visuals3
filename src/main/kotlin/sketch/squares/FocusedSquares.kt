package sketch.squares

import BaseSketch
import processing.core.PConstants
import kotlin.random.Random

class FocusedSquares : BaseSketch() {

    private val strokeColor = grey9
    private val fillColor = grey5
    private val backgroundColor = grey3

    private val radius: Float = 250F
    private val itemGap: Float = 15F
    private val itemSize: Float = 20F
    private val itemMaxSize: Float = 50F
    private val minRotation: Float = 0F
    private val maxRotation: Float = PConstants.TWO_PI
    private val itemBoundarySize = itemSize + itemGap
    private val itemCenter: Float = itemSize / 2F

    private val itemHorizontalCount: Int = (screen.width / itemBoundarySize).toInt() + 3
    private val itemVerticalCount: Int = (screen.height / itemBoundarySize).toInt() + 3
    private val itemTotalCount: Int = itemHorizontalCount * itemVerticalCount

    override fun setup() {
        smooth()
        rectMode(CENTER)
        background(backgroundColor)

        stroke(strokeColor)
        fill(fillColor)
//        noFill()

        drawItemsInGrid { n ->
            val x = n.rem(itemHorizontalCount) * itemBoundarySize
            val y = n.div(itemHorizontalCount) * itemBoundarySize

            /** Distance **/
            // From mouse
//            val dist = dist(mouseX.toFloat(), mouseY.toFloat(), x, y)
            // From center
            val dist = dist(screen.centerX, screen.centerY, x, y)

            /** Item size **/
            val nthItemSize = map(dist, 0F, screen.centerX, itemSize, itemMaxSize)
//            val nthItemSize = map(dist(screen.centerX, screen.centerY, x, y), 0F, screen.centerX, itemSize, itemMaxSize)
//            val nthItemSize = itemSize + n

            /** Angle **/
            // Fixed
//            val angle = 3F
            // Semi-random
            val angle =
                map(dist, 0F, screen.centerX, minRotation, map(Random.nextFloat(), 0F, 1F, minRotation, maxRotation))

            rotatedItem(x, y, angle) {
                rect(0F, 0F, nthItemSize, nthItemSize)
            }
        }
    }

    private fun rotatedItem(x: Float, y: Float, angle: Float, drawItem: () -> Unit) {
        pushMatrix()
        translate(x + itemCenter, y + itemCenter)
        rotate(angle)
        drawItem()
        popMatrix()
    }

    private fun drawItemsInGrid(drawItem: (Int) -> Unit) {
        for (n in 0..itemTotalCount) {
            drawItem(n)
        }
    }
}


