package session.winter21

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import show.first.MidiController
import util.circle
import util.translateToCenter

fun main() {
    PApplet.main(FeedingSnake::class.java)
}

class FeedingSnake: BaseSketch() {

    private val dampening = 0.95f

    private val snake = Snake()
    private val midiController by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        midiController.on(MidiController.PAD_1..MidiController.PAD_16, { pitch, velocity ->
            val directionAngle = ((pitch - 20) / 8f) * PI
            snake.move(directionAngle, velocity.toFloat())
        })

    }
    override fun draw() {
        translateToCenter()
        background(grey3)

        snake.update()
        snake.draw()
    }

    private inner class Snake() {

        val segments = mutableListOf<Segment>()
        var position = PVector()
        var acceleration = PVector()
        var velocity = PVector()

        @Synchronized
        fun move(angle: Float, strength: Float) {
            acceleration.add(PVector.fromAngle(angle).mult(strength/100f))
            velocity.add(acceleration).mult(dampening)
        }

        fun update() {
            position.add(velocity)
            segments.add(Segment(position.copy(), 50f))
        }

        @Synchronized
        fun draw() {
            segments.forEach {
                it.draw()
            }
        }
    }

    private inner class Segment(
        val position: PVector,
        val size: Float) {

        fun draw() {
            noFill()
            stroke(grey11)
            strokeWeight(1f)
            circle(position, size * 2)
        }
    }
}