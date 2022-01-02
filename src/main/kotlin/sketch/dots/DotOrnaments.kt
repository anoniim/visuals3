package sketch.dots

import BaseSketch
import Screen
import com.hamoid.VideoExport
import input.MidiController
import input.MidiController.Companion.CONTROL_18
import input.MidiController.Companion.CONTROL_19
import processing.core.PApplet
import processing.core.PVector
import util.point
import util.times
import util.translateToCenter


fun main() {
    PApplet.main(DotOrnaments::class.java)
}

class DotOrnaments : BaseSketch(
    screen = Screen.LG_ULTRAWIDE
) {

    // config
    private val colorPalette = colors.sunrise.toMutableList().apply { add(color(249, 65, 68)) }
    private var gaps = 0.5f
    private var angleSpeedFactor = 40_000f
//        get() = 6000f - mouseXF / widthF * 6000f

    private val angleSpeed //= 1f
        get() = frameCount / angleSpeedFactor

    private val midiController by lazy { MidiController(this, 1, 2) }
    private val videoExport by lazy { VideoExport(this) }

    override fun setup() {
        super.setup()
        midiController.on(CONTROL_18) { pitch: Int ->
            angleSpeedFactor = map(pitch.toFloat(), 0f, 127f, 40_000f, 1_000f)
        }
        midiController.on(CONTROL_19) { pitch: Int ->
            gaps = map(pitch.toFloat(), 0f, 127f, 0.1f, 2f)
        }
        // record
//        videoExport.startMovie()
    }

    override fun draw() {
        background(grey3)
        translateToCenter()

        var angle = 0f
        var radius = 0f
        while (radius < halfWidthF) {
            val location = PVector.fromAngle(angle) * radius
            drawPoint(location, angle)
            angle += angleSpeed
            radius += gaps
        }
        videoExport.saveFrame()
    }

    private fun drawPoint(location: PVector, angle: Float) {
        stroke(getColor(angle))
        strokeWeight(10f)
        point(location)
    }

    private fun getColor(angle: Float): Int {
        return lerpColors(angle % TWO_PI * 1f / TWO_PI, *colorPalette.toIntArray())
    }

    private fun lerpColors(amt: Float, vararg colors: Int): Int {
        if (colors.size == 1) return colors[0]
        val cunit = 1f / (colors.size - 1)
        return lerpColor(colors[floor(amt / cunit)], colors[ceil(amt / cunit)], amt % cunit / cunit)
    }
}