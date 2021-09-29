package sketch.sound.miso

import BaseSketch
import Screen
import com.hamoid.VideoExport
import processing.core.PShape
import processing.core.PVector
import processing.sound.SoundFile
import sound.SoundHelper
import javax.swing.Spring.scale

open class EveningExperiment1Idea2 : BaseSketch(
    Screen(1600, 900),
    fullscreen = true,
    longClickClear = true,
    renderer = P2D
) {

    private val wfSamples = 900
    private val bands = 1024 // power of 2 (256)
    private val fft by lazy { sound.fft(bands, inputFile) }
    private val waveform by lazy { sound.waveform(wfSamples, inputFile) }
    private val inputFile: SoundFile by lazy {
        SoundFile(this, "data/input/miso.wav").apply {
            play()
//            jump(15f) // vibrato
//            jump(60f) // piano
//            jump(105f) // piano chaos
        }
    }
    private val vibratoSceneCue by lazy {
        SoundHelper.FftSceneCue(listOf(3, 4, 5, 6, 7), 0.01f)
    }
    private val pianoSceneCue by lazy {
        SoundHelper.FftSceneCue(listOf(14, 15, 16), 0.07f).apply {
            setRepeatable(throttleMillis = 1000L, triggerLimit = 6)
        }
    }
    private val pianoSceneCue2 by lazy {
        SoundHelper.FftSceneCue(listOf(14, 15, 16), 0.10f).apply {
            setRepeatable(throttleMillis = 5000L)
            setEnabled(false)
        }
    }

    private val initialCircleStep = 0.01f // 0.005f
    private val initialRadius = 80f
    private val diameter = 2 * initialRadius
    private val initialCircle = InitialCircle()
    private val growingCircles = mutableListOf<GrowingCircle>()
    private val center = PVector(0f, 0f)
    private val visibleDistanceFromCenter
        get() = dist(0f, 0f, halfWidthF / 2f, halfHeightF / 2f) / 1.3f
    private val vibratingCirclesScaleFactor = 1.8f
    private val vibratingCircleRadians = 3 * TWO_PI + QUARTER_PI / 2f
    private val angleStep = vibratingCircleRadians / wfSamples
    private val expansionVectors = List<PVector>(wfSamples) {
        PVector.fromAngle(it * angleStep).mult(vibratingCirclesScaleFactor)
    }
    private var centralColor = grey11
    private var startScaling = false
    private var globalScaleFactor = 1f
    private var globalScaleStep = 0.001f
    private val globalScaleLimit = 2.4f

    private val videoExport: VideoExport by lazy {
        VideoExport(this).apply {
            setAudioFileName("input/miso.wav")
            setFrameRate(28.75f)
//            setMovieFileName("data/output")
        }
    }

    override fun setup() {
        frameRate(30f)
//        videoExport.startMovie()
    }

    override fun draw() {
        fft.analyze()
        waveform.analyze()

        background(grey3)
        translate(halfWidthF, halfHeightF)
        finalScaling()
        drawInitialCircle()
        drawGrowingCircles()

        vibratoSceneCue.checkAverage(fft)
        checkPianoSceneCue()

        drawLongPressOverlay {
            scale(1/globalScaleFactor)
            translate(-halfWidthF, -halfHeightF)
        }
        videoExport.saveFrame()
    }

    override fun reset() {
        videoExport.endMovie()
        exit()
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
        pianoSceneCue2.checkAny(fft) {  triggerCount ->
            if (triggerCount == 1) startScaling = true
            onPianoTriggered()
        }
    }

    private fun onPianoTriggered() {
        centralColor = colors.dirtyBeach.poll() ?: white
        growingCircles.add(GrowingCircle(vibratingCircle(centralColor)))
    }

    private fun vibratingCircle(color: Int): PShape {
        return createShape().apply {
            beginShape()
            noFill()
            stroke(color)
            strokeWeight(1f)
            val wfData = waveform.data
            val angleStep = vibratingCircleRadians / wfData.size
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

    private fun finalScaling() {
        if (startScaling) {
            scale(globalScaleFactor)
            globalScaleFactor += globalScaleStep
            if(globalScaleFactor >= globalScaleLimit) globalScaleStep *= -1
        }
    }

    private inner class InitialCircle {

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

    private inner class GrowingCircle(private var shape: PShape) {

        var isVisible = true
        private var lastDistanceFromCenter = 0f

        fun update() {
            for (i in 0 until shape.vertexCount) {
                val newVertex = shape.getVertex(i).add(expansionVectors[i]).add(PVector.random2D())
                shape.setVertex(i, newVertex.x, newVertex.y)
            }
            lastDistanceFromCenter = shape.getVertex(0).dist(center)
            shape.setStroke(getNewAlpha(lastDistanceFromCenter))
            updateVisibility(lastDistanceFromCenter)
        }

        private fun getNewAlpha(distFromCenter: Float): Int {
            val newAlpha = map(distFromCenter, 0f, visibleDistanceFromCenter, 200f, 0f)
            return color(
                red(centralColor),
                green(centralColor),
                blue(centralColor),
                newAlpha
            )
        }

        private fun updateVisibility(distFromCenter: Float) {
            if (isVisible && distFromCenter > visibleDistanceFromCenter) {
                isVisible = false
            }
        }

        fun show() {
            shape(shape)
        }
    }
}