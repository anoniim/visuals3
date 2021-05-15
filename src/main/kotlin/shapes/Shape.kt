package shapes

import BaseSketch

open class Shape(
    protected val applet: BaseSketch
) {

    protected fun display(displayFun: BaseSketch.() -> Unit) {
        displayFun(applet)
    }

    open fun draw(drawModifiers: (BaseSketch.() -> Unit)? = null) {
        drawModifiers?.invoke(applet)
    }
}