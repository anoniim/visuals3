package shapes

import BaseSketch

abstract class Shape(
    protected val applet: BaseSketch
) {

    protected fun display(drawModifiers: (BaseSketch.() -> Unit)? = null, displayFun: BaseSketch.() -> Unit) {
        drawModifiers?.invoke(applet)
        displayFun(applet)
    }

    abstract fun draw(drawModifiers: (BaseSketch.() -> Unit)? = null)
}