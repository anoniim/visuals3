package session.winter21

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import kotlin.random.Random

fun main() {
    PApplet.main(WaveFieldKeys::class.java)
}

class WaveFieldKeys : BaseSketch(renderer = P3D) {

    // config
    private val numOfPoints = 32
    private val moveSpeed = 4f
    private val addLineOnFrame = 6 // 3 - 20
    private val waveReach = 200f
    private val maxWaveHeight = 100f
    private val waveRiseDelay = 10f // 1-10
    private val sideMargin = 10f
    private val lineColor = green

    private val pointDensity by lazy { (widthF - 2 * sideMargin) / (numOfPoints + 2) }
    private val maxDistance by lazy { -2f * heightF }
    private val lines = mutableListOf<Line>()
    private val notes = mutableListOf<Note>()

    override fun draw() {
        translate(0f, heightF)
        background(grey3)

        notes.forEach {
            it.moveBack()
            it.grow()
            // config
//            drawPointForNote(it)
        }
        lines.forEach {
            it.update()
            it.draw()
        }
        manageLists()
    }

    override fun keyTyped() {
        if (keyPressed && key == ' ') {
            notes.add(Note(Random.nextInt(numOfPoints), random(20f)))
        }
    }

    private fun manageLists() {
        notes.removeIf { it.isVisible() }
        lines.removeIf { !it.isVisible }
        if (frameCount % addLineOnFrame == 0) generateNewLine()
    }

    private fun generateNewLine() {
        lines.add(Line(color = lineColor))
    }

    private fun drawPointForNote(it: Note) {
        stroke(red)
        strokeWeight(it.velocity)
        point(it.position.x, it.position.y, it.position.z)
    }

    private inner class Line(
        val color: Int
    ) {

        var isVisible = true
        private var alpha = 150f
        private var yOffset = 0f
        private var zOffset = 0f

        fun update() {
            moveBack()
            updateIsVisible()
        }

        fun draw() {
            pushMatrix()
            translate(0f, yOffset, zOffset)
            drawLine()
            popMatrix()
        }

        private fun drawLine() {
            stroke(color, alpha)
            strokeWeight(1f)
            noFill()
            beginShape()
            val xExpansion = -zOffset
            val edgeStart = -xExpansion
            vertex(edgeStart, 0f)
            vertex(edgeStart + sideMargin, 0f)
            val waveStart = edgeStart + sideMargin + pointDensity
            for (i in 0 until numOfPoints) {
                // config: turn waves
                // val x = waveStart + i * map(zOffset, maxDistance, 0f, pointDensity - zOffset/10f, pointDensity)
                val x = waveStart + i * pointDensity - zOffset
                val y = getY(i)
                curveVertex(x, y)
            }
            val edgeEnd = widthF + xExpansion
            vertex(edgeEnd - sideMargin - pointDensity, 0f)
            vertex(edgeEnd, 0f)
            endShape()
        }

        private fun getY(i: Int): Float {
            val samePitchNotes = notes.filter { note -> note.pitch == i }
            val closestNote = samePitchNotes.findClosest()
            return if (closestNote != null) {
                val distance = abs(yOffset - closestNote.position.y)
                if (distance < waveReach) {
                    -1 * closestNote.getWaveHeight(distance)
                } else 0f
            } else 0f
        }

        private fun List<Note>.findClosest(): Note? {
            var closestNote: Note? = null
            var minDistance = Float.MAX_VALUE
            forEach {
                val distance = abs(yOffset - it.position.y)
                if (distance < minDistance) {
                    minDistance = distance
                    closestNote = it
                }
            }
            return closestNote
        }

        private fun Note.getWaveHeight(distance: Float) = constrain(
            map(distance, 0f, waveReach, waveHeight, 0f),
            0f, maxWaveHeight
        )

        private fun moveBack() {
            yOffset -= moveSpeed
            zOffset -= moveSpeed
        }

        private fun updateIsVisible() {
            if (yOffset < maxDistance) isVisible = false
        }

    }

    private inner class Note(val pitch: Int, val velocity: Float) {

        val position: PVector = PVector(pointDensity * (2 + pitch), 0f)
        var waveHeight: Float = 0f
        var time: Float = 0f

        fun moveBack() {
            position.y -= moveSpeed
            position.z -= moveSpeed
        }

        fun grow() {
            time += 0.1f
            waveHeight =
                if (waveHeight < maxWaveHeight) map(time, 0f, waveRiseDelay, 0f, maxWaveHeight) else maxWaveHeight
        }

        fun isVisible(): Boolean {
            return position.y < maxDistance
        }
    }
}
