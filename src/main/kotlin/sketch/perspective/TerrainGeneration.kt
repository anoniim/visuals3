package sketch.perspective

import BaseSketch
import util.translateToCenter

class TerrainGeneration : BaseSketch(
    renderer = P3D
) {
    private val terrainHeight = 250f
    private val noiseFactor = 0.2f
    private val speed = 0.09f
    private val distance = 500f
    private val size = 30f
    private val gridWidth = 2600f
    private val gridHeight = 2000f

    private var time = 0f
    private val numOfCols = ceil(gridWidth / size)
    private val numOfRows = ceil(gridHeight / size)
    private var terrain = generateTerrain(time)

    override fun draw() {
        background(grey3)

        drawTerrain()
        advanceTime()
        terrain = generateTerrain(time)
    }

    private fun advanceTime() {
        if (!isSpacebarPressed()) time += speed
    }

    private fun isSpacebarPressed() = keyPressed && key == ' '

    private fun drawTerrain() {
        noFill()
        translateToCenter()
        rotateX(PI / 3)
        translate(-gridWidth / 2f, -distance)
        for (j in 0 until numOfRows) {
            val alpha = map(j.toFloat(), 0f, numOfRows / 2f, 0f, 255f)
            stroke(grey11, alpha)
            beginShape(TRIANGLE_STRIP)
            for (i in 0 until numOfCols) {
                val x = i * size
                val y = j * size
                val yNext = y + size
                val center = gridWidth / 2f
                val raisedEdges = sq(x - center) / 1000f
                val z = terrain[j][i] + raisedEdges
                val zNext = terrain[j + 1][i] + raisedEdges
                vertex(x, y, z)
                vertex(x, yNext, zNext)
            }
            endShape()
        }
    }

    private fun generateTerrain(time: Float): Array<Array<Float>> {
        return Array(numOfRows + 1) { j ->
            Array(numOfCols) { i ->
                noise(j * noiseFactor - time, i * noiseFactor) * terrainHeight
            }
        }
    }
}