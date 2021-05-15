package shapes

import BaseSketch

open class Circle(
    applet: BaseSketch,
    val x: Float,
    val y: Float,
    var r: Float
): Shape(applet) {

    override fun draw(drawModifiers: (BaseSketch.() -> Unit)?) {
        super.draw(drawModifiers)
        display {
            ellipse(x, y, r * 2, r * 2)
        }
    }
}