package sketch.synchronization

import BaseSketch
import processing.core.PShape
import processing.core.PVector

class SineWaveSync : BaseSketch(renderer = P2D) {

    private val backgroundAlpha = 255f // 10f To see the dots are moving in a straight line
    private val drawGuide = false

    private val phaseDiff = PI
    private val extent = calculateExtent()
    private val xEnd = calculateXEnd()
    private val yOrigin = 0f // screen.centerY

    private val sines = listOf(
        Sine(50, PVector(0f, yOrigin), 1f, 0),
        Sine(50, PVector(xEnd, yOrigin), -1f, 0),
        Sine(50, PVector(0f, yOrigin + extent * 1.5f), 1f, 1),
        Sine(50, PVector(xEnd, yOrigin + extent * 1.5f), -1f, 1),
        Sine(50, PVector(0f, yOrigin + extent * 3f), 1f, 2),
        Sine(50, PVector(xEnd, yOrigin + extent * 3f), -1f, 2),
        Sine(50, PVector(0f, yOrigin + extent * 4.5f), 1f, 3),
        Sine(50, PVector(xEnd, yOrigin + extent * 4.5f), -1f, 3),
        Sine(50, PVector(0f, yOrigin + extent * 6f), 1f, 4),
        Sine(50, PVector(xEnd, yOrigin + extent * 6f), -1f, 4),
        Sine(50, PVector(0f, yOrigin + extent * 7.5f), 1f, 5),
        Sine(50, PVector(xEnd, yOrigin + extent * 7.5f), -1f, 5),
        Sine(50, PVector(0f, yOrigin + extent * 9f), 1f, 6),
        Sine(50, PVector(xEnd, yOrigin + extent * 9f), -1f, 6),
        Sine(50, PVector(0f, yOrigin + extent * 10.5f), 1f, 7),
        Sine(50, PVector(xEnd, yOrigin + extent * 10.5f), -1f, 7),
        Sine(50, PVector(0f, yOrigin + extent * 12f), 1f, 8),
        Sine(50, PVector(xEnd, yOrigin + extent * 12f), -1f, 8),
    )

    private var time = 0f

    override fun setup() {
        background(grey3)
    }

    override fun draw() {
        drawBackground()
//        drawExtentText()

        for (sine in sines) {
            sine.update()
            sine.show()
        }

        if (!keyPressed) {
            time += 0.04f
        }
    }

    private inner class Sine(
        numOfPoints: Int,
        val origin: PVector,
        val direction: Float,
        order: Int,
    ) {

        private val points = MutableList(numOfPoints) {
            Point(HALF_PI) { time, phase ->
                val offTime = if (direction < 0) {
                    time - QUARTER_PI
                } else {
                    time
                } - order * PI
                sinePathFunction(offTime, phase, it)
            }
        }

        private fun sinePathFunction(time: Float, phase: Float, it: Int): PVector {
            val directedTime = direction * time
            val directedPhase = -direction * phase
            val x = extent * (it * directedPhase + directedTime)
            val y = extent * sin(it * directedPhase + directedTime)
            return PVector(x, y)
        }

        val guideShape by lazy { createGuideShape() }

        private fun createGuideShape(): PShape {
            return createShape().apply {
                beginShape()
                repeat(2500) {
                    val guidePoint = sinePathFunction(it / extent, 0f, 0)
                    vertex(guidePoint.x, guidePoint.y)
                }
                endShape()
            }
        }

        fun update() {
            for (point in points) {
                point.update()
            }
        }

        fun show() {
            pushMatrix()
            translate(origin.x, origin.y)
            if (drawGuide) {
                drawGuide()
            }
            points.forEach { it.show() }
            popMatrix()
        }

        fun drawGuide() {
            strokeWeight(2f)
            stroke(grey7)
            noFill()
            shape(guideShape)
        }

    }

    private inner class Point(
        val phase: Float,
        val pathFunction: (Float, Float) -> PVector
    ) {

        var position: PVector = PVector()

        fun update() {
            position = pathFunction(time, phase)
        }

        fun show() {
            stroke(white)
            strokeWeight(20f)
            point(position.x, position.y)
        }
    }

    private fun drawBackground() {
        noStroke()
        fill(grey3, backgroundAlpha)
        rect(0f, 0f, screen.widthF, screen.heightF)
    }

    private fun drawExtentText() {
        fill(grey5)
        textSize(30f)
        text(extent, 30f, 30f)
    }

    private fun calculateXEnd(): Float {
        val scaledPhaseDiff = phaseDiff * extent
        var howManyTimesItFits = floor((screen.widthF / scaledPhaseDiff)) + 1
        if (howManyTimesItFits % 2 == 0) {
            howManyTimesItFits++
        }
        return howManyTimesItFits * scaledPhaseDiff
    }

    private fun calculateExtent(): Float {
//        screen.widthF/ (6 * PI)
        return 72.757f
//        map(mouseXF, 0f, screen.widthF,10f, 100f)
    }
}


