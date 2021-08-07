package shapes

import BaseSketch

open class Circle(
    applet: BaseSketch,
    val x: Float,
    val y: Float,
    var r: Float,
    private val drawModifiers: (BaseSketch.() -> Unit)? = null
): Shape(applet) {

    override fun draw(drawModifiers: (BaseSketch.() -> Unit)?) {
        display(drawModifiers ?: this.drawModifiers) {
            circle(x, y, r * 2)
        }
    }
}