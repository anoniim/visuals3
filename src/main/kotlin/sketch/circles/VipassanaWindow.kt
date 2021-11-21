package sketch.circles

import BaseSketch
import processing.core.PGraphics
import processing.core.PVector

class VipassanaWindow : BaseSketch() {

    private val bgColor = white
    private val rippleColor = grey11

    private val numOfBigCircles = 20
    private val bigRadius = 100f
    private val bigCircleMargin = 0f
    private val numOfSmallCircles = 220
    private val smallRadius = 30f
    private val smallCircleMargin = 2f

    private lateinit var bigCircles: List<Circle>
    private lateinit var smallCircles: List<Circle>
    private lateinit var bigMask: PGraphics
    private lateinit var smallMask: PGraphics

    override fun setup() {
        bigMask = generateMask(bigRadius * 2)
        smallMask = generateMask(smallRadius * 2)
        bigCircles = generateCircles(numOfBigCircles, bigRadius, bigCircleMargin, bigMask)
        smallCircles = generateCircles(numOfSmallCircles, smallRadius, smallCircleMargin, smallMask)
    }

    private fun generateMask(size: Float): PGraphics {
        val sizeInt = size.toInt()
        return createGraphics(sizeInt, sizeInt).apply {
            beginDraw()
            val halfObjectSize = width / 2f
            circle(halfObjectSize, halfObjectSize, halfObjectSize * 2)
            endDraw()
        }
    }

    private fun generateCircles(count: Int, radius: Float, margin: Float, mask: PGraphics): List<Circle> {
        val circles = mutableListOf<Circle>()
        repeat(count) {
            val position = findPositionWithoutOverlapping(circles, radius, margin)
            circles.add(Circle(position, radius, mask))
        }
        return circles
    }

    private fun findPositionWithoutOverlapping(circles: MutableList<Circle>, radius: Float, margin: Float): PVector {
        var position = PVector(random(screen.widthF), random(screen.heightF))
        var isOverlapping = isOverlapping(position, circles, radius, margin)
        var counter = 0
        while (isOverlapping) {
            position = PVector(random(screen.widthF), random(screen.heightF))
            isOverlapping = isOverlapping(position, circles, radius, margin)
            counter++
            if (counter > 1000) {
                throw Exception("Not possible to generate a new circle with radius $radius")
            }
        }
        return position
    }

    private fun isOverlapping(
        position: PVector,
        circles: MutableList<Circle>,
        radius: Float,
        margin: Float
    ) = circles.any {
        it.position.dist(position) < (radius * 2) + margin
    }

    override fun draw() {
        background(bgColor)
        drawDiagonals()
        (bigCircles + smallCircles).forEach {
            it.draw()
        }

        noLoop()
    }

    private fun drawDiagonals() {
        val spacing = 10f
        var x = 0f
        noFill()
        stroke(rippleColor)
        while (x < widthF * 2f) {
            line(x, 0f, 0f, x)
            x += spacing
        }
    }

    private inner class Circle(val position: PVector, val radius: Float, val mask: PGraphics) {

        val graphicsSize = (radius * 2).toInt()
        val numOfRipples = ceil(radius / 2.5f)
        val rippleOrigin = PVector(random(graphicsSize), random(graphicsSize))

        fun draw() {
            val background = createGraphics(graphicsSize, graphicsSize).apply {
                beginDraw()
                background(bgColor)
                stroke(rippleColor)
                noFill()
                repeat(numOfRipples) {
                    circle(rippleOrigin.x, rippleOrigin.y, (it + 1) * ((radius * 4.5f) / numOfRipples))
                }
                endDraw()
            }

            background.mask(mask)
            image(background, position.x, position.y)
        }
    }
}