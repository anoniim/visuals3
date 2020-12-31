package util

import processing.core.PApplet
import processing.core.PApplet.map
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class NoiseLoop (
    private val applet: PApplet,
    val diameter: Float = 5F,
    val min: Float = 0F,
    val max: Float = 1F
) {

    private val centerX: Float = Random.nextInt(1000).toFloat()
    private val centerY: Float = Random.nextInt(1000).toFloat()

    fun value(angle: Float): Float {
        val xOff = map(cos(angle), -1F, 1F, centerX, centerX + diameter)
        val yOff = map(sin(angle), -1F, 1F, centerY, centerY + diameter)
        val noise = applet.noise(xOff, yOff)
        return map(noise, 0F, 1F, min, max)
    }
}