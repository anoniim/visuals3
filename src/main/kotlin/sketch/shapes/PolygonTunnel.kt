package sketch.shapes

import BaseSketch
import Screen
import processing.core.PVector
import util.line
import util.translateToCenter

class PolygonTunnel : BaseSketch(Screen(fullscreen = true)) {

    // config
//    private val angleStep = 2*PI/3f // triangle
    private val angleStep = PI/2f // square
//    private val angleStep = PI/3f // pentagon
//    private val angleStep = TWO_PI/5f // hexagon

    private var angle = 0f
    private val path = mutableListOf(vectorFromAngle(0f))

    override fun draw() {
        background(grey3)
        stroke(grey11)
        strokeWeight(10f / (frameCount))

        translateToCenter()
        scale(frameCount / 10f)
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

    private fun vectorFromAngle(angle: Float) = PVector.fromAngle(angle).setMag(500f / frameCount)

}
