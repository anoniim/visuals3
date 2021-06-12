package sketch.perspective

import BaseSketch
import Screen
import util.*

class FoldingStripes : BaseSketch(
    Screen(),
    fullscreen = false,
    renderer = P3D,
    smoothLevel = 8
) {

    // config
    private val beamColors = colors.blackAndWhite
    private val timeSpeed = 10f

    private val beamCount = 80
    private val beamWidth: Float by lazy {
        widthF / beamCount
    }

    private val upBeams: MutableList<Beam> by lazy { MutableList(beamCount) { Beam(it) } }
    private val downBeams: MutableList<Beam> by lazy { MutableList(beamCount) { Beam(it, -1f) } }

    private var folding: Boolean = false
    private var spinning: Boolean = false

    override fun keyPressed() {
        when (key) {
            'f' -> folding = !folding
            's' -> spinning = !spinning
            'd' -> spinning = !spinning
        }
    }

    override fun draw() {
        translate(0f, halfHeightF)
        background(grey11)
        drawBeams()
    }

    private fun drawBeams() {
        (upBeams + downBeams).forEach {
            it.draw()
            it.update()
        }
    }

    private inner class Beam(var index: Int, val yDirection: Float = 1f) {

        private val color = beamColors.getRolling(index)

        private var x = index * beamWidth
        private var z = 0f
        private var zDirection = 1f

        fun update() {
            fold()
            spin()
        }

        private fun spin() {
            if (spinning) {
                x += timeSpeed / 10f
                if (x > widthF) x -= beamCount * beamWidth
            }
        }

        private fun fold() {
            if (folding) {
                z += zDirection * timeSpeed
                if (z > 7400f || z < 0f) zDirection *= -1
            }
        }

        fun draw() {
            drawBeam()
        }

        private fun drawBeam() {
            noStroke()
            fill(color)
            beginShape()
            vertex(x, 0f, 0f)
            vertex(x, -yDirection * halfHeightF, z)
            vertex(x + beamWidth, -yDirection * halfHeightF, z)
            vertex(x + beamWidth, 0f, 0f)
            endShape(CLOSE)
        }
    }
}
