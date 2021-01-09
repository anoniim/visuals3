package lines

import BaseSketch
import Screen

class HalfFill : BaseSketch(
    Screen(1500, 800),
    longClickClear = true
) {

    private val respawnChance = 1000 // 100 - 1000
    private var maxSpeed: Float = 5f
    private val minSpeed = 0.1f
    val colorMode = false
    val minWidth = 20f

    private var speed = maxSpeed
    val rectangles = mutableListOf(Rectangle(0f, screen.widthF, true))
    val fillColor = grey11
    val emptyColor = grey3

    override fun draw() {
        background(grey3)

        for (i in 0 until rectangles.size) {
            val rect = rectangles[i]
            rect.show()
            rect.update()?.let {
                rectangles.addAll(it)
            }
        }
        val rectangleSizeLog = kotlin.math.log2(rectangles.size.toFloat() * 100)
        speed = constrain(map(rectangleSizeLog, 5f, 13.5f, maxSpeed, minSpeed), minSpeed, maxSpeed)
        super.draw()
        rectangles.removeIf {
            it.useless
        }
        println (rectangles.size)
    }

    override fun reset() {
        speed = maxSpeed
        rectangles.clear()
        rectangles.add(Rectangle(0f, screen.widthF, true))
    }

    inner class Rectangle(
        private val startX: Float,
        private val endX: Float,
        var fill: Boolean,
        private val parent: Rectangle? = null
    ) {

        var useless = false
        var hasSpawnedChildren: Boolean = false
        private var color = newRandomColor()
        private var progressX: Float = 0f
        private var width: Float = (endX - startX)

        fun update(): List<Rectangle>? {
            if (hasSpawnedChildren) return null
            if (isFillingUp()) {
                progressX += speed
            } else {
                if (!hasSpawnedChildren && width > minWidth) {
                    hasSpawnedChildren = true
                    return spawnChildren()
                } else {
                    parent?.useless = true
                    if (random(respawnChance) > respawnChance - 1) {
                        return spawnNew()
                    }
                }
            }
            return null
        }

        private fun isFillingUp() = progressX < width

        private fun spawnChildren(): List<Rectangle> {
            val halfWidth = width / 2
            return listOf(
                Rectangle(startX, startX + halfWidth, !fill, this),
                Rectangle(startX + halfWidth, endX, fill, this)
            )
        }

        private fun spawnNew(): List<Rectangle> {
            return listOf(Rectangle(startX, endX, !fill, this))
        }

        fun show() {
            noStroke()
            if (colorMode) {
                fill(color)
            } else {
                fill(if (fill) fillColor else emptyColor)
            }
            rect(startX, 0f, progressX, screen.heightF)
        }

        private fun newRandomColor(): Int = color(
            random(100f, 200f),
            random(100f, 200f),
            random(100f, 200f))
    }
}