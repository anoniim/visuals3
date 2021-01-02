package circles

import BaseSketch
import Screen

class Spiral: BaseSketch(Screen(1200, 800)) {

    private val speed = 0.05f // 0.05f
    private val heightThreshold = 1.32f // 1.32f
    private val diameterStep = 0.5f

    private var clockwise: Boolean = true
    private var radianProgress: Float = 0f
    private var diameter: Float = 0f

    override fun setup() {
        frameRate(130f)
        background(grey3)
        stroke(grey11)
    }

    override fun mousePressed() {
        switch()
    }

    override fun draw() {
        noFill()
        arc(screen.centerX, screen.centerY, diameter, diameter, radianProgress, radianProgress + speed)
        if (clockwise) {
            radianProgress += speed
            diameter += diameterStep
        } else {
            radianProgress -= speed
            diameter -= diameterStep
        }
        if (diameter > heightThreshold * screen.heightF) {
            switch()
        } else if (diameter < 0) {
            switch()
            diameter = 0f
            strokeWeight(4f)
            stroke(grey3)
        }
    }

    private fun switch() {
        clockwise = !clockwise
        diameter -= diameter / (2 * (radianProgress / TWO_PI))
    }
}