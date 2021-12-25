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

    private val repelArea = 100f

    private val snake = Snake()
    private val midiController by lazy { MidiController(this, 1, 2) }

    override fun setup() {
        midiController.on(MidiController.PAD_1..MidiController.PAD_16, { pitch, velocity ->
            val directionAngle = ((pitch - 12) / 15f) * TWO_PI
            snake.move(directionAngle, velocity.toFloat())
        })
    }

    override fun draw() {
        translateToCenter()
        background(grey3)

        snake.update()
        snake.edges()
        snake.draw()
    }

    private inner class Snake() {

        val segments = mutableListOf<Segment>()
        var position = PVector()
        var acceleration = PVector()
        var velocity = PVector()
        private var size = 10f

        @Synchronized
        fun move(angle: Float, strength: Float) {
            acceleration.add(PVector.fromAngle(angle))
            velocity.add(acceleration).normalize().mult(0.9f)
            acceleration = PVector()
            size += (strength - size) * 0.07f
        }


        fun update() {
            position.add(velocity)
            if(frameCount % 4 == 0) segments.add(Segment(position.copy(), size))
        }

        @Synchronized
        fun draw() {
            segments.forEach {
                it.draw()
            }
        }

        fun edges() {
            when {
                position.x < -halfWidthF + repelArea -> move(HALF_PI, size)
                position.x > halfWidthF - repelArea -> move(-HALF_PI, size)
                position.y < -halfHeightF + repelArea -> move(HALF_PI, size)
                position.y > halfHeightF - repelArea -> move(-HALF_PI, size)
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