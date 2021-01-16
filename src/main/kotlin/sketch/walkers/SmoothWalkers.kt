package sketch.walkers

import BaseSketch
import java.util.concurrent.ConcurrentLinkedQueue

class SmoothWalkers : BaseSketch() {

    private val numOfWalkers = 10 // <300
    private val velocity: Float = 1F // 0.5-10
    private val initialDirectionVariance: Int = 4 // 1..10
    private val noiseStepDifference = 0.005F // 0.005-0.03
//    private val noiseStepDifference: Float
//        get() = (5..30).random()/100F // Cool flooding effect
    private val timeImportance = 0.001F // 0.001

    override fun setup() {
        smooth()
        background(grey1)
        fill(transparentLight)
        stroke(transparentLight)
    }

    // ConcurrentLinkedQueue used to prevent concurrent modification exception
    // when removing walkers that went out of the screen
    private val walkers: ConcurrentLinkedQueue<Walker> = ConcurrentLinkedQueue()

    override fun draw() {
        walkers.forEach { walker ->
            walker.apply {
                if (!isOut()) {
                    move()
                    draw()
                } else {
                    walkers.remove(this)
                }
            }
        }

        if (mousePressed) {
            for (i in 1..numOfWalkers) {
                walkers.add(Walker(mouseXF, mouseYF))
            }
        }
    }

//    override fun mouseClicked() {
//        for (i in 1..numOfWalkers) {
//            walkers.add(Walker(mouseXF, mouseYF))
//        }
//    }

    inner class Walker(var x: Float, var y: Float) {

        private val initialVelocity: Float
            get() = (-initialDirectionVariance..initialDirectionVariance).random().toFloat()

        var px = x
        var py = y
        var velocityX = initialVelocity
        var velocityY = initialVelocity

        fun draw() {
            line(px, py, x, y)
            px = x
            py = y
        }

        fun isOut(): Boolean {
            return x < 0 || x > width || y < 0 || y > height
        }

        fun move() {
            x += velocityX
            y += velocityY
            updateVelocity()
        }

        private fun updateVelocity() {
            velocityX += map(noise(x * noiseStepDifference, y * noiseStepDifference, millis() * timeImportance),
                0F, 1F, -velocity, velocity)
            velocityY += map(noise(y * noiseStepDifference, x * noiseStepDifference, millis() * timeImportance),
                0F, 1F, -velocity, velocity)
        }
    }
}