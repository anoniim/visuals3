package sketch.perspective

import BaseSketch
import peasy.PeasyCam
import processing.core.PVector
import structures.Grid
import util.translateToCenter

class GrassFromAbove : BaseSketch(
    renderer = P3D
) {

    private val spacing: Float = 20f
    private val grass by lazy { generateGrass() }
    private val cam by lazy { PeasyCam(this, 100.0) }

    private fun generateGrass(): Grid<Blade> {
        return Grid(screen, spacing) { n, x, y ->
            Blade(PVector(x, y), 50f)
        }
    }

    override fun draw() {
        background(grey3)
        translateToCenter()
        cam.rotateY(map(mouseXF, 0f, widthF, -0.1f, 0.1f).toDouble())
        grass.updateAndDraw()
    }

    private inner class Blade(val position: PVector, val length: Float) : Grid.SimpleItem() {
        override fun update() {

        }

        override fun draw() {
            strokeWeight(1f)
            stroke(green)
            line(position.x, position.y, 0f, position.x, position.y, length)
        }
    }
}