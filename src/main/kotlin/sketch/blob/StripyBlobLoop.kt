@file:Suppress("MemberVisibilityCanBePrivate")

package sketch.blob

import util.OpenSimplexNoise
import BaseSketch


class StripyBlobLoop : BaseSketch() {

    private val strokeColor = grey9
    private val fillColor = grey5
    private val backgroundColor = grey3

    private val itemSize: Float = 20F
    private val itemHorizontalCount: Int = (screen.width / itemSize).toInt() + 3
    private val itemVerticalCount: Int = (screen.height / itemSize).toInt() + 3
    private val itemTotalCount: Int = itemHorizontalCount * itemVerticalCount

    val noise = OpenSimplexNoise()
    var xBlob: Float = 0F
    var yBlob: Float = 0F
    var t = 0f
    var numFrames = 120
    private var noiseRadius: Float = 2F
    val randomness = 0.0005  // 0.0001 for smooth

    override fun setup() {
        smooth()
        stroke(strokeColor)
    }

    override fun draw() {
        background(backgroundColor)
        t = map(frameCount-1F, 0F, numFrames.toFloat(), 0F, 1F)

        xBlob = width / 2 * noise.eval(
            0.0,
            (noiseRadius * cos(TWO_PI * t)).toDouble(),
            (noiseRadius * sin(TWO_PI * t)).toDouble()
        ).toFloat() + width / 2
        yBlob = height / 2 * noise.eval(
            10000.0,
            (noiseRadius * cos(TWO_PI * t)).toDouble(),
            (noiseRadius * sin(TWO_PI * t)).toDouble()
        ).toFloat() + height / 2

        drawItemsInGrid { n, m ->
            val x1 = getCoordinate(n)
            val y1 = getCoordinate(m)
            val x2 = getCoordinate(n + 1)
            val y2 = getCoordinate(m + 1)

            val radius = 100 * noise.eval(
                x1 * itemSize * randomness,
                y1 * itemSize * randomness,
                noiseRadius * cos(TWO_PI * t).toDouble(),
                noiseRadius * sin(TWO_PI * t).toDouble()
            ).toFloat() + 100

            if (dist(x1, y1, xBlob, yBlob) < radius) {
                line(x1, y1, x2, y2)
            } else {
                line(x2, y1, x1, y2)
            }
        }
        
//        recordAndExit(numFrames)
    }

    private fun getCoordinate(itemIndex: Int): Float = itemIndex * itemSize

    private fun drawItemsInGrid(drawItem: (Int, Int) -> Unit) {
        for (n in 0..itemHorizontalCount) {
            for (m in 0..itemVerticalCount) {
                drawItem(n, m)
            }
        }
    }
}