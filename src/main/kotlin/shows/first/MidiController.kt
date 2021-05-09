package shows.first

import processing.core.PApplet
import themidibus.MidiBus
import themidibus.Note

class MidiController(applet: PApplet, input: Int, output: Int) {

    interface Callback {
        fun noteOn(channel: Int, pitch: Int, velocity: Int)
        fun noteOff(channel: Int, pitch: Int, velocity: Int)
        fun controllerChange(channel: Int, number: Int, value: Int)
    }

    private val bus = MidiBus(this, input, output)
    private val triggerActions = mutableMapOf<Int, (pitch: Int, velocity: Int) -> Unit>()
    private val activeNotes = MutableList<Note?>(255) { null }

    init {
        applet.registerMethod("dispose", this)
    }

    fun on(button: Int, action: (pitch: Int, velocity: Int) -> Unit) {
        triggerActions[button] = action
    }

    fun noteOn(channel: Int, pitch: Int, velocity: Int) {
        triggerActions[pitch]?.invoke(pitch, velocity)
        activeNotes.add(pitch, Note(channel, pitch, velocity))

        PApplet.println("$pitch ON ($velocity)")
    }

    fun noteOff(channel: Int, pitch: Int, velocity: Int) {
        activeNotes.removeAt(pitch)

        PApplet.println("$pitch OFF")
    }

    fun controllerChange(channel: Int, number: Int, value: Int) {
        PApplet.println("Control $number changed to $value")
    }

    fun dispose() {
        bus.dispose()
    }

    fun isOn(button: Int): Boolean {
        return activeNotes.getOrNull(button) != null
    }

    companion object {
        const val PAD_1 = 12
        const val PAD_16 = 27
    }

    class Signal() {

    }
}