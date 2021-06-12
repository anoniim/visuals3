package sketch.waves

import RecordedSketch
import Screen

class NoisyWave : RecordedSketch(
    Screen(1200, 800),
    fullscreen = true
) {

    private val backgroundColor = grey3

    private val waveCenterY: Float = random(1000F)
    private val waveCenterZ: Float = random(1000F)

    private val numOfStrings = 50
    private val amplitude = 50F // 100
    private val sineStretch = 50F // 10-200 (100)
    private val yVariation = 100F // 100-500 (250)
    private val smoothness = 0.001F * 12 // 1-30 (10)
    private val randomnessDiameter = 0.1F * 9 // 1-10 (4)
    private val stringCohesion = 0.01F * 5 // 5-25 (5)

    override fun setup() {
        record(frames = 180, label = "2", skipFrames = 5, record = false)
        noFill()
//        frameRate(10F)
    }

    override fun render(percent: Float) {
        background(backgroundColor)

        for (string in 0..numOfStrings) {
            stroke(getStrokeColor(string))
            beginShape()
            for (x in 0..screen.width) {
                val sineWave = amplitude * sin(x / sineStretch)
                val noiseAngle = percent * TWO_PI + (string * stringCohesion)
                val yOff = map(sin(noiseAngle), -1F, 1F, waveCenterY, waveCenterY + randomnessDiameter)
                val zOff = map(cos(noiseAngle), -1F, 1F, waveCenterZ, waveCenterZ + randomnessDiameter)
                val noiseWave = map(
                    noise(x * smoothness, yOff, zOff),
                    0F,
                    1F,
                    -yVariation,
                    yVariation
                )
                val y = screen.centerY + sineWave + noiseWave
                vertex(x.toFloat(), y)
            }
            endShape()
        }
    }

    private fun getStrokeColor(string: Int): Int {
        val red = map(string.toFloat(), 0F, 20F, 0F, 255F).toInt()
        val green = 100
        val blue = 200
        val alpha = 80
        return color(red, green, blue, alpha)
    }
}