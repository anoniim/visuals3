package sketch.squares

import BaseSketch

class RaisingTiles : BaseSketch(renderer = P2D) {

    private val tileGap: Float = 12F
    private val tileSize: Float = 60F
    private val fillMin = 50f
    private val fillMax = 150f
    private val zMin = 0.5f
    private val zMax = 1f

    private val tileTotalSize = tileSize + tileGap
    private val horizontalCount = ceil(screen.widthF / tileTotalSize)
    private val verticalCount = ceil(screen.heightF / tileTotalSize)
    private val tiles = mutableListOf<Tile>()

    override fun setup() {
        rectMode(CENTER)
        for (j in 0 until verticalCount) {
            for (i in 0 until horizontalCount) {
                val x = i * tileTotalSize + tileTotalSize / 2f
                val y = j * tileTotalSize + tileTotalSize / 2f
                val z = random(0.7f, zMax)
                val fill = random(fillMin, fillMax)
                tiles.add(Tile(x, y, z, fill))
            }
        }
    }

    override fun draw() {
        showTiles()
        pokeRandomTile()
    }

    override fun mousePressed() {
        pokeSelectedTile()
    }

    override fun mouseDragged() {
        pokeSelectedTile()
    }

    private fun showTiles() {
        background(grey3)
        for (tile in tiles) {
            tile.update()
            tile.show()
        }
    }

    private fun pokeRandomTile() {
        if (frameCount % 60 == 0) {
            tiles.random().poke()
        }
    }

    private fun pokeSelectedTile() {
        val j = floor(mouseYF / tileTotalSize)
        val i = floor(mouseXF / tileTotalSize)
        val index = (i + j * horizontalCount)
        if (index >= 0 && index < tiles.size) {
            tiles[index].poke()
        }
    }

    private inner class Tile(
        val x: Float,
        val y: Float,
        var z: Float,
        var fillRawOriginal: Float
    ) {

        private val zOriginal = z
        private var isPoked: Boolean = false
        private var floatProgress = random(TWO_PI)
        private var pokeProgress = 0f

        fun update() {
            updateFloating()
            updatePoking()
        }

        private fun updateFloating() {
            floatProgress += 0.01f
            z += (sin(floatProgress) * 0.0009f)
            if (z >= zMax) {
                floatProgress += PI - (floatProgress % TWO_PI)
            }
        }

        private fun updatePoking() {
            if (isPoked) {
                pokeProgress += 0.01f
                z = constrain(z + (-1 * sin(pokeProgress) * 0.005f), 0f, zOriginal)
                if (pokeProgress > TWO_PI) {
                    isPoked = false
                    pokeProgress = 0f
                }
            }
        }

        fun show() {
            noStroke()
            val fillRaw = map(z, zMin, zMax, 0f, 1f) * fillRawOriginal
            fill(constrain(color(fillRaw), grey3, grey11))
            rect(x, y, z * tileSize, z * tileSize, 5f)
        }

        fun poke() {
            isPoked = true
        }
    }
}


