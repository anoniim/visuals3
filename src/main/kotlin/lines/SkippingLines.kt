package lines

import BaseSketch
import kotlin.random.Random

class SkippingLines: BaseSketch() {

    private val strokeColor = grey9
    private val fillColor = grey5
    private val backgroundColor = grey3

    private val itemSize: Float = 20F
    private val itemHorizontalCount: Int = (screen.width / itemSize).toInt() + 3
    private val itemVerticalCount: Int = (screen.height / itemSize).toInt() + 3
    private val itemTotalCount: Int = itemHorizontalCount * itemVerticalCount


    override fun setup() {
        smooth()
//        rectMode(CENTER)
        background(backgroundColor)

        stroke(strokeColor)
        fill(fillColor)
//        noFill()

        drawItemsInGrid { n, m ->
            val x = n.rem(itemHorizontalCount) * itemSize
            val y = n.div(itemHorizontalCount) * itemSize

            /** Distance **/
            // From mouse
//            val dist = dist(mouseX.toFloat(), mouseY.toFloat(), x, y)
            // From center
            val dist = dist(screen.centerX, screen.centerY, x, y)


        }
    }


    private fun drawItemsInGrid(drawItem: (Int, Int) -> Unit) {
        for (n in 0..itemHorizontalCount) {
            for (m in 0..itemVerticalCount) {
                drawItem(n, m)
            }
        }
    }
}


