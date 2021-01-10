package spirals

import BaseSketch
import Screen


class SimpleSpiral : BaseSketch(Screen(800, 800), P2D) {

    override fun draw() {
        translate(screen.centerX, screen.centerY)
        background(grey3)
        noStroke()
        noFill()
        drawSpiral()
    }

    private fun drawSpiral() {
        stroke(red)
        for (i in 0..820) {
            val angle = i.toFloat()
            val r = angle * 5f
            arc(0f, 0f, r, r, angle-1f, angle)
        }
    }
}