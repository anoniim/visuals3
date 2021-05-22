package sketch.spirals

import BaseSketch
import Screen
import processing.core.PVector
import util.line
import util.translateToCenter

class PolygonSpiral : BaseSketch(Screen(fullscreen = true)) {


    // config
    private val baseAngle = PI / 2f
    private val radiusFactor = 4 // 2-10
    private val angleStep = baseAngle + 0.006f
    private val scale = 100f // at least minScale but doesn't matter

    private val minScale = 100f
    private var angle = 0f
    private val path = mutableListOf(vectorFromAngle(0f))

    override fun setup() {
//        frameRate(15f)
    }

    override fun draw() {
        background(grey3)
        stroke(grey11)
        updateStrokeWeight()
        translateToCenter()
        updateScale()

        addNewPoint()
        drawPath()
    }

    private fun drawPath() {
        for (i in 1 until path.size) {
            line(path[i - 1], path[i])
        }
    }

    private fun addNewPoint() {
        angle += angleStep
        val newPoint = vectorFromAngle(angle)
        path.add(newPoint)
    }

    private fun vectorFromAngle(angle: Float) = PVector.fromAngle(angle).setMag(getRadiusMagnitude())

    private fun getRadiusMagnitude(): Float {
        return if (scale >= minScale) {
            120f * scale - (radiusFactor * frameCount * scale/10f)
        } else {
            1500f - (radiusFactor * frameCount)
        }
    }

    private fun updateScale() {
        if (scale >= minScale) {
            scale(1f / scale + frameCount / (60f * scale))
        }
    }

    private fun updateStrokeWeight() {
        if (scale >= minScale) {
            strokeWeight(scale / 10f - 10f / (frameCount))
        }
    }

    override fun keyPressed() {
        if (key == ' ') {
            noLoop()
        } else {
            loop()
        }
    }
}
