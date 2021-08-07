package shapes

import BaseSketch
import processing.core.PApplet
import processing.core.PConstants.PI
import processing.core.PConstants.TWO_PI

class Polygon(
    applet: BaseSketch,
    type: Type,
    size: Float,
) : Shape(applet) {

    private val pShape by lazy {
        with(applet) {
            createShape().apply {
                beginShape()
                stroke(grey11)
                strokeWeight(1.5f)
                noFill()
                var angle = 0f
                while (angle <= TWO_PI) {
                    val x = size * PApplet.cos(angle)
                    val y = size * PApplet.sin(angle)
                    vertex(x, y)
                    angle += type.innerAngle
                }
                endShape(PApplet.CLOSE)
            }
        }
    }

    override fun draw(drawModifiers: (BaseSketch.() -> Unit)?) {
        display(drawModifiers) {
            shape(pShape)
        }
    }

    @Suppress("unused")
    enum class Type(val innerAngle: Float) {
        TRIANGLE(2 * PI / 3f),
        SQUARE(PI / 2f),
        PENTAGON(TWO_PI / 5f),
        HEXAGON(PI / 3f),
        OCTAGON(PI / 4f),
    }
}
