package sketch.sound.zdenda

import processing.core.PApplet
import processing.sound.SoundFile
import sketch.sound.BarVisualizer
import sound.SoundHelper

fun main() {
    PApplet.main(GrainVisualizer::class.java)
}

class GrainVisualizer : BarVisualizer(
    resolution = 88
) {

    private var beepTriggerCount = 0

    override fun setup() {
        super.setup()
        inputFile = SoundFile(this, "data/input/audio/grain.wav").apply {
            loop()
//            jump(18f) // vibrato
//            jump(28f) // scream
//            jump(54f) // beats
//            jump(80f) // flute
        }
    }

    private val vibratoCue by lazy {
        SoundHelper.WaveformSceneCue(0.6f)
    }
    private val screamCue by lazy {
        SoundHelper.FftSceneCue(listOf(36), 0.02f)
    }
    private val beepCue by lazy {
        SoundHelper.FftSceneCue(listOf(12), 0.1f).apply {
            setRepeatable(throttleMillis = 1000L, triggerLimit = 16)
        }
    }
    private val beatCue by lazy {
        SoundHelper.FftSceneCue(listOf(4), 0.10f).apply {
            setRepeatable(throttleMillis = 400L)
            setEnabled(false)
        }
    }
    private val flute1Cue by lazy {
        SoundHelper.FftSceneCue(listOf(49), 0.10f).apply {
            setRepeatable()
            setEnabled(false)
        }
    }
    private val flute2Cue by lazy {
        SoundHelper.FftSceneCue(listOf(46,47), 0.10f).apply {
            setRepeatable()
            setEnabled(false)
        }
    }
    private val flute3Cue by lazy {
        SoundHelper.FftSceneCue(listOf(20), 0.10f).apply {
            setRepeatable()
            setEnabled(false)
        }
    }

    override fun draw() {
        super.draw()
        checkVibratoSceneCue()
        drawCueCount()
    }

    private fun drawCueCount() {
        for(i in 0 until beepTriggerCount) {
            stroke(green)
            strokeWeight(10f)
            point(600f + i * 20f, 26f)
        }
    }

    private fun checkVibratoSceneCue() {
        vibratoCue.checkMax(waveform) {
            showLabel("VIBRATO", x = 200f)
        }
        screamCue.checkAverage(fft) {
            showLabel("SCREAM", x = 300f)
        }
        flute1Cue.checkAverage(fft) {
            showLabel("FLUTE 1", x = 400f)
        }
        flute2Cue.checkAverage(fft) {
            showLabel("FLUTE 2", x = 400f, y = 2 * 22f + 20f)
        }
        flute3Cue.checkAverage(fft) {
            showLabel("FLUTE 3", x = 400f, y = 3 * 22f + 20f)
        }
        beepCue.checkAny(fft) {
            showLabel("BEEP", x = 500f)
            beepTriggerCount++
            if (beepTriggerCount >= 16) beatCue.setEnabled(true)
        }
        beatCue.checkAverage(fft) { triggerCount ->
            showLabel("BEAT $triggerCount", 28f, x = 500f, y = 2 * 28f + 20f)
            if (triggerCount >= 20) {
                flute1Cue.setEnabled(true)
                flute2Cue.setEnabled(true)
                flute3Cue.setEnabled(true)
            }
        }
    }

    private fun showLabel(label: String, textSize: Float = 22f, x: Float, y: Float = textSize + 20f) {
        textSize(textSize)
        fill(green)
        text(label, x, y)
    }

    override fun reset() {
        super.reset()
        vibratoCue.reset()
        beepTriggerCount = 0
    }
}