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
    private val controllers = mutableMapOf<Int, ((Int) -> Unit)>()

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

    fun on(
        controller: Int,
        changeAction: (change: Int) -> Unit,
    ) {
        controllers[controller] = changeAction
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
        controllers[number]?.invoke(value)

        PApplet.println("Control $number changed to $value")
    }

    fun dispose() {
        bus.dispose()
    }

    fun printDevices() {
        MidiBus.list()
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
        const val PAD_25 = 36
        const val PAD_26 = 37
        const val PAD_27 = 38
        const val PAD_28 = 39
        const val PAD_29 = 40
        const val PAD_30 = 41
        const val PAD_31 = 42
        const val PAD_32 = 43
        const val PAD_33 = 44
        const val PAD_34 = 45
        const val PAD_35 = 46
        const val PAD_36 = 47
        const val PAD_37 = 48
        const val PAD_38 = 49
        const val PAD_39 = 50
        const val PAD_40 = 51
        const val PAD_41 = 52
        const val PAD_42 = 53
        const val PAD_43 = 54
        const val PAD_44 = 55
        const val PAD_45 = 56
        const val PAD_46 = 57
        const val PAD_47 = 58
        const val PAD_48 = 59
        const val PAD_49 = 60
        const val PAD_50 = 61
        const val PAD_51 = 62
        const val PAD_52 = 63
        const val PAD_53 = 64
        const val PAD_54 = 65
        const val PAD_55 = 66
        const val PAD_56 = 67
        const val PAD_57 = 68
        const val PAD_58 = 69
        const val PAD_59 = 70
        const val PAD_60 = 71
        const val PAD_61 = 72
        const val PAD_62 = 73
        const val PAD_63 = 74
        const val PAD_64 = 75
        const val PAD_65 = 76
        const val PAD_66 = 77
        const val PAD_67 = 78
        const val PAD_68 = 79
        const val PAD_69 = 80
        const val PAD_70 = 81
        const val PAD_71 = 82
        const val PAD_72 = 83
        const val PAD_73 = 84
        const val PAD_74 = 85
        const val PAD_75 = 86
        const val PAD_76 = 87
        const val PAD_77 = 88
        const val PAD_78 = 89
        const val PAD_79 = 90

        const val KNOB_14 = 14 // Mikro knob

        fun printDevices() {
            MidiBus.list()
        }
    }

    class Signal() {

    }
}