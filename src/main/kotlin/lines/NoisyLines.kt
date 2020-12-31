package lines

import RecordedSketch

class NoisyLines : RecordedSketch() {

    private val backgroundColor = grey3

    private val centerY: Float = random(1000F)
    private val centerZ: Float = random(1000F)

    private val amplitude = 100F // 100
    private val sineStretch = 100F // 10-200 (100)
    private val yVariation = 550F // 100-500 (250)
    private val smoothness = 0.001F * 10 // 1-30 (10)
    private val randomnessDiameter = 0.1F * 4 // 1-10 (4)
    private val stringCohesion = 0.01F * 10 // 5-25 (10)

    override fun setup() {
        record(frames = 480, take = 2, record = false)
        noFill()
    }

    override fun render(percent: Float) {
        background(backgroundColor)

        for (string in 0..20) {
            stroke(getStrokeColor(string))
            beginShape()
            for (x in 0..screen.width) {
                val sineWave = amplitude * sin(x / sineStretch)
                val noiseAngle = percent * TWO_PI + (string * stringCohesion)
                val yOff = map(sin(noiseAngle), -1F, 1F, centerY, centerY + randomnessDiameter)
                val zOff = map(cos(noiseAngle), -1F, 1F, centerZ, centerZ + randomnessDiameter)
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