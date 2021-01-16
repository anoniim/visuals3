package sketch.physics

import BaseSketch
import processing.core.PVector
import kotlin.random.Random

class SoupParticles : BaseSketch() {

    private var drawForce: Boolean = true
    private val strokeColor = grey9
    private val fillColor = grey5
    private val fillColor2 = grey9
    private val backgroundColor = grey3

    private val layer1: MutableList<Circle> = mutableListOf()
    private val layer2: MutableList<Circle> = mutableListOf()
    private val layer3: MutableList<Circle> = mutableListOf()
    private val layer1Count: Int = 300 // 500
    private val layer2Count: Int = 200 // 1000
    private val layer3Count: Int = 200 // 1000
    private val maxRadius = 10.0f

    override fun setup() {
        smooth()
        fill(fillColor)

        for (i in 1..layer1Count) {
            layer1.add(Circle(mass = 1.6F, maxShift = 100F, fill = fillColor2))
        }

        for (i in 1..layer2Count) {
            layer2.add(Circle(mass = 1.2F, maxShift = 200F, fill = orange))
        }

        for (i in 1..layer3Count) {
            layer3.add(Circle(mass = 0.7F, maxShift = 380F, fill = yellow))
        }
    }

    override fun draw() {
        background(backgroundColor)
        translate(screen.widthF/2, screen.heightF/2)

        for (circle in (layer1 + layer2 + layer3)) {
            circle.move()
            circle.draw()
        }
    }

    open inner class Circle(
        private val originX: Float = map(Random.nextFloat(), 0F, 1F, -screen.widthF/2, screen.widthF/2),
        private val originY: Float = map(Random.nextFloat(), 0F, 1F, -screen.heightF, screen.heightF),
//        private val originX: Float = 0F,
//        private val originY: Float = 0F,
        private val mass: Float = 1.5F, // Mass
        private val maxShift: Float = 50F,
        private val fill: Int
    ) {

        private val radius = map(Random.nextFloat(), 0F, 1F, 1F, maxRadius)
        private var location = PVector(originX, originY)
        private var velocity = PVector(0F, 0F)
        private var acceleration = PVector(0F, 0F)
        private var force = PVector(0F, 0F) // Force

        // Spring simulation constants
        var damping = 0.1f // Damping

        private val jitterThreshold = 10F
        private val maxJitter: Float = 20F
        private val jitter: PVector = PVector(
                    map(Random.nextFloat(), 0F, 1F, -maxJitter, maxJitter),
                    map(Random.nextFloat(), 0F, 1F, -maxJitter, maxJitter))


        fun move() {
            // Calculate force
            val targetX = originX + map(mouseXF, 0F, screen.widthF, -maxShift, maxShift)
            val targetY = originY + map(mouseYF, 0F, screen.heightF, -maxShift, maxShift)
//            val targetY = originY + random(-10F, 10F)
            val target = PVector(targetX, targetY)
            force = target.sub(location)

            // Set the acceleration, f=ma == a=f/m
            acceleration = force / mass
            if (abs(force.x) > jitterThreshold || abs(force.y) > jitterThreshold) {
                acceleration.add(jitter)
            }

            // Update velocity
            velocity = (velocity.add(acceleration)).mult(damping)
            if (abs(velocity.x) < 0.1 && abs(velocity.y) < 0.1) {
                velocity = PVector(0.0f, 0F)
            }
//            velocity.limit(10F)

            // Update location
            location = location.add(velocity)
            location.limit(500F)
        }

        fun draw() {
            noStroke()
            fill(fill)
            ellipse(location.x, location.y, radius * 2, radius * 2)
        }
    }
}


