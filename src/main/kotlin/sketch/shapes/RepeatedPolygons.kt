package sketch.shapes

import BaseSketch
import util.Polygon
import util.translateToCenter

class RepeatedPolygons : BaseSketch() {

    private val circleRadius = 200f
    private val repeatCount = 16
    private val shapeSize = 100f
    private val shape = Polygon.TRIANGLE

    private val angleSegment = TWO_PI / repeatCount
    private val shapes by lazy {
        List(repeatCount) {
            ShapeUnit(angle = it * angleSegment)
        }
    }

    override fun draw() {
        background(grey3)
        translateToCenter()
        rotate(-HALF_PI)

        shapes.forEach {
            it.update {

                // pulsating
//                radius = circleRadius + 50f * sin(frameCount / 100f)

                // mouse control, TODO find all scales at which the triangles cross and animate gradually from one to another
                size = 200f
                radius = map(mouseYF, 0f, heightF, 0f, halfHeightF)
            }
            it.draw()
        }
    }

    private inner class ShapeUnit(
        val polygon: Polygon = shape,
        var size: Float = shapeSize,
        var radius: Float = circleRadius,
        var angle: Float
    ) {

        private val pShape by lazyShapeCreation()

        fun update(updateFun: ShapeUnit.() -> Unit) {
            updateFun(this)
        }

        fun draw() {
            pushMatrix()
            val x = radius * cos(angle)
            val y = radius * sin(angle)
            translate(x, y)
            rotate(angle)
            shape(pShape)
            popMatrix()
        }

        private fun lazyShapeCreation() = lazy {
            createShape().apply {
                beginShape()
                stroke(grey11)
                noFill()
                var angle = 0f
                while (angle <= TWO_PI) {
                    val x = size * cos(angle)
                    val y = size * sin(angle)
                    vertex(x, y)
                    angle += polygon.innerAngle
                }
                endShape(CLOSE)
            }
        }
    }
}
