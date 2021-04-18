package sketch.sound

import BaseSketch
import Screen
import processing.sound.SoundFile

class AudioClock : BaseSketch(Screen(1000, 200), renderer = P2D) {

    private var startTime: Long = 0L
    private val signalLength = 9f
    private val clockLength = 3f
    private val totalSamples = 72f
    private val trackLength = signalLength + clockLength
    private var sampleNumber = 0
    private var text = ""

    private val clockTextSize = 102f

    private val inputFile: SoundFile by lazy {
        SoundFile(this, "data/input/hodiny.wav")
    }

    override fun setup() {
        background(grey3)
    }

    override fun draw() {
        background(if (inputFile.isPlaying) yellow else grey3)
        sampleNumber = floor(map(mouseXF, 0f, screen.widthF, 0f, totalSamples))

        val hour = floor(sampleNumber / 6f)
        val minute = sampleNumber % 6
        text = "$hour:${minute}0"

        drawClock()
        stopSoundAtTheEnd()
    }

    private fun drawClock() {
        fill(if (inputFile.isPlaying) grey3 else yellow)
        textSize(clockTextSize)
        textMode(CENTER)
        text(text, screen.centerX - 1.5f * clockTextSize, screen.centerY)
    }

    private fun stopSoundAtTheEnd() {
        if (System.currentTimeMillis() - startTime >= trackLength * 1000 - 100) {
            pauseSound()
        }
    }

    private fun playSound() {
        if (!inputFile.isPlaying) {
            inputFile.cue(sampleNumber * trackLength + trackLength)
            inputFile.play()
            startTime = System.currentTimeMillis()
        }
    }

    private fun pauseSound() {
        inputFile.stop()
        startTime = 0L
    }

    override fun mousePressed() {
        playSound()
    }

    override fun mouseReleased() {
        pauseSound()
    }

//    override fun keyPressed(event: KeyEvent?) {
//        if(keyCode == ) {
//
//        }
//    }
}