package sketch.physics

import BaseSketch
import processing.core.PVector
import kotlin.random.Random


class Dot : BaseSketch() {

    private val strokeColor = grey9
    private val fillColor = grey5
    private val fillColor2 = grey9
    private val backgroundColor = grey3

    private val maxRadius: Float = 100F
    private val dot = Dot(maxShift = 300F, fill = fillColor2)

    // Spring drawing constants for top bar
    var springHeight = 32 // Height
    var left = 0 // Left position= 0
    var right = 0// Right position= 0
    var max = 200 // Maximum Y value
    var min = 100 // Minimum Y value
    var move = false // If mouse down and over




    override fun setup() {
//        smooth()
//        frameRate(1F)
        fill(fillColor)

        rectMode(CORNERS)
        noStroke()
        left = width / 2 - 100
        right = width / 2 + 100
    }

    override fun draw() {
        background(backgroundColor)

        translate(screen.widthF / 2, screen.heightF / 2)

        dot.move()
        dot.draw()
//        dot.updateSpring()
//        dot.drawSpring()
    }

    private inner class Dot(
//        private val originX: Float = map(Random.nextFloat(), 0F, 1F, -screen.widthF, screen.widthF),
//        private val originY: Float = map(Random.nextFloat(), 0F, 1F, -screen.heightF, screen.heightF),
        private val originX: Float = 0F,
        private val originY: Float = 0F,
        val maxShift: Float = 50F, val fill: Int
    ) {

        private val radius = map(Random.nextFloat(), 0F, 1F, 10F, maxRadius)
        private var location = PVector(originX, originY)
        private var velocity = PVector(0F, 0F)
        private var acceleration = PVector(0F, 0.1F)
        private var force = PVector(0F, 0F) // Force

        // Spring simulation constants
        var mass = 1.5f // Mass
        var damping = 0.1f // Damping

        fun move() {
            val targetX = originX + map(mouseXF, 0F, screen.widthF, -maxShift, maxShift)
            val targetY = originY + map(mouseYF, 0F, screen.heightF, -maxShift, maxShift)
//            val targetY = originY + random(-10F, 10F)
            val target = PVector(targetX, targetY)
//            target.mult(0.5F)

            force = target.sub(location)

            stroke(255F)
            line(0F, 0F, target.x, target.y)

            acceleration = force / mass // Set the acceleration, f=ma == a=f/m
            velocity = (velocity.add(acceleration)).mult(damping) // Set the velocity
            location = location.add(velocity) // Updated position
            if (abs(velocity.x) < 0.1 && abs(velocity.y) < 0.1) {
                velocity = PVector(0.0f, 0F)
            }
            velocity.limit(10F)
            location.limit(500F)
        }

        fun draw() {
            noStroke()
            fill(fill)
            ellipse(location.x, location.y, radius * 2, radius * 2)
        }
    }
}


