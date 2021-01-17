package sketch.sound.miso

import processing.sound.SoundFile
import sketch.sound.BarVisualizer
import sound.SoundHelper

class EveningExperiment1Visualizer : BarVisualizer() {

    private var pianoTriggerCount = 0

    override fun setup() {
        inputFile = SoundFile(this, "input/miso.wav").apply {
            loop()
//            jump(15f) // vibrato
            jump(60f) // piano
//            jump(105f) // piano chaos
        }
    }

    private val vibratoSceneCue by lazy {
        SoundHelper.FftSceneCue(listOf(3, 4, 5, 6, 7), 0.01f)
    }
    private val pianoSceneCue by lazy {
        SoundHelper.FftSceneCue(listOf(14, 15, 16), 0.08f).apply {
            setRepeatable(throttleMillis = 1000L, triggerLimit = 6)
        }
    }
    private val pianoSceneCue2 by lazy {
        SoundHelper.FftSceneCue(listOf(14, 15, 16), 0.10f).apply {
            setRepeatable(throttleMillis = 5000L)
            setEnabled(false)
        }
    }

    override fun draw() {
        super.draw()
        checkVibratoSceneCue()
        drawCueCount()
    }

    private fun drawCueCount() {
        for(i in 0 until pianoTriggerCount) {
            stroke(green)
            strokeWeight(10f)
            point(600f + i * 20f, 26f)
        }
    }

    private fun checkVibratoSceneCue() {
        vibratoSceneCue.checkAverage(fft) {
            showLabel(200f, "VIBRATO")
        }
        pianoSceneCue.checkAny(fft) { triggerCount ->
            showLabel(400f, "PIANO")
            pianoTriggerCount++
            if (triggerCount == 6) {
                pianoSceneCue2.setEnabled(true)
            }
        }
        pianoSceneCue2.checkAny(fft) { triggerCount ->
            showLabel(500f, "PIANO $triggerCount")
            pianoTriggerCount++
        }
    }

    private fun showLabel(x: Float, label: String) {
        val labelTextSize = 22f
        textSize(labelTextSize)
        fill(green)
        text(label, x, labelTextSize + 20f)
    }

    override fun reset() {
        super.reset()
        vibratoSceneCue.reset()
        pianoTriggerCount = 0
    }
}