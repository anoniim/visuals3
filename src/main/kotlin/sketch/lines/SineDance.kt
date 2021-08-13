package sketch.lines

import BaseSketch
import processing.core.PApplet
import processing.core.PVector
import util.translateToCenter

/**
 * TODO with a controller:
 * - Knobs for every parameter of the lines
 * - buttons to change modes like square/saw wave
 */
class SineDance : BaseSketch() {

    private val numOfLines = 26
    private val lines = MutableList(numOfLines) {
        val angle = TWO_PI / numOfLines
        Line(
            it,
            length = 0f,
            angle = it * angle
        )
    }

    override fun setup() {
    }

    private fun effects() {
        addEffect(1, 600) {
            length += 2f
        }
        addEffect(60, 160) {
            wavelength = 25f
            amplitudeFn = { _, x -> map(x, 0f, length / 2, 0f, 10f) * sin(it / (8 * TWO_PI)) }
        }
        addEffect(230, 500) {
            phase -= 0.1f
            amplitudeFn = { _, x -> -constrain(x/3, 0f, 100f) * sin(it / 100f) }
        }
        addEffect(430, 3000) {
            phase -= 1.5f
            angle -= sin(constrain(it/600f, 0f, TWO_PI/1000f))
        }
        addEffect(630, 200) {
            wavelength += it/100f
        }
        addEffect(830, ceil(TWO_PI * 100)) {
            thickness = 2 * abs(cos(it/100))
        }
        addEffect(900, 760) {
            if (wavelength > 5) {
                wavelength -= constrain(it / 200f, 0f, 0.31f)
            }
        }
        addEffect(1600, 600) {
            amplitudeFn = if (amplitude > 0 && thickness > 1.99f) {
                { ampl, _ -> ampl - 0.001f }
            } else {
                thickness = 1.99f
                { ampl, _ -> ampl + 0.0061f }
            }
        }
        addEffect(2200, 600) {
            amplitudeFn = { ampl, _ -> ampl - 0.01f }
        }
//        addEffect(300, 3000) {
//            phase -= 1f + it / 100f
//        }
    }

    override fun draw() {
        background(grey3)
        translateToCenter()
        rotate(-HALF_PI)
        effects()
        drawLines()
        println(frameCount)
    }

    private fun drawLines() {
        lines.forEach {
            it.update()
            it.draw()
        }
    }

    private fun addEffect(startFrame: Int, length: Int, effect: Line.(Float) -> Unit) {
        if (frameCount == startFrame) {
            lines.forEach {
                it.addEffect(frameCount, length, effect)
            }
        }
    }

    private inner class Line(
        private val index: Int,
        var origin: PVector = PVector(),
        var length: Float = TWO_PI * 100f,
        var angle: Float = 0f,
        var thickness: Float = 2f,
        var color: Int = grey11,
        var resolution: Float = 1f,
        var function: (Float, Float, Float) -> Float = sineWave
    ) {

        var time = 0f
        var phase = 0f
        var amplitude = 0f
        var amplitudeFn: (Float, Float) -> Float = { ampl, _ -> ampl }
        var wavelength = 1f

        private var effects = mutableListOf<Effect>()

        fun update() {
            time += 0.01f
            effects.forEach {
                it.execute(this)
            }
        }

        fun addEffect(startFrame: Int, length: Int, effect: Line.(Float) -> Unit) {
            effects.add(Effect(startFrame, length, effect))
        }

        fun draw() {
            noFill()
            stroke(color)
            strokeWeight(thickness)
            pushMatrix()
            rotate(angle)
            drawShape()
            popMatrix()
        }

        private fun drawShape() {
            beginShape()
            var x = 0f
            while (x <= length) {
                amplitude = amplitudeFn(amplitude, x)
                val y = function(x + phase, amplitude, wavelength)
                curveVertex(origin.x + x, origin.y + y)
                x += resolution
            }
            endShape()
        }
    }

    private inner class Effect(val startFrame: Int, val length: Int, val effect: Line.(Float) -> Unit) {
        fun execute(line: Line) {
            if (isRunning()) {
                effect(line, effectProgress)
            }
        }

        private val effectProgress: Float
            get() = (frameCount - startFrame).toFloat()

        private fun isRunning() = frameCount > startFrame && frameCount - startFrame < length
        private fun hasFinished() = frameCount - startFrame > length
    }
}


private val sineWave: (Float, Float, Float) -> Float = { x, ampl, wavelength ->
    ampl * PApplet.sin(x / wavelength)
}