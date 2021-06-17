package sketch.cells

import BaseSketch
import Screen
import processing.core.PVector
import structures.QuadTree

/**
 * It's too slow! QuadTree didn't help (I might need to improve QuadTree implementation).
 */
class WorleyNoiseCells : BaseSketch(Screen(500, 500)) {

    // config
    private val depth = 100
    private val numOfPoints = 50
    private val speed = 2
    private val maxDistance by lazy { widthF / 4f }
    private val minConstraint = 10f

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
        drawCells()
        z += speed
        println("frame")
    }

    private fun drawCells() {
        loadPixels()
        val currentZ = z % depth
        for (x in 0 until width) {
            for (y in 0 until height) {
                setPixelColor(x, y, currentZ)
            }
        }
        updatePixels()
    }

    private fun setPixelColor(x: Int, y: Int, z: Int) {
        val distances = getDistances(x, y, z)
        val closest = if (distances.isNotEmpty()) constrain(distances[0], minConstraint, maxDistance) else maxDistance
        val color = color(map(closest, minConstraint, maxDistance, 0f, 111f).toInt())
        pixels[y * width + x] = color
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