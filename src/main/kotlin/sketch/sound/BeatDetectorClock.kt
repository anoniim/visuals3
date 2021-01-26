package sketch.sound

import BaseSketch
import Screen

class BeatDetectorClock : BaseSketch(Screen(1500, 800)) {

    private val bands = 256 // power of 2 (256)
    private val fft by lazy { sound.fft(bands) }
    private val hands = List(3) {
        Line(it, -HALF_PI)
    }

    override fun setup() {
    }

    override fun draw() {
        background(grey3)
        translate(screen.centerX, screen.centerY)

        drawCircle()
        drawHands()

//        fft.analyze()
    }

    private fun drawCircle() {
        strokeWeight(2f)
        stroke(grey7)
        noFill()
        circle(0f, 0f, 600f)
    }

    private fun drawHands() {
        strokeWeight(2f)
        stroke(grey7)
        for (hand in hands) {
            hand.update()
            hand.show()
        }
    }

    inner class Line(val index: Int, startAngle: Float) {

        private var length: Float = 280f
        private var angle = startAngle
        private var targetAngle = startAngle
        private var angleStep = 0.02f
        private var angleJump = TWO_PI/60f
        private var sampleSum = 0f

        fun update() {
            val i = (frameCount - 10) % 61
            if (i == index * 14) {
                targetAngle += angleJump
            }
            if (angle < targetAngle) {
                angle += angleStep
            }
        }

        fun show() {
            strokeWeight(4f - index.toFloat())
            val x = cos(angle) * length
            val y = sin(angle) * length
            line(0f, 0f, x, y)
        }
    }
}