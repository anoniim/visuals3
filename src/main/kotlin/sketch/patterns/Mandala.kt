package sketch.patterns

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import util.circle
import util.curveVertex
import util.translateToCenter

fun main() {
    PApplet.main(Mandala::class.java)
}

class Mandala : BaseSketch(
    fullscreen = true
) {

    private val scrollSpeed = 0.1f
    private val lineCount = 40
    private val r1 = 50f
    private val r2 = 100f
    private val r3 = 110f
    private val r4 = 140f
    private val r5 = 150f
    private val r6 = 190f
    private val backgroundColor = grey3
    private var rotation = 0f
    private var lineStrokeWeight = 13f

    private val lines2 by lazy { createLinesInCircle(r2, 1f) }
    private val lines3 by lazy { createLinesInCircle(r3, -1f) }
    private val lines4 by lazy { createLinesInCircle(r4, -1f) }
    private val lines5 by lazy { createLinesInCircle(r5, -1f) }
    private val lines6 by lazy { createLinesInCircle(r6, 1f) }

    override fun draw() {
        translateToCenter()
        background(backgroundColor)


        for (x in -400..400 step 400) {
            for (y in -400..400 step 400) {
                pushMatrix()
                translate(x.toFloat(), y.toFloat())
                val center = PVector(0f, 0f)
                drawMandala(center)
                popMatrix()
            }
        }

        rotation += 0.005f
    }

    private fun drawMandala(center: PVector) {
        drawCircle(center, r6)
        drawLines(lines6)
        drawCircle(center, r5)
        //        drawLines(lines5)
        drawCircle(center, r4)
        drawLines(lines4)
        drawCircle(center, r3)
        //        drawLines(lines3)
        drawCircle(center, r2)
        drawLines(lines2)
        drawCircle(center, r1)
        //        drawBackground(r6)
    }

    private fun drawCircle(center: PVector, radius: Float) {
        fill(backgroundColor)
        stroke(grey11)
        noStroke()
//        strokeWeight(3f)
        circle(center, 2 * radius)
    }

    private fun drawLines(lines: List<Line>) {
        lines.forEach {
            it.update()
            it.draw()
        }
    }

    private fun drawBackground(contourRadius: Float) {
        noStroke()
        fill(backgroundColor)
        beginShape()
        vertex(-halfWidthF, -halfHeightF)
        vertex(-halfWidthF, halfHeightF)
        vertex(halfWidthF, halfHeightF)
        vertex(halfWidthF, -halfHeightF)
        vertex(-halfWidthF, -halfHeightF)
        beginContour()
        val angle = TWO_PI / 12
        for (i in 0..14) {
            curveVertex(PVector.fromAngle(i * angle).setMag(contourRadius))
        }
        endContour()
        endShape()
    }

    private fun createLinesInCircle(radius: Float, direction: Float) = List(lineCount) {
        Line(it, radius, scrollSpeed, direction)
    }

    private inner class Line(val index: Int, val radius: Float, val speed: Float, val direction: Float) {

        val spacing = heightF / lineCount
        var y = -halfHeightF + index * spacing
        var x = 0f

        fun update() {
            y += direction * speed
            if (y > halfHeightF) {
                y = -halfHeightF
            }
            if (y < -halfHeightF) {
                y = halfHeightF
            }
            x = radius * cos((y/radius) * HALF_PI)
//            x = sqrt(sq(radius) - sq(y))
        }

        fun draw() {
            if (y < -radius || y > radius) return
            pushMatrix()
            if (direction == 1f) {
                rotate(rotation)
            }
            doDraw()
            popMatrix()
        }

        private fun doDraw() {
            if(index % 2 == 0) stroke(blue) else stroke(purple)
            strokeWeight(lineStrokeWeight)
            line(-x, y, x, y)
        }
    }
}
