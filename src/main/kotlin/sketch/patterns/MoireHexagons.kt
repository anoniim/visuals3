package sketch.patterns

import BaseSketch
import Screen

class MoireHexagons : BaseSketch(Screen(1500, 800)) {

    private val edges = 6
    private val radius = 40f // 10 - 100
    private val strokeWeight = 1f // 1 - 10
    private val rotationStep: Float = 0.1f

    private val backgroundLayer = mutableListOf<Hexagon>()
    private val foregroundLayer = mutableListOf<Hexagon>()
    private val diameter = radius * 2
    private val rowHeight = sqrt(sq(radius) - sq(radius / 2))
    private val numForegroundItems = floor(2 * screen.widthF / rowHeight) / 2
    private var rotation: Float = 0f

    override fun setup() {
        initBackgroundLayer()
        initForegroundLayer()
    }

    override fun draw() {
        background(grey3)
        for (hexagon in backgroundLayer) {
            hexagon.show()
        }

        translate(mouseXF, mouseYF)
//        translate(screen.centerX, screen.centerY)
        rotate(getRotation())

        for (hexagon in foregroundLayer) {
            hexagon.show()
        }
    }

    private fun getRotation(): Float {
        if (mousePressed && mouseButton == RIGHT) {
            rotation += rotationStep
        } else if (mousePressed && mouseButton == LEFT) {
            rotation -= rotationStep
        }
        return rotation
    }

    private fun initBackgroundLayer() {
        val maxItemsInRow = floor(screen.widthF / rowHeight)
        val maxItemsInCol = floor(screen.heightF / rowHeight)
        for (j in -maxItemsInCol..maxItemsInCol) {
            for (i in -maxItemsInRow..maxItemsInRow) {
                backgroundLayer.add(Hexagon(j, i))
            }
        }
    }

    private fun initForegroundLayer() {
        for (j in -numForegroundItems..numForegroundItems) {
            for (i in -numForegroundItems..numForegroundItems) {
                foregroundLayer.add(Hexagon(j, i, false))
            }
        }
    }

    inner class Hexagon(
        private val j: Int,
        private val i: Int,
        private val createOnInit: Boolean = true) {

        private val shape = createShape()

        init {
            if (createOnInit) {
                val (centerX, centerY) = calculateCenter(j, i)
                createHexagonShape(centerX, centerY)
            }
        }

        fun show() {
            if (!createOnInit) {
                val (centerX, centerY) = calculateCenter(j, i)
                createHexagonShape(centerX, centerY)
            }
            shape(shape)
        }

        private fun calculateCenter(j: Int, i: Int): Pair<Float, Float> {
            val xOffset = if (j % 2 == 0) 1.5f * radius else 0f
            val centerX = xOffset + i * 3 * radius
            val centerY = j * rowHeight
            return Pair(centerX, centerY)
        }

        private fun createHexagonShape(centerX: Float, centerY: Float) {
            shape.beginShape()
            shape.noFill()
            shape.stroke(grey9)
            shape.strokeWeight(strokeWeight)
            for (a in 0 until edges) {
                val angle = a * radians(60f)
                val x = centerX + cos(angle) * radius
                val y = centerY + sin(angle) * radius
                shape.vertex(x, y)
            }
            shape.endShape(CLOSE)
        }
    }
}