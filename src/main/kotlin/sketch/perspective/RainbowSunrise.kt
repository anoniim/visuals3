package sketch.perspective

import BaseSketch
import Screen
import processing.core.PVector
import util.*

class RainbowSunrise : BaseSketch(
    Screen(fullscreen = false),
    renderer = P2D,
    smoothLevel = 8
) {

    // config
    private val beamColors = colors.sunrise.shuffled()
    private val timeSpeed = 0.005f
    private val slowSpin = 0.001f
    private val fastSpin = 0.01f

    private val beamExtent: Float = 3000f
    private val beams: MutableList<Beam> by lazy { MutableList(20) { Beam(it) } }

    private var time = 0f

    override fun draw() {
        riseSun()
        drawBeams()
         time += timeSpeed
    }

    private fun riseSun() {
        val yOffset = map(sin(time), -1f, 1f, 2f, 1/2f)
        translate(halfWidthF, yOffset * heightF)
    }

    private fun drawBeams() {
        beams.forEach {
            it.draw()
        }
    }

    private inner class Beam(var index: Int) {

        private val color = beamColors.getRolling(index)
        private val angleSegment by lazy { TWO_PI / beams.size }
        private val spinSpeed
            get() = map(sin(time), -1f, 1f, slowSpin, fastSpin)

        private var spin = 0f

        fun draw() {
            drawBeam()
            spin += spinSpeed
        }

        private fun drawBeam() {
            val vectorStart = PVector.fromAngle(index * angleSegment + spin)
            val vectorEnd = PVector.fromAngle((index + 1) * angleSegment + spin)

            noStroke()
            fill(color)
            beginShape()
            vertex(vectorStart)
            vertex(vectorEnd)
            vertex(vectorEnd.setMag(beamExtent))
            vertex(vectorStart.setMag(beamExtent))
            endShape(CLOSE)
        }
    }
}
