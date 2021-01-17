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
    private val maxLength = 100f // 100
    private var showBarNumbers = false

//    private val inputFile: SoundFile? by lazy { SoundFile(this, "input/miso.wav").apply { loop() } }
    private val inputFile: SoundFile? = null
    private val waveform by lazy { sound.waveform(samples, inputFile) }
    private val fft by lazy { sound.fft(bands, inputFile) }
    private val barWidthTotal = strokeWeight + spacing
    private val padding = 50f / strokeWeight
    private val numBars = floor(screen.widthF / barWidthTotal - padding)
    private val xOffset = (screen.widthF - numBars * barWidthTotal) / 2 + strokeWeight / 2
    private val fftTranslationY = 0.5f * screen.heightF
    private val waveformTranslationY = 0.4f * screen.heightF
    private val fftBars = List(numBars) { Bar(it) }
    private val fftBinMaxList = MutableList(numBars) { 0f }
    private val waveformBars = List(numBars) { Bar(it) }
    private val samples = numBars
    private val labelTextSize = 14f
    private var maxWaveformPeak = 0f
    private var selectedFftBins = mutableListOf<Int>()

    override fun draw() {
        background(grey3)
        stroke(grey7)
        pushMatrix()
        drawFft()
        drawWaveform()
        popMatrix()
        super.drawLongPressOverlay()
    }

    override fun mouseReleased() {
        val newSelected = getBarIndexForX(mouseXF)
        selectedFftBins.run {
            if (contains(newSelected)) {
                remove(newSelected)
            } else {
                add(newSelected)
            }
        }
        super.mouseReleased()
    }

    private fun drawWaveform() {
        showBarNumbers = false
        translate(0f, waveformTranslationY)
        waveform.analyze()
        var waveformPeak = 0f
        for (line in waveformBars.withIndex()) {
            val bin = waveform.data[line.index]
            line.value.update(bin)
            line.value.show()
            waveformPeak = max(waveformPeak, binValueToLength(bin))
        }
        showPeakGuideline(waveformPeak)
    }

    private fun showPeakGuideline(waveformPeak: Float) {
        // Current
        strokeWeight(2f)
        stroke(white)
        val y = -waveformPeak - strokeWeight / 2f
        line(0f, y, xOffset, y)
        // Max
        stroke(grey11)
        maxWaveformPeak = max(waveformPeak, maxWaveformPeak)
        val yMax = -maxWaveformPeak - strokeWeight / 2f
        line(0f, yMax, xOffset, yMax)
        // Max label
        stroke(white)
        textSize(labelTextSize)
        val textPadding = 5f
        val yText = constrain(yMax, -waveformTranslationY + labelTextSize + 4 * textPadding, 0f) - textPadding
        text(maxWaveformPeak, 0f, yText)
    }

    private fun drawFft() {
        showBarNumbers = true
        translate(0f, fftTranslationY)
        fft.analyze()
        for (line in fftBars.withIndex()) {
            val bin = fft.spectrum[line.index]
            line.value.update(bin)
            line.value.show()
            drawFftBinMax(line.index, bin)
        }
    }

    private fun drawFftBinMax(index: Int, binValue: Float) {
        val binCurrent = binValueToLength(binValue)
        val binMax = max(fftBinMaxList[index], binCurrent)
        fftBinMaxList[index] = binMax
        val x = getXForBar(index)
        // Current
        strokeWeight(3f)
        stroke(yellow)
        point(x, -binCurrent - strokeWeight / 2f)
        // Max
        strokeWeight(5f)
        stroke(orange)
        point(x, -binMax - strokeWeight / 2f)
    }

    private fun binValueToLength(bin: Float): Float = map(bin, -0.1f, 0.2f, 0f, maxLength)
    private fun getXForBar(i: Int) = xOffset + i * barWidthTotal
    private fun getBarIndexForX(x: Float) = floor((x - xOffset + strokeWeight/2f) / barWidthTotal)

    override fun reset() {
        inputFile?.stop()
        inputFile?.loop()
        maxWaveformPeak = 0f
        fftBinMaxList.replaceAll { 0f }
        selectedFftBins.clear()
    }

    inner class Bar(val i: Int) {

        private val history = LinkedList<Float>()
        private val maxHistorySize: Int = 5
        private var binSum = 0f

        fun update(bin: Float) {
            binSum += (bin - binSum) * smoothingFactor
            val scaledLength: Float = binValueToLength(binSum)
            addToHistory(scaledLength)
        }

        private fun addToHistory(scaledLength: Float) {
            if (history.size >= maxHistorySize) {
                history.removeFirst()
            }
            history.add(scaledLength)
        }

        fun show() {
            strokeWeight(strokeWeight)
            val x = getXForBar(i)
            for (lineLength in history.withIndex()) {
                setColor(i, lineLength.index)
                line(x, 0f, x, -lineLength.value)
            }
            showBarNumbers(x)
        }

        private fun setColor(barIndex: Int, historyIndex: Int) {
            if (historyIndex == maxHistorySize - 1) {
                stroke(if (showBarNumbers && selectedFftBins.contains(barIndex)) { darkRed } else { red })
            } else {
                stroke(orange, map(historyIndex, 1, maxHistorySize, 150, 0))
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