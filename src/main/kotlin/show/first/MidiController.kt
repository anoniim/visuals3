package show.first

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
    private val releaseActions = mutableMapOf<Int, ((pitch: Int) -> Unit)?>()
    private val activeNotes = MutableList<Note?>(255) { null }

    init {
        applet.registerMethod("dispose", this)
    }

    fun on(
        buttonRange: IntRange,
        triggerAction: (pitch: Int, velocity: Int) -> Unit,
        releaseAction: ((pitch: Int) -> Unit)? = null
    ) {
        buttonRange.forEach {
            triggerActions[it] = triggerAction
            releaseActions[it] = releaseAction
        }
    }

    fun on(
        button: Int,
        triggerAction: (pitch: Int, velocity: Int) -> Unit,
        releaseAction: ((pitch: Int) -> Unit)? = null
    ) {
        triggerActions[button] = triggerAction
        releaseActions[button] = releaseAction
    }

    fun isOn(button: Int): Boolean {
        return activeNotes.getOrNull(button) != null
    }

    /**
     * Called by MidiBus when a note is played.
     */
    fun noteOn(channel: Int, pitch: Int, velocity: Int) {
        triggerActions[pitch]?.invoke(pitch, velocity)
        activeNotes.add(pitch, Note(channel, pitch, velocity))

        PApplet.println("$pitch ON ($velocity)")
    }

    /**
     * Called by MidiBus when a note is stops playing.
     */
    fun noteOff(channel: Int, pitch: Int, velocity: Int) {
        activeNotes.removeAt(pitch)
        releaseActions[pitch]?.invoke(pitch)

        PApplet.println("$pitch OFF")
    }

    /**
     * Called by MidiBus when there is a controller change.
     */
    fun controllerChange(channel: Int, number: Int, value: Int) {
        PApplet.println("Control $number changed to $value")
    }

    fun dispose() {
        bus.dispose()
    }

    companion object {
        const val PAD_1 = 12
        const val PAD_2 = 13
        const val PAD_3 = 14
        const val PAD_4 = 15
        const val PAD_5 = 16
        const val PAD_6 = 17
        const val PAD_7 = 18
        const val PAD_8 = 19
        const val PAD_9 = 20
        const val PAD_10 = 21
        const val PAD_11 = 22
        const val PAD_12 = 23
        const val PAD_13 = 24
        const val PAD_14 = 25
        const val PAD_15 = 26
        const val PAD_16 = 27
        const val PAD_17 = 28
        const val PAD_18 = 29
        const val PAD_19 = 30
        const val PAD_20 = 31
        const val PAD_21 = 32
        const val PAD_22 = 33
        const val PAD_23 = 34
        const val PAD_24 = 35
    }

    class Signal() {

    }
}