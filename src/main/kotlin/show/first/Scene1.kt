package show.first

import BaseSketch
import input.MidiController
import processing.core.PConstants.*
import processing.core.PVector
import input.MidiController.Companion.PAD_1
import util.plus
import util.times

class Scene1(applet: BaseSketch) : Scene(applet) {

    private val maxSpeed = 5

    private val controller by lazy { MidiController(applet, 1, 2) }
    private val path by lazy { generatePath() }
    private var position = 0

    override val update: SketchContext = {

        fun shouldSpeedUp() = (keyPressed && key == ' ') || controller.isOn(PAD_1)

        fun update() {
            if (position < path.size - maxSpeed) {
                val speed = if (shouldSpeedUp()) maxSpeed else 1
                position += speed
            } else {
                position = path.size - 1
            }
        }

        fun draw() {
            background(grey3)
            stroke(grey11)
            strokeWeight(15f)
            val location = path[position]
            point(location.x, location.y)
        }

        update()
        draw()
    }

    private fun generatePath(): List<PVector> {
        with(applet) {
            val start = PVector(halfWidthF, halfHeightF)
            return List(1000) {
                if (it == 0) {
                    start
                } else {
                    val angle = QUARTER_PI - noise(it / 160f) * HALF_PI
                    start.copy() + (PVector.fromAngle(angle) * it.toFloat())
                }
            }.asReversed()
        }
    }
}
