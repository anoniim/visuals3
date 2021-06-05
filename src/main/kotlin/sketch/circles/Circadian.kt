package sketch.circles

import BaseSketch
import util.SimplexNoise
import util.translateToCenter

class Circadian : BaseSketch(renderer = P2D) {

    // config
    private val noiseAmp = 100 // 10 - 100
    private val distortedSize = 9 // 2-9
    private val nGroups = 3
    private val nCircles = 10
    private val nPoints = 100
    private val radius = 300f
    private val groupRotation = QUARTER_PI / 4

    private val angleSegment = TWO_PI / nPoints
    private val simplexNoise = SimplexNoise()
    private val distortedRange = IntRange(1 * nPoints / 10, distortedSize * nPoints / 10)
    private var noiseBuffer = List(nGroups * nCircles) { List(nPoints) { 0f } }
    private var time = 0f


    override fun draw() {
        background(grey3)
        translateToCenter()
        rotate(PI)
        noFill()

        repeat(nGroups) {
            stroke(colors.pastel[it+1])
            rotate(groupRotation)
            repeat(nCircles) { circleIndex ->
                drawCircle(it, circleIndex)
            }
        }
        updateNoiseBuffer()
        if (frameCount % nGroups == 0) time += map(mouseXF, 0f, widthF, 0.001f, 0.1f)
    }

    private fun updateNoiseBuffer() {
        val newNoisePoints = List(distortedRange.count()) {
            val noiseSpaceCoord = it * QUARTER_PI / distortedRange.count()
            val x = 10f * cos(noiseSpaceCoord)
            val y = 10f * sin(noiseSpaceCoord)
            val amplitude = 1/constrain(abs(map(it, 0, distortedRange.count(), -3, 3)), 0.5f, 10f) * noiseAmp
            amplitude * (simplexNoise.eval(x, y, time))
        }
        noiseBuffer = (noiseBuffer.drop(1) + listOf(newNoisePoints))
    }

    private fun drawCircle(groupIndex: Int, circleIndex: Int) {
        beginShape()
        for (i in 0..nPoints) {
            startPivot(i)
            if (i in distortedRange) {
                val bufferIndex = floor(map(i, distortedRange.first, distortedRange.last, 0, distortedRange.count() -1))
                val noiseGroupIndex = groupIndex * circleIndex + circleIndex
                val (x, y) = getPointForIndex(i, radius + noiseBuffer[noiseGroupIndex][bufferIndex])
                curveVertex(x, y)
            } else {
                val (x, y) = getPointForIndex(i)
                curveVertex(x, y)
            }
            endPivot(i)
        }
        endShape()
    }

    private fun startPivot(i: Int) {
        if (i == 0) {
            val (x, y) = getPointForIndex(nPoints - 1)
            curveVertex(x, y)
        }
    }

    private fun endPivot(i: Int) {
        if (i == nPoints) {
            val (x, y) = getPointForIndex(1)
            curveVertex(x, y)
        }
    }

    private fun getPointForIndex(i: Int, segmentRadius: Float = radius): Pair<Float, Float> {
        val x = segmentRadius * cos(i * angleSegment)
        val y = segmentRadius * sin(i * angleSegment)
        return Pair(x, y)
    }
}


