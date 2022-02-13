package sketch.circles

import BaseSketch
import processing.core.PApplet
import util.translateToCenter

fun main() {
    PApplet.main(TwistedSunBeam::class.java)
}

class TwistedSunBeam : BaseSketch(
    screen = Screen.PIXELBOOK_GO
) {

    // config
    private val circleRadius = 150f
    private val numOfBeams = 80
    private val growthSpeed = 6f
    private val noiseFactor = 5f // 0.5 .. 5

    private val beams = List(numOfBeams) {
        Beam(it * TWO_PI / numOfBeams)
    }

    override fun draw() {
        translateToCenter()
        background(grey3)

        beams.forEach {
            it.update()
            it.draw()
        }
    }

    private inner class Beam(val angle: Float) {

        private var length = 0f
        private var progress = 0f

        fun update() {
            if (length < 300f) {
                progress += 0.01f
                length += growthSpeed * noise(noiseFactor * angle, progress)
            }
        }

        fun draw() {
            strokeWeight(2f)
            stroke(grey11)
            pushMatrix()
            rotate(angle)
            line(circleRadius, 0f, circleRadius + length, mouseXF + noise(noiseFactor * angle, progress))
            popMatrix()
        }
    }
}
