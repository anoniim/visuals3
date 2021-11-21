package shapes

import BaseSketch
import processing.core.PVector

open class Circle(
    applet: BaseSketch,
    val x: Float,
    val y: Float,
    var r: Float,
    private val drawModifiers: (BaseSketch.() -> Unit)? = null
): Shape(applet) {

    val position = PVector(x, y)

    override fun draw(drawModifiers: (BaseSketch.() -> Unit)?) {
        display(drawModifiers ?: this.drawModifiers) {
            circle(x, y, r * 2)
        }
    }
}