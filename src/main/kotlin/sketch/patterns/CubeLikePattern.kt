package sketch.patterns

import BaseSketch
import processing.core.PVector
import structures.Grid
import util.plus
import util.vertex

class CubeLikePattern : BaseSketch() {

    private val grid = Grid(screen, 80f) { n, x, y ->
        TShape(n, x, y)
    }

    override fun setup() {
//        noLoop()
    }

    override fun draw() {
        background(grey3)
        grid.updateAndDraw()
    }

    private inner class TShape(val n: Int, val x: Float, val y: Float) : Grid.ChangingItem() {

        val origin = PVector(x, y)
        private var angleRotation = 0f
        val direction = if (random(1f) < 0.5f) 1 else -1

        override fun update() {
            angleRotation += direction * 0.01f
        }

        private val evenAngles = List(3) { HALF_PI + it * TWO_PI / 3 }
        private val oddAngles = List(3) { -HALF_PI + it * TWO_PI / 3 }

        override fun draw() {
            stroke(grey11)
            strokeWeight(3f)
            noFill()
            val row = n / grid.numOfCols
            val xOffset = if (row % 2 == 0) grid.itemSize / 2 else 0f
            for (angle in evenAngles) {
                val adjustedOrigin = PVector(xOffset, 0f) + origin

                beginShape()
                vertex(adjustedOrigin)
                val v1 = PVector.fromAngle(angleRotation + angle)
                    .setMag(grid.itemSize * 3 / 5)
                vertex(adjustedOrigin.copy() + v1)
                val v3 = PVector.fromAngle(angleRotation + angle - TWO_PI / 3)
                    .setMag(grid.itemSize * 3 / 5)
                val v2 = v1.copy() + v3.copy()
                vertex(adjustedOrigin.copy() + v2)
                vertex(adjustedOrigin.copy() + v3)
                endShape()
//                    line(adjustedOrigin.x, adjustedOrigin.y, v1.x, v1.y)
            }
        }

        override fun refresh() {

        }

    }
}
