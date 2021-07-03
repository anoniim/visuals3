package sketch.cells

import BaseSketch
import Screen
import processing.core.PVector
import structures.QuadTree

/**
 * It's too slow! QuadTree didn't help (I might need to improve QuadTree implementation).
 */
class WorleyOutline : BaseSketch(Screen(500, 500)) {

    // config
    private val depth = 100
    private val numOfPoints = 50
    private val speed = 2
    private val maxDistance by lazy { widthF / 4f }

    private lateinit var qTree: QuadTree<PVector>
    private lateinit var points: List<PVector>
    private var z = 0

    override fun setup() {
        qTree = QuadTree(halfWidthF, halfHeightF, halfWidthF)
        points = List(numOfPoints) {
            PVector(random(widthF), random(heightF), random(depth))
                .also { qTree.insert(it) }
        }
    }

    override fun draw() {
        background(grey11)
        drawOutlines()
        z += speed
        println("frame")
    }

    private fun drawOutlines() {
        beginShape()
        val currentZ = z % depth
        for (x in 0 until width) {
            for (y in 0 until height) {
                // TODO
                val distances = getDistances(x, y, z)
//                pixels[y * width + x] =
            }
        }
        endShape()
    }

    private fun getDistances(x: Int, y: Int, z: Int): List<Float> {
        val distances = mutableListOf<Float>()
        qTree.query(x.toFloat(), y.toFloat(), maxDistance).forEach {
            val distance = PVector(x.toFloat(), y.toFloat(), z.toFloat()).dist(it)
            distances.add(distance)
        }
        return distances.sorted()
    }
}