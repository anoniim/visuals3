package sketch.walkers

import BaseSketch
import util.translateToCenter
import kotlin.random.Random

// TODO Find and implement hotspots where the walkers appear automatically or where they appear from on button press
class CityExploration : BaseSketch() {

    // config
    private val cell: Float = 5F
    private val initialScale = 4
    private val yTranslationOffset = 100f

    private val nCols by lazy { floor(widthF / cell) + 1 }
    private val nRows by lazy { floor(heightF / cell) + 1 }
    private val map by lazy { loadImage("data/input/show1/london_full.png") }
    private val walkers: MutableList<Walker> = mutableListOf()

    override fun setup() {
        map.resize(nCols, nRows)
        map.loadPixels()
    }

    override fun draw() {
        translateToCenter()
        translate(0f, yTranslationOffset)
        scale()
        background(grey5)
//        drawMap()
//        drawPixelatedMap()
        drawWalkers()
    }

    private fun scale() {
        // TODO start scaling later (potentially on button press)
        val scale = initialScale - frameCount / 300f
        if(scale > 1) {
            scale(scale)
        }
    }

    private fun drawPixelatedMap() {
        pushMatrix()
        translateFromCenter()
        noStroke()
        repeat(nRows) { j ->
            val y = j * cell
            repeat(nCols) { i ->
                val x = i * cell
                val color = map.get(i, j)
                fill(color)
                ellipse(x, y, cell, cell)
            }
        }
        popMatrix()
    }

    private fun drawMap() {
        if (mousePressed) {
            pushMatrix()
            translateFromCenter()
            image(map, 0f, 0f, widthF, heightF)
            popMatrix()
        }
    }

    private fun drawWalkers() {
        for (walker in walkers) {
            walker.run {
                move()
                draw()
            }
        }
    }

    override fun mouseClicked() {
        val x = floor(mouseXF / widthF * nCols)
        val y = floor(mouseYF / heightF * nRows)
        walkers.add(Walker(x, y))
    }

    private fun translateFromCenter() {
        translate(-width / 2f, -height / 2f)
        translate(0f, -yTranslationOffset)
    }

    inner class Walker(xInit: Int, yInit: Int) {

        private val path = mutableListOf(Pair(xInit, yInit))

        fun draw() {
            // TODO this is very ineffective as draws path for every single walker (overdrawing)
            //  Instead, remember all visited places and draw them only once every frame
            noStroke()
            pushMatrix()
            translateFromCenter()
            for (step in path) {
                val i = step.first
                val j = step.second
                val color = map.get(i, j)
                fill(color)
                ellipse(i * cell, j * cell, cell, cell)
            }
            popMatrix()
        }

        fun move() {
            val direction = Random.nextFloat()
            val lastStep = path.last()
            val newStep = when (direction) {
                in 0F..0.25F -> Pair(lastStep.first, lastStep.second - 1) // Go up
                in 0.25F..0.5F -> Pair(lastStep.first + 1, lastStep.second) // Go right
                in 0.5F..0.75F -> Pair(lastStep.first, lastStep.second + 1) // Go down
                else -> Pair(lastStep.first - 1, lastStep.second) // Go left (0.75F..1F)
            }
            if (newStep.isNotOutside()) {
                path.add(newStep)
            } else {
                // Do not add if outside of the screen
            }
        }

        private fun Pair<Int, Int>.isNotOutside(): Boolean {
            return first in 0 until nCols && second in 0 until nRows
        }
    }
}