package sketch.waves

import BaseSketch
import Screen
import processing.core.PVector
import java.util.*

class SineFlower : BaseSketch(Screen(800, 800)) {

    private val numLines = 8
    private val lineLength = 600
    private val minStrokeWeight = 0.5f
    private val maxStrokeWeight = 5f
    private val endColor = 200
    private val startColor = 70
    private val scatter = 1.008f
    private val frameDivisor = 2f
    private val amplitude = 5f

    private val lines = MutableList(numLines) { index ->
        Line(index * TWO_PI / numLines)
    }
    private var hasCurvingStarted = false

    override fun draw() {
        translate(screen.centerX, screen.centerY)
        background(grey3)

        for (line in lines) {
            if (frameCount < lineLength) {
                line.grow(lineLength - frameCount)
            }
            if (frameCount >= lineLength) {
                hasCurvingStarted = true
                line.curve(frameCount)
            }
            line.show()
        }
    }

    inner class Line(val angle: Float) {

        private val path = LinkedList<PVector>()
        private var lineColor: Int = grey7

        fun grow(distance: Int) {
            path.add(PVector(distance.toFloat(), 0f))
        }

        fun curve(frameCount: Int) {
            shiftBack()
            path.removeFirst()
            addNewCurved(frameCount)
        }

        private fun shiftBack() {
            val xArray = mutableListOf(path[0].x)
            for (i in 1 until path.size - 1) {
                val current = path[i]
                xArray.add(i, current.x)
                current.set(xArray[i - 1], current.y * scatter)
            }
        }

        private fun addNewCurved(frameCount: Int) {
            path.add(createNewPoint(frameCount))
            lineColor = calculateColor(frameCount)
        }

        private fun createNewPoint(frameCount: Int): PVector {
            val yOffset = sin(frameCount / frameDivisor) * amplitude
            pushMatrix()
            rotate(-angle)
            val yOffsetVector = PVector(0f, yOffset)
            val newPoint = path.last.copy().add(yOffsetVector)
            popMatrix()
            return newPoint
        }

        private fun calculateColor(frameCount: Int): Int {
            val progression = constrain(frameCount - lineLength, 0, 1000)
            val green = floor(map(progression, 0, 1000, startColor, endColor))
            return color(70, green, 70)
        }

        fun show() {
            pushMatrix()
            rotate(-angle)
            noFill()
            for (i in 1 until path.size) {
                strokeWeight(map(i.toFloat(), 1f, lineLength.toFloat(), maxStrokeWeight, minStrokeWeight))
                stroke(lineColor)
                val previous = path[i - 1]
                val current = path[i]
                line(previous.x, previous.y, current.x, current.y)
            }
            popMatrix()
        }
    }
}