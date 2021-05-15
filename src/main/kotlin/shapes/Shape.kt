package shapes

import processing.core.PApplet

open class Shape(
    private val applet: PApplet
) {

    fun display(displayFun: PApplet.() -> Unit) {
        displayFun(applet)
    }
}