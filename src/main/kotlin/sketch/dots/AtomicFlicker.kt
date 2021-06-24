package sketch.dots

import BaseSketch
import Screen
import kotlin.random.Random

class AtomicFlicker : BaseSketch(
    Screen(1576, 895)
) {

    // config
    private val cellSize: Float = 2F // 1-6f

    // config
    private val revealEnabled = true
    private var revealProgress = 0f
    private var revealStep = 0.0005f
    private var maxRevealRate = 100

    private val startProgress = 0f
    private val fullAlpha = PI
    private val alphaIncrease = 0.1f

    private val nCols by lazy { floor(widthF / cellSize) + 1 }
    private val nRows by lazy { floor(heightF / cellSize) + 1 }
    private val stencil by lazy { loadImage("data/input/earth_and_moon.jpeg") }
    private val stencilPixelSize by lazy { stencil.pixels.size }
    private val availableToReveal by lazy { MutableList(stencilPixelSize) { it } }
    private val shiningPixels = mutableMapOf<Int, Float>()
    private val dimmingPixels = mutableMapOf<Int, Float>()
    private val shiningToBeRemoved = mutableListOf<Int>()
    private val dimmingToBeRemoved = mutableListOf<Int>()

    override fun setup() {
        noStroke()
        background(black)
        stencil.resize(nCols, nRows)
        if (!revealEnabled) drawStencil(cellSize) // config
    }

    override fun draw() {
        translateEdges()
        flicker()
    }

    private fun flicker() {
        removeOld()
        addNew()
        updateAndDraw()
    }

    private fun removeOld() {
        shiningToBeRemoved.run {
            forEach {
                shiningPixels.remove(it)
            }
            clear()
        }
        dimmingToBeRemoved.run {
            forEach {
                dimmingPixels.remove(it)
            }
            clear()
        }
    }

    private fun addNew() {
        val times = if (revealEnabled) floor(sin(revealProgress) * maxRevealRate) else 1
        repeat(times) {
            if (revealEnabled && availableToReveal.isEmpty()) return
            val newFlickerPixel = getNewFlickerPixel()
            shiningPixels[newFlickerPixel] = startProgress
        }
        revealProgress += revealStep
    }

    private fun getNewFlickerPixel(): Int {
        return if (revealEnabled) {
            val newFlickerPixel = availableToReveal.random()
            availableToReveal.remove(newFlickerPixel)
            newFlickerPixel
        } else {
            Random.nextInt(stencilPixelSize)
        }
    }

    private fun updateAndDraw() {
        shiningPixels.forEach { (i, alphaProgress) ->
            if (alphaProgress <= fullAlpha) {
                drawPixel(i, stencil.pixels[i], cellSize, nCols)
                shiningPixels[i] = alphaProgress + alphaIncrease
                drawPixel(i, color(255f, 255f * sin(alphaProgress)), cellSize, nCols)
            } else {
                shiningToBeRemoved.add(i)
                dimmingPixels[i] = startProgress
            }
        }
        dimmingPixels.forEach { (i, alphaProgress) ->
            if (alphaProgress <= fullAlpha) {
                drawPixel(i, stencil.pixels[i], cellSize, nCols)
                dimmingPixels[i] = alphaProgress + alphaIncrease
                drawPixel(i, color(0f, 255f * sin(alphaProgress)), cellSize, nCols)
            } else {
                dimmingToBeRemoved.add(i)
            }
        }
    }

    private fun drawStencil(pixelSize: Float) {
        translateEdges()
        stencil.pixels.forEachIndexed { i, color ->
            drawPixel(i, color, pixelSize, nCols)
        }
    }

    private fun translateEdges() {
        translate(cellSize / 2f, cellSize / 2f)
    }

    private fun drawPixel(i: Int, color: Int, pixelSize: Float, width: Int) {
        val x = (i % width) * pixelSize
        val y = (i / width) * pixelSize
        fill(color)
        circle(x, y, pixelSize)
    }
}
