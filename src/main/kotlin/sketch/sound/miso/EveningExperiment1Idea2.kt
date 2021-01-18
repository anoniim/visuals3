package sketch.sound.miso

import BaseSketch
import Screen
import processing.core.PShape
import processing.core.PVector
import processing.sound.SoundFile
import sound.SoundHelper
import java.util.*

open class EveningExperiment1Idea2 : BaseSketch(
    Screen(1700, 1100, fullscreen = false),
    renderer = P2D
) {

    private val wfSamples = 900
    private val bands = 1024 // power of 2 (256)
    private val fft by lazy { sound.fft(bands, inputFile) }
    private val waveform by lazy { sound.waveform(wfSamples, inputFile) }
    private val inputFile: SoundFile by lazy {
        SoundFile(this, "input/miso.wav").apply {
            loop()
//            jump(15f) // vibrato
//            jump(60f) // piano
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

    private val initialCircleStep = 0.005f // 0.005f
    private val initialRadius = 80f
    private val diameter = 2 * initialRadius
    private val initialCircle = InitialCircle()
    private val growingCircles = mutableListOf<GrowingCircle>()
    private val center = PVector(0f, 0f)
    private val visibleDistanceFromCenter
        get() = dist(0f, 0f, halfWidthF / 2f, halfHeightF / 2f)
    private val scaleFactor = 1.4f
    private val vibratingCircleRadians = 3 * TWO_PI + QUARTER_PI / 2f
    private val angleStep = vibratingCircleRadians / wfSamples
    private val expansionVectors = List<PVector>(wfSamples) {
        PVector.fromAngle(it * angleStep).mult(scaleFactor)
    }
    private var centralColor = grey11
    private val colors = LinkedList<Int>(
        listOf(
            color(111, 111, 111),
            color(100, 100, 111),
            color(100, 100, 145),
            color(100, 130, 150),
            color(95, 135, 160),
            color(70, 140, 160),
            color(70, 155, 175),
            color(50, 165, 185),
            color(29, 173, 211),
            color(29, 173, 211),
            color(19, 160, 211),
            color(19, 160, 211),
            color(19, 173, 228),
            color(19, 173, 228),
            color(20, 183, 243),
            color(20, 183, 243),
            color(0, 160, 255),
            color(0, 160, 255),
            color(80, 180, 255),
            color(120, 200, 255),
            color(180, 230, 255),
            color(220, 255, 255),
            color(255, 255, 255)
        )
    )

    override fun setup() {
        println(visibleDistanceFromCenter)
    }

    override fun draw() {
        fft.analyze()
        waveform.analyze()

        background(grey3)
        translate(halfWidthF, halfHeightF)
        drawInitialCircle()
        drawGrowingCircles()

        vibratoSceneCue.checkAverage(fft)
        checkPianoSceneCue()
    }

    private fun drawInitialCircle() {
        initialCircle.update()
        initialCircle.show()
    }

    private fun drawGrowingCircles() {
        for (circle in growingCircles) {
            circle.show()
            circle.update()
        }
        growingCircles.removeIf { !it.isVisible }
    }

    private fun checkPianoSceneCue() {
        pianoSceneCue.checkAny(fft) { triggerCount ->
            onPianoTriggered()
            if (triggerCount == 6) {
                pianoSceneCue2.setEnabled(true)
            }
        }
        pianoSceneCue2.checkAny(fft) {
            onPianoTriggered()
        }
    }

    private fun onPianoTriggered() {
        centralColor = colors.poll() ?: white
        growingCircles.add(GrowingCircle(vibratingCircle(centralColor)))
    }

    private fun vibratingCircle(color: Int): PShape {
        return createShape().apply {
            beginShape()
            noFill()
            stroke(color)
            strokeWeight(1f)
            val wfData = waveform.data
            val angleStep = (vibratingCircleRadians) / wfData.size
            for (wfDataPoint in wfData.withIndex()) {
                val angle = wfDataPoint.index * angleStep
                val radius = initialRadius + wfDataPoint.value * 80f
                val x = cos(angle) * radius
                val y = sin(angle) * radius
                curveVertex(x, y)
            }
            endShape()
        }
    }

    inner class InitialCircle {

        private var angleProgress = 0f

        fun update() {
            if (!isComplete()) {
                angleProgress += initialCircleStep
            }
        }

        private fun isComplete() = angleProgress >= TWO_PI

        fun show() {
            if (!vibratoSceneCue.hasTriggered()) {
                noFill()
                stroke(grey11)
                strokeWeight(2f)
                arc(0f, 0f, diameter, diameter, 0f, angleProgress)
            } else {
                shape(vibratingCircle(centralColor))
            }
        }
    }

    inner class GrowingCircle(private var shape: PShape) {

        var isVisible = true
        private var lastDistanceFromCenter = 0f

        fun update() {
            var distFromCenter = Float.MAX_VALUE
            val newShape = createShape().apply {
                beginShape()
                stroke(centralColor, getNewAlpha(lastDistanceFromCenter))
                for (i in 0 until shape.vertexCount) {
                    val newVertex = shape.getVertex(i).add(expansionVectors[i]).add(PVector.random2D())
                    distFromCenter = min(newVertex.dist(center), distFromCenter)
                    lastDistanceFromCenter = distFromCenter
                    curveVertex(newVertex.x, newVertex.y)
                }
                endShape()
            }
            shape = newShape
            updateVisibility(distFromCenter)
        }

        private fun getNewAlpha(distFromCenter: Float) =
            map(distFromCenter, 0f, visibleDistanceFromCenter / 2f, 200f, 0f)

        private fun updateVisibility(distFromCenter: Float) {
            if (isVisible && distFromCenter > visibleDistanceFromCenter / 2) {
                isVisible = false
            }
        }

        fun show() {
            shape(shape)
        }
    }
}