package shapes

import processing.core.PApplet

open class Circle(
    applet: PApplet,
    val x: Float,
    val y: Float,
    var r: Float
): Shape(applet) {

    fun draw() {
        display {
            ellipse(x, y, r * 2, r * 2)
        }
    }
}