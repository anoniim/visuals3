package structures

import processing.core.PApplet.abs
import processing.core.PVector

class QuadTree<Particle: PVector>(x: Float, y: Float, halfDimension: Float) {

    private val boundary = Boundary(x, y, halfDimension)
    private val maxSize: Int = 4
    private val particles = mutableListOf<Particle>()
    private lateinit var northWest: QuadTree<Particle>
    private lateinit var northEast: QuadTree<Particle>
    private lateinit var southEast: QuadTree<Particle>
    private lateinit var southWest: QuadTree<Particle>

    fun insert(particles: Collection<Particle>): Boolean {
        for (particle in particles) {
            if (!insert(particle)) {
                return false
            }
        }
        return true
    }

    fun insert(particle: Particle): Boolean {
        if (!particle.isIn(boundary)) return false

        if (particles.size < maxSize) {
            particles.add(particle)
            return true
        }

        if (!this::northWest.isInitialized) subdivide()
        if (northWest.insert(particle)) return true
        if (northEast.insert(particle)) return true
        if (southEast.insert(particle)) return true
        if (southWest.insert(particle)) return true
        return false
    }

    private fun subdivide() {
        boundary.run {
            val newHalfDimension = halfDimension / 2f
            northWest = QuadTree(x - newHalfDimension, y - newHalfDimension, newHalfDimension)
            northEast = QuadTree(x + newHalfDimension, y - newHalfDimension, newHalfDimension)
            southEast = QuadTree(x + newHalfDimension, y + newHalfDimension, newHalfDimension)
            southWest = QuadTree(x - newHalfDimension, y + newHalfDimension, newHalfDimension)
        }
    }

    fun query(x: Float, y: Float, range: Float): List<Particle> {
        val queryRange = Boundary(x, y, range)
        val particlesInRange = mutableListOf<Particle>()
        if (!queryRange.intersects(boundary)) return particlesInRange

        for (particle in particles) {
            if (particle.isIn(queryRange)) particlesInRange.add(particle)
        }
        if(!this::northWest.isInitialized) return particlesInRange

        particlesInRange.addAll(northWest.query(x, y, range))
        particlesInRange.addAll(northEast.query(x, y, range))
        particlesInRange.addAll(southEast.query(x, y, range))
        particlesInRange.addAll(southWest.query(x, y, range))
        return particlesInRange
    }

    class Boundary(
        val x: Float,
        val y: Float,
        val halfDimension: Float
    ) {
        fun intersects(boundary: Boundary): Boolean {
            val maxDistance = halfDimension + boundary.halfDimension
            return abs(x - boundary.x) < maxDistance
                    && abs(y - boundary.y) < maxDistance
        }
    }

    private fun PVector.isIn(boundary: Boundary): Boolean {
        return x in (boundary.x - boundary.halfDimension..boundary.x + boundary.halfDimension)
                && y in (boundary.y - boundary.halfDimension..boundary.y + boundary.halfDimension)
    }
}