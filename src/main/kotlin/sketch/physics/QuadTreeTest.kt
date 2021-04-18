package sketch.physics

import BaseSketch
import Screen
import processing.core.PVector
import structures.QuadTree

class QuadTreeTest : BaseSketch(screen = Screen(800, 800), renderer = P2D) {

    private val particles = MutableList(200) {
        Particle(PVector(random(screen.widthF), random(screen.heightF)))
    }
    val qTree = QuadTree<Particle>(screen.centerX, screen.centerY, screen.centerX)

    override fun setup() {
//        smooth()
        val result = qTree.insert(particles)
        println(result)
    }

    override fun draw() {
        background(grey3)

        for (particle in particles) {
            strokeWeight(5f)
            stroke(grey11)
            point(particle.x, particle.y)
        }

        val inRange = qTree.query(mouseXF, mouseYF, 100f)
        for (particle in inRange) {
            strokeWeight(15f)
            stroke(red)
            point(particle.x, particle.y)
        }
    }

    class Particle(position: PVector): QuadTree.Particle {
        override var x: Float = position.x
        override var y: Float = position.y
    }
}


