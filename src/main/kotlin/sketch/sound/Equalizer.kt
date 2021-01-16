package sketch.sound

import BaseSketch
import Screen
import java.util.*

class Equalizer : BaseSketch(Screen(1500, 800)) {

    private val bands = 256 // power of 2 (256)
    private val smoothingFactor = 0.2f // 0.2
    private val strokeWeight = 10f // 1-100 (30)
    private val spacing = 5f // 5
    private val maxLength = 400f // 400

    private val waveform by lazy { sound.waveform(samples) }
    private val fft by lazy { sound.fft(bands) }
    private val lineWidthTotal = strokeWeight + spacing
    private val padding = 50f / strokeWeight
    private val numLines = floor(screen.widthF / lineWidthTotal - padding)
    private val xOffset = (screen.widthF - numLines * lineWidthTotal) / 2 + strokeWeight / 2
    private val lines1 = List(numLines) { Line(it) }
    private val samples = numLines

    override fun draw() {
        background(grey3)
        translate(0f, 0.7f * screen.heightF)
        strokeWeight(strokeWeight)
        stroke(grey7)

        waveform.analyze()
        fft.analyze()

        for (line in lines1.withIndex()) {
//            val sample = waveform.data[line.index]
            val sample = fft.spectrum[line.index]
            line.value.update(sample)
            line.value.show()
        }
    }

    inner class Line(val i: Int) {

        private val history = LinkedList<Float>()
        private val maxHistorySize: Int = 5
        private var length: Float = 0f
        private var sampleSum = 0f

        fun update(sample: Float) {
            sampleSum += (sample - sampleSum) * smoothingFactor
            val scaledLength: Float = map(sampleSum, -0.1f, 0.2f, 0f, maxLength)
            length = scaledLength
            addToHistory(scaledLength)
        }

        private fun addToHistory(scaledLength: Float) {
            if (history.size >= maxHistorySize) {
                history.removeFirst()
            }
            history.add(scaledLength)
        }

        fun show() {
            val x = xOffset + i * lineWidthTotal
            for (lineLength in history.withIndex()) {
                if (lineLength.index == maxHistorySize-1) {
                    stroke(red)
                } else {
                    stroke(orange, map(lineLength.index, 1, maxHistorySize, 150, 0))
                }
                line(x, 0f, x, -lineLength.value)
            }
        }
    }
}