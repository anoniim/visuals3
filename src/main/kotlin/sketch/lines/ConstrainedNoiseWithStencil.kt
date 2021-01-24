package sketch.lines

import BaseSketch
import Screen
import processing.core.PGraphics
import kotlin.math.roundToInt


class ConstrainedNoiseWithStencil : BaseSketch(
    Screen(1200, 800),
    longClickClear = true
) {

    private val spacing: Float = 25f // 20
    private val xSmoothness = 6f // 6
    private val ySmoothness = 6f // 6
    private val noiseFactor = 50f // 50
    private var movementStep: Float = 0.01f // 0.01

    private val noiseRadius = 400f // 400
    private val drop = 0f // 100f

    private val lineBuffer = noiseFactor / spacing
    private val numLines = screen.heightF / spacing
    private val yBuffer = -noiseFactor
    private var lines = generateLines()
    private val stencil by lazy {
//        generateStencil()
        loadStencilImage()
    }

    override fun settings() {
        super.settings()
        pixelDensity(1)
    }

    override fun draw() {
        background(grey3)
//        drawStencil()
        drawLines()
        drawLongPressOverlay()
    }

    private fun drawStencil() {
        image(stencil, 0f, 0f)
    }

    private fun drawLines() {
        for (line in lines) {
            line.draw()
            line.update()
        }
    }

    override fun reset() {
        exit()
    }

    private fun generateLines() = List((numLines + lineBuffer).roundToInt()) {
        Line(yBuffer + it * spacing)
    }

    private fun generateStencil(): PGraphics {
        val stencil: PGraphics = createGraphics(screen.width, screen.height)
        stencil.beginDraw()
//        drawStencilImage(stencil, "data/input/standby.png")
        stencil.beginShape()
        stencil.fill(color(255))
        val sides = 3
        val angle: Float = 360f / sides
        val r = noiseRadius
        for (i in 0 until sides) {
            val x: Float = screen.centerX + cos(radians(i * angle)) * r
            val y: Float = screen.centerY + sin(radians(i * angle)) * r
            stencil.vertex(x, y)
        }
        stencil.endShape(CLOSE)
        stencil.endDraw()
        stencil.loadPixels()
        return stencil
    }

    private fun loadStencilImage(): PGraphics {
        val stencil: PGraphics = createGraphics(screen.width, screen.height)
        stencil.beginDraw()
        drawStencilImage(stencil, "data/input/standby.png")
        stencil.loadPixels()
        return stencil
    }

    private fun drawStencilImage(stencil: PGraphics, imageFile: String) {
        val stencilImage = loadImage(imageFile)
        stencilImage.resize(0, floor(0.9f * screen.height))
        stencilImage.filter(INVERT)
        stencil.image(stencilImage, screen.centerX - stencilImage.width / 2f, screen.centerY - stencilImage.height / 2f)
    }

    private inner class Line(val yOrigin: Float) {

        private val yOffset: Float
            get() = map(mouseYF, 0f, screen.heightF, -drop, drop)
        private var horizontalMovement: Float = 0f
        private var verticalMovement: Float = 0f

        private val numPoints = ceil(screen.widthF / xSmoothness)
        private val segmentLength = screen.widthF / numPoints
        private var shape = false

        fun draw() {
            strokeWeight(1.5f)
            noFill()
            stroke(grey9)
            beginShape()
            for (i in -1..numPoints + 1) {
                val x = i * segmentLength
//                splitInShapes(x)
                val y = yOrigin + calculateNoise(x, yOrigin, i)
                curveVertex(x, y)
            }
            endShape()
        }

        private fun splitInShapes(x: Float) {
            if (shouldNoise(x, yOrigin) && !shape) {
                vertex(x, yOrigin)
                endShape()
                shape = true
                beginShape()
                vertex(x, yOrigin)
            }
            if (!shouldNoise(x, yOrigin) && shape) {
                vertex(x, yOrigin)
                endShape()
                shape = false
                beginShape()
                vertex(x, yOrigin)
            }
            if (shape) {
                stroke(orange)
            } else {
                stroke(grey3)
            }
        }

        private fun calculateNoise(x: Float, y: Float, i: Int): Float {
            return if (shouldNoise(x, y)) {
                val yNoise = noise(i, 1f)
                yNoise + yOffset
            } else {
                val yNoise = noise(i, 0.2f)
                yNoise
            }
        }

        private fun noise(i: Int, noiseDampening: Float = 1f): Float {
            return map(
                noise(i / xSmoothness + horizontalMovement, yOrigin / (spacing * ySmoothness) + verticalMovement),
                0f,
                1f,
                -noiseFactor,
                noiseFactor
            ) * noiseDampening
        }

        private fun shouldNoise(x: Float, y: Float): Boolean {
            if (x < 0f || x > widthF || y < 0f || y > heightF) return false
            val color = stencil.pixels[floor(x) + floor(y) * width]
            return brightness(color) > 1f
        }

        fun update() {
            horizontalMovement -= movementStep
            verticalMovement += movementStep
//            verticalMovement -= map(noise(horizontalMovement), 0f, 1f, -2f, 2f) * movementStep
        }
    }
}