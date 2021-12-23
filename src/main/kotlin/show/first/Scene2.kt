package show.first

import BaseSketch
import processing.core.PConstants.PI
import processing.core.PConstants.TWO_PI
import processing.core.PVector
import show.first.MidiController.Companion.PAD_1

class Scene2(applet: BaseSketch) : Scene(applet) {

    private var time = 0f
    private var color = applet.grey11
    private var angleOffset = applet.random(PI)
    private val controller by lazy { MidiController(applet, 1, 2) }

    init {
        controller.on(PAD_1, { _, _ -> boom() })
    }

    override fun keyPressed(key: Char) {
        if (key == ' ') {
            boom()
        }
    }

    private fun boom() {
        time = 0f
        color = applet.colors.pastel.random()
        angleOffset = applet.random(PI)
    }

    override val update: SketchContext = {
        time += 0.4f

        fun draw() {
            backgroundWithAlpha(color(33, 33, 33, 60))
            stroke(color)
            strokeWeight(15f)
            val numOfPoints = 30
            val radius = time * 10
            val segment = TWO_PI / numOfPoints
            for (theta in 0..numOfPoints) {
                pushMatrix()
                translate(halfWidthF, halfHeightF)
                rotate(angleOffset + theta * segment)
                val noise = 30 * noise(time, theta.toFloat())
                val location = PVector(noise, radius)
                point(location.x, location.y)
                popMatrix()
            }
        }

        draw()
    }
}