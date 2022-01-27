package sketch.sound

import BaseSketch
import Screen
import processing.sound.SoundFile
import sound.SoundHelper.Companion.getSelectedFftBinAverage
import java.util.*

open class BarVisualizer(
    resolution: Int = 88,
) : BaseSketch(Screen(1500, 800), longClickClear = true) {

    private val bands = 1024 // power of 2 (256, 512, 1024, 2048)
    private val numBars = resolution // 16-100 (30)
    private val maxLength = 100f // 100
    private val smoothingFactor = 0.2f // 0.2
    private var showBarNumbers = false

    private val sidePadding = 35f // 35
    private val barSpacing = 5f // 5
    private val strokeWeight = (screen.widthF - sidePadding - numBars * barSpacing) / numBars
    private val barWidthTotal = strokeWeight + barSpacing
    private val xOffset = sidePadding + barWidthTotal / 2f
    private val fftTranslationY = 0.5f * screen.heightF
    private val waveformTranslationY = 0.4f * screen.heightF
    private val fftBars = List(numBars) { Bar(it) }
    private val fftBinMaxList = MutableList(numBars) { 0f }
    private val waveformBars = List(numBars) { Bar(it) }
    private val samples = numBars
    private val labelTextSize = 14f
    private val labelPadding = 20f
    private var maxWaveformPeak = 0f
    private var selectedFftBins = mutableListOf<Int>()
    private var timeOffset = 0

    protected var inputFile: SoundFile? = null
    protected val waveform by lazy { sound.waveform(samples, inputFile) }
    protected val fft by lazy { sound.fft(bands, inputFile) }

    override fun draw() {
        background(grey3)
        stroke(grey7)
        pushMatrix()
        drawTime()
        drawFft()
        drawWaveform()
        popMatrix()
        drawLongPressOverlay()
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

    private fun drawTime() {
        fill(white)
        textSize(2*labelTextSize)
        if (timeOffset == 0) timeOffset = millis()
        val time = (millis() - timeOffset) / 1000
        text("${time}s", labelPadding, labelTextSize + labelPadding)
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
            waveformPeak = max(waveformPeak, bin)
        }
        showPeakGuideline(waveformPeak)
    }

    private fun showPeakGuideline(waveformPeak: Float) {
        // Current
        strokeWeight(2f)
        stroke(white)
        val y = -binValueToLength(waveformPeak) - strokeWeight / 2f
        line(0f, y, sidePadding, y)
        // Max
        stroke(grey11)
        maxWaveformPeak = max(waveformPeak, maxWaveformPeak)
        val yMax = -binValueToLength(maxWaveformPeak) - strokeWeight / 2f
        line(0f, yMax, sidePadding, yMax)
        // Max label
        fill(white)
        textSize(labelTextSize)
//        val yText = constrain(yMax, -waveformTranslationY + labelTextSize + labelPadding, 0f) - labelPadding/2f
        text(maxWaveformPeak, 0f, 0f) // yText to show label at peak height
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
        drawSelectedFftBinAverage()
    }

    private fun drawSelectedFftBinAverage() {
        fill(white)
        textSize(labelTextSize)
        val label = getSelectedFftBinAverage(fft, selectedFftBins)
        val yTopCorner = -fftTranslationY + labelTextSize + labelPadding
        text(label, 0f, 0f) //  yTopCorner to show label in top corner
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
    private fun getBarIndexForX(x: Float) = floor((x - xOffset + strokeWeight / 2f) / barWidthTotal)

    override fun reset() {
        inputFile?.stop()
        inputFile?.loop()
        maxWaveformPeak = 0f
        fftBinMaxList.replaceAll { 0f }
        selectedFftBins.clear()
        timeOffset = millis()
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
                setStrokeColor(i, lineLength.index)
                line(x, 0f, x, -lineLength.value)
            }
            showBarNumbers(x)
        }

        private fun setStrokeColor(barIndex: Int, historyIndex: Int) {
            if (historyIndex == maxHistorySize - 1) {
                stroke(
                    if (showBarNumbers && selectedFftBins.contains(barIndex)) {
                        darkRed
                    } else {
                        red
                    }
                )
            } else {
                stroke(orange, map(historyIndex, 1, maxHistorySize, 150, 0))
            }
        }

        private fun showBarNumbers(x: Float) {
            if (showBarNumbers) {
                fill(white)
                textSize(strokeWeight)
                text(i, x - strokeWeight / 2, 0f)
            }
        }
    }
}