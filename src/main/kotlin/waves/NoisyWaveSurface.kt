package waves

import RecordedSketch
import Screen

class NoisyWaveSurface : RecordedSketch(Screen(1200, 800)) {

    private val backgroundColor = grey3

    private val centerY: Float = random(1000F)
    private val centerZ: Float = random(1000F)

    private val numOfStrings = 100
    private val spacing: Float = 10F
    private val amplitude = 5F // 100
    private val sineStretch = 3F // 10-200 (100)
    private val yVariation = 200F // 100-500 (250)
    private val smoothness = 0.001F * 1 // 1-30 (10)
    private val randomnessDiameter = 0.1F * 9 // 1-10 (4)
    private val stringCohesion = 0.01F * 5 // 5-25 (5)

    override fun setup() {
        record(frames = 240, label = "1", skipFrames = 5, record = false)
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
                val noiseAngle = percent * TWO_PI - (string * stringCohesion)
                val yOff = map(sin(noiseAngle), -1F, 1F, centerY, centerY + randomnessDiameter)
                val zOff = map(cos(noiseAngle), -1F, 1F, centerZ, centerZ + randomnessDiameter)
                val noiseWave = map(
                    noise(x * smoothness, yOff, zOff),
                    0F,
                    1F,
                    -yVariation,
                    yVariation
                )
                val y = -100F + string * spacing + sineWave + noiseWave
                vertex(x.toFloat(), y)
            }
            endShape()
        }
    }

    private fun getStrokeColor(string: Int): Int {
        val red = 50
        val green = map(string.toFloat(), 0F, numOfStrings.toFloat(), 100F, 200F).toInt()
        val blue = 200
        val alpha = 80
        return color(red, green, blue, alpha)
    }
}