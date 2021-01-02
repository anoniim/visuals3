package pixels

import BaseSketch
import Screen

class WaterRipples : BaseSketch(Screen(1200, 800)) {

    private val defaultColor = grey3
    private val damping = 0.9f // 0.90 - 0.99

    var current = Array(screen.width) { FloatArray(screen.height) }
    var previous = Array(screen.width) { FloatArray(screen.height) }

    override fun mousePressed() {
        current[mouseX][mouseY] = 255F
    }

    override fun draw() {
        background(defaultColor)
        loadPixels()
        for (x in (1 until screen.width - 1)) {
            for (y in (1 until screen.height - 1)) {
                current[x][y] = ((previous[x - 1][y]
                        + previous[x + 1][y]
                        + previous[x][y + 1]
                        + previous[x][y - 1]) / 2
                        - current[x][y])

                current[x][y] = current[x][y] * damping
                pixels[x + y * screen.width] = color(current[x][y] * 255)
            }
        }
        updatePixels()
        // Swap the buffers
        val temp = previous
        previous = current
        current = temp
    }
}