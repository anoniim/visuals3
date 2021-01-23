package sketch.lines

import BaseSketch
import Screen
import kotlin.math.roundToInt

class ConstraintNoise : BaseSketch(
    Screen(1200, 800),
    longClickClear = true
) {

    private val spacing: Float = 40f // 20
    private val xSmoothness = 6f // 6
    private val ySmoothness = 6f // 6
    private val noiseFactor = 150f // 50
    private val noiseRadius = 400f // 400
    private val drop = 0f // 100f
    private var movementStep: Float = 0.01f // 0.01

    private val lineBuffer = noiseFactor / spacing
    private val numLines = screen.heightF / spacing
    private val yBuffer = -noiseFactor
    private var lines = generateLines()

    override fun setup() {
//        println("yBuffer: $yBuffer")
    }

    override fun draw() {
        background(grey3)
        for (line in lines) {
            line.draw()
            line.update()
        }
        drawLongPressOverlay()
    }

    override fun reset() {
        lines = generateLines()
    }

    private fun generateLines() = List((numLines + lineBuffer).roundToInt()) {
        Line(yBuffer + it * spacing)
    }

    private inner class Line(val yOrigin: Float) {

        private val yOffset: Float
            get() = map(mouseYF, 0f, screen.heightF, -drop, drop)
        private var horizontalMovement: Float = 0f
        private var verticalMovement: Float = 0f

        private val numPoints = ceil(screen.widthF / xSmoothness)
        private val segmentLength = screen.widthF / numPoints

        fun draw() {
            stroke(grey9)
            strokeWeight(1f)
            noFill()
            beginShape()
            for (i in -1..numPoints + 1) {
                val x = i * segmentLength
                val y = yOrigin + calculateNoise(x, yOrigin, i)
                curveVertex(x, y)
            }
            endShape()
        }

        private fun calculateNoise(x: Float, y: Float, i: Int): Float {
            val distFromCenter = distFromScreenCenter(x, y)
            val verticalDistFromCenter = dist(0f, y, 0f, screen.centerY)
            val shouldNoise = distFromCenter < noiseRadius
            return if (shouldNoise) {
//                val noiseDampening = map(distFromCenter, 0f, noiseRadius, 1f, 0.2f)
                val noiseDampening = map(verticalDistFromCenter, 0f, noiseRadius, 1f, 0f)
                val yNoise = map(
                    noise(i / xSmoothness + horizontalMovement, yOrigin / (spacing * ySmoothness) + verticalMovement),
                    0f,
                    1f,
                    -noiseFactor,
                    noiseFactor
                ) * noiseDampening
                yNoise + yOffset
            } else {
                0f
            }
        }

        fun update() {
            horizontalMovement -= movementStep
            verticalMovement += movementStep
//            verticalMovement -= map(noise(horizontalMovement), 0f, 1f, -2f, 2f) * movementStep
        }

    }
}