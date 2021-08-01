package sketch.stars

import BaseSketch
import processing.core.PVector
import util.*
import java.util.*

class SilhouetteSparks : BaseSketch() {

    private val forceDampening = 0.8f
    private val forceReduction = 0.01f
    private val maxMouseHistorySize = 3
    private val maxLocationHistory = 10
    private val pixelReduction = 200 // 200

//    private val sparks = doubleCircle()
    private val sparks by lazy { imgStencil }

    private fun doubleCircle() = List(400) {
            val offset = if (it % 2 == 0) 200f else -200f
            val x = offset + cos(it * TWO_PI / 100) * 300f
            val y = sin(it * TWO_PI / 100) * 300f
            Spark(PVector(x, y))
        }

    private val mouseHistory = LinkedList<PVector>()
    private val img by lazy {
        loadImage("data/input/flower_stencil.png").apply {
            filter(THRESHOLD)
            filter(INVERT)
            val aspectRatio = heightF / height
            val imgWidth = round(width * aspectRatio)
            val imgHeight = this@SilhouetteSparks.height
            resize(imgWidth, imgHeight)
        }
    }
    private val imgStencil by lazy {
        val stencilPixels = mutableListOf<Spark>()
        img.loadPixels()
        for (i in img.pixels.indices) {
            if (img.pixels[i] != black) {
                val x = (i % img.width).toFloat()
                val y = (i / img.width).toFloat()
                stencilPixels.add(Spark(PVector(x, y)))
            }
        }
        stencilPixels.shuffle()
        stencilPixels.subList(0, stencilPixels.size / pixelReduction)
    }

    override fun draw() {
        background(grey3)
        // config
//        translateToCenter()
        translate((widthF - img.width)/2f, (height - img.height)/2f)

//        drawStencilImg()

        addToHistory(mouseVector())
        val force = calculateForce()
        drawForce(force)

        sparks.forEach { spark ->
            spark.applyForce(force)
            spark.update()
            spark.draw()
        }

    }

    private fun drawStencilImg() {
        stroke(white)
        imgStencil.forEach {
            point(it.origin)
        }
        noLoop()
    }

    private fun addToHistory(mouseVector: PVector) {
        if (mouseHistory.size >= maxMouseHistorySize) {
            mouseHistory.removeFirst()
        }
        mouseHistory.add(mouseVector)
    }

    private fun drawForce(force: PVector) {
        stroke(grey11)
        strokeWeight(1f)
        line(PVector(), force.copy().mult(10f))
    }

    private fun calculateForce(): PVector {
        val first = mouseHistory.first
        val last = mouseHistory.last
        return (last.copy() - first).mult(forceReduction)
    }

    private fun mouseVector() = PVector(mouseXF, mouseYF).sub(PVector(halfWidthF, halfHeightF))

    private inner class Spark(val origin: PVector) {

        private val locationHistory = LinkedList<PVector>()
        private var location = getOriginLocation()
        private var velocity = PVector()
        private var acceleration = PVector()

        fun applyForce(force: PVector) {
            if (random(1) < 0.5f) return
            acceleration.add(force)
            addRandomnessWhenMoving(force) // config
        }

        fun update() {
            velocity.add(acceleration)
            velocity.mult(forceDampening)
            location.add(velocity)
            addToHistory(location.copy())
            acceleration = PVector()
            if (isStill()) {
                location = getOriginLocation()
                locationHistory.clear()
            }
        }

        private fun getOriginLocation() = origin.copy()//.add(PVector.random2D().mult(14f)) // config

        private fun addToHistory(location: PVector) {
            if (locationHistory.size >= maxLocationHistory) {
                locationHistory.removeFirst()
            }
            locationHistory.add(location)
        }

        private fun addRandomnessWhenMoving(force: PVector) {
            if (force != PVector()) {
                acceleration.add(PVector.random2D())
            }
        }

        fun draw() {
            drawTrail()
            stroke(getColor())
            strokeWeight(getSize())
            point(location)
        }

        private fun drawTrail() {
            if (isStill()) return
            strokeWeight(2f)
            noFill()
            locationHistory.windowed(2).forEachIndexed { i, segment ->
                stroke(colors.sunrise[i])
                beginShape()
                line(segment[0], segment[1])
                endShape()
            }
        }

        private fun getSize() = velocity.mag() * 2f

        private fun getColor(): Int {
            val alpha = map(velocity.mag() * 10f, 0f, 10f, 0f, 255f).toInt()
            return color(160, 40, 0, alpha)
        }

        private fun isStill() = velocity.mag() < 0.1f
    }
}