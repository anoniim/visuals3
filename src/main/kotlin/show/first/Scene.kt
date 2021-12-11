package show.first

import BaseSketch

typealias SketchContext = BaseSketch.() -> Unit

abstract class Scene(val applet: BaseSketch) {
    var isRunning = false

    protected abstract val update: SketchContext
    protected open fun keyPressed(key: Char) {}

    fun updateScene() {
        if (isRunning) {
            update(applet)
        }
    }

    fun notifyKeyPressed(key: Char) {
        if (isRunning) {
            keyPressed(key)
        }
    }

    fun finish() {
        isRunning = false
    }
    fun start() {
        isRunning = true
    }

    fun transitionTo(nextScene: Scene) {
        finish()
        nextScene.start()
    }

}