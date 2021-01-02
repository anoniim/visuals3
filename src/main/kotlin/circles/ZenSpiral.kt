package circles

import BaseSketch
import Screen

class ZenSpiral: BaseSketch(Screen(1200, 800, fullscreen = true)) {

    private val speed = 0.05f // 0.05f
    private val heightThreshold = 1.3f // 1.32f / 1.3f when fullscreen
    private val diameterStep = 0.5f // 0.5f

    private var clockwise = 1
    private var filling: Boolean = true
    private var radianProgress: Float = 0f
    private var diameter: Float = 0f

    override fun setup() {
        frameRate(20f)
        background(grey3)
        noFill()
        stroke(grey11)
        strokeWeight(1f)
    }

    override fun draw() {
        radianProgress += clockwise * speed
        diameter += clockwise * diameterStep
        if (diameter > heightThreshold * height) {
            reverseDirection(true)
        } else if (diameter < 0) {
            reverseDirection(false)
        }
        arc(width/2f, height/2f, diameter, diameter, radianProgress, radianProgress + speed)
    }

    private fun reverseDirection(reachedEnd: Boolean) {
        clockwise *= -1
        if (reachedEnd) {
            diameter -= diameter / (2 * (radianProgress / TWO_PI))
        } else {
            diameter = 0f
            radianProgress = 0f
            reverseFilling()
        }
    }

    private fun reverseFilling() {
        if (filling) {
            strokeWeight(4f)
            stroke(grey3)
        } else {
            stroke(grey11)
            strokeWeight(1f)
        }
        filling = !filling
    }
}