package sketch.spirals

import BaseSketch
import Screen
import com.hamoid.VideoExport
import processing.core.PShape
import processing.core.PVector
import processing.opengl.PShapeOpenGL.createShape
import util.translateToCenter
import util.vertex

class PolygonStripeTunnel : BaseSketch(Screen()) {

    // config
    private val shape = Shape.SQUARE
    private val speed = 0.08f // 0.01 - 2
    private val initSize = 50f // 10 - 100
    private val spin = 0.0f // (0.01) 0 - 0.1f
    private val perspectiveEnabled = true

    private val dissolveFactor = 1f + speed / if (spin > 0) 200f else 50f
    private var zoomingIn = false
    private var animationProgress = 0f
    private var angle = shape.innerAngle
    private val shapes = MutableList<StripeShape>(1) {
        StripeShape(shape)
    }

    override fun keyPressed() {
        when (key) {
            ' ' -> noLoop()
            ENTER -> zoomingIn = true
            else -> loop()
        }
    }

    override fun draw() {
        background(grey3)
        stroke(grey11)
        translateToCenter()

        drawShapes()
        removeOutOfScreenSegments()
//        videoExport.saveFrame() // record
    }

    private fun drawShapes() {
        shapes.forEach {
            it.draw()
        }
    }

    private fun vectorFromAngle(angle: Float) = PVector.fromAngle(angle).setMag(initSize)

    private fun removeOutOfScreenSegments() {
        shapes.removeIf { it.radius > widthF }
    }

    private inner class StripeShape(
        shape: Shape,
        var radius: Float = 100f
    ) {
        val s: PShape = createShape().apply {
            beginShape()
            var angle = 0f
            while (angle < TWO_PI) {
                vertex(PVector.fromAngle(angle))
                angle += shape.innerAngle
            }
            endShape()
        }

        fun draw() {
            stroke(red)
            fill(yellow)
            shape(s)
        }
    }

    @Suppress("unused")
    private enum class Shape(val innerAngle: Float) {
        TRIANGLE(2 * PI / 3f),
        SQUARE(PI / 2f),
        PENTAGON(TWO_PI / 5f),
        HEXAGON(PI / 3f),
    }

    @Suppress("unused")
    private val videoExport by lazy {
        VideoExport(this).apply {
            startMovie() // record
        }
    }
}
