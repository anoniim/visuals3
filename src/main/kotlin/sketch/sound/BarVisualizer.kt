package sketch.sound

import BaseSketch
import Screen
import processing.sound.SoundFile
import java.util.*

class BarVisualizer : BaseSketch(Screen(1500, 800), longClickClear = true) {

    private val bands = 1024 // power of 2 (256)
    private val smoothingFactor = 0.2f // 0.2
    private val strokeWeight = 20f // 1-100 (30)
    private val spacing = 5f // 5
    private val maxLength = 100f // 400
    private var showBarNumbers = true

//    private val inputFile by lazy { SoundFile(this, "input/miso.wav").apply { loop() } }
    private val inputFile: SoundFile? = null
    private val waveform by lazy { sound.waveform(samples, inputFile) }
    private val fft by lazy { sound.fft(bands, inputFile) }
    private val barWidthTotal = strokeWeight + spacing
    private val padding = 50f / strokeWeight
    private val numBars = floor(screen.widthF / barWidthTotal - padding)
    private val xOffset = (screen.widthF - numBars * barWidthTotal) / 2 + strokeWeight / 2
    private val fftBars = List(numBars) { Bar(it) }
    private val waveformBars = List(numBars) { Bar(it) }
    private val samples = numBars

    override fun draw() {
        background(grey3)
        stroke(grey7)
        pushMatrix()
        drawWaveform()
        drawFft()
        popMatrix()
        super.drawLongPressOverlay()
    }

    private fun drawWaveform() {
        showBarNumbers = false
        translate(0f, 0.4f * screen.heightF)
        waveform.analyze()
        var waveformPeak = 0f
        for (line in waveformBars.withIndex()) {
            val bin = waveform.data[line.index]
            line.value.update(bin).also { waveformPeak = max(waveformPeak, it) }
            line.value.show()
        }
        showPeakGuideline(waveformPeak)
    }

    private fun showPeakGuideline(waveformPeak: Float) {
        strokeWeight(2f)
        stroke(white)
        val top = -waveformPeak - strokeWeight/2
        line(0f, top, xOffset, top)
    }

    private fun drawFft() {
        showBarNumbers = true
        translate(0f, 0.5f * screen.heightF)
        fft.analyze()
        for (line in fftBars.withIndex()) {
            val bin = fft.spectrum[line.index]
            line.value.update(bin)
            line.value.show()
        }
    }

    override fun reset() {
        inputFile?.stop()
        inputFile?.loop()
    }

    inner class Bar(val i: Int) {

        private val history = LinkedList<Float>()
        private val maxHistorySize: Int = 5
        private var binSum = 0f

        fun update(bin: Float): Float {
            binSum += (bin - binSum) * smoothingFactor
            val scaledLength: Float = map(binSum, -0.1f, 0.2f, 0f, maxLength)
            return scaledLength.also { addToHistory(it) }
        }

        private fun addToHistory(scaledLength: Float) {
            if (history.size >= maxHistorySize) {
                history.removeFirst()
            }
            history.add(scaledLength)
        }

        fun show() {
            strokeWeight(strokeWeight)
            val x = xOffset + i * barWidthTotal
            for (lineLength in history.withIndex()) {
                setColor(lineLength)
                line(x, 0f, x, -lineLength.value)
            }
            showBarNumbers(x)
        }

        private fun setColor(lineLength: IndexedValue<Float>) {
            if (lineLength.index == maxHistorySize - 1) {
                stroke(red)
            } else {
                stroke(orange, map(lineLength.index, 1, maxHistorySize, 150, 0))
            }
        }

        private fun showBarNumbers(x: Float) {
            if (showBarNumbers) {
                stroke(white)
                textSize(strokeWeight)
                text(i, x - strokeWeight / 2, 0f)
            }
        }
    }
}