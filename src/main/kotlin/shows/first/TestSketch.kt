package shows.first

import BaseSketch
import Screen
import shows.first.MidiController.Companion.PAD_16


class TestSketch : BaseSketch(fullscreen = true) {

    // TODO MIDI signals are currently sent to all MidiControllers (how to send them only to the active scene?)

    private lateinit var scenes: List<Scene>
    private val controller by lazy { MidiController(this, 1, 2) }
    private var scenePointer = 0

    override fun setup() {
        val scene1 = Scene1(this)
        val scene2 = Scene2(this)
        scenes = listOf(
            scene1,
            scene2,
        )
        scene1.start()

        controller.on(PAD_16, this::transitionToNextScene)
    }

    override fun draw() {
        for (scene in scenes) {
            scene.updateScene()
        }
    }

    override fun keyPressed() {
        if (key == ENTER) {
            transitionToNextScene(0, 0)
        }
        for (scene in scenes) {
            scene.notifyKeyPressed(key)
        }
    }

    private fun transitionToNextScene(pitch: Int, velocity: Int) {
        if (scenePointer < scenes.size - 1) {
            val currentScene = scenes[scenePointer]
            val nextScene = scenes[scenePointer + 1]
            currentScene.transitionTo(nextScene)
            scenePointer++
        }
    }
}