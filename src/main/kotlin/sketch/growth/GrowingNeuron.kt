package sketch.growth

import BaseSketch
import util.SimplexNoise


class GrowingNeuron: BaseSketch(renderer = P2D) {

    // config
    private val zoom = 0.01f
    private val offset = 300f
    private val zNoise = 0.0005f // 0-0.001

    override fun setup() {
        noiseDetail(1)
        background(grey3)
    }

    override fun draw() {
        // config
//        fill(grey3, 40f)
//        rect(0f, 0f, screen.widthF, screen.heightF)

        var xx = random(width)
        var yy = random(height)

        strokeWeight(0.5f)
        stroke(grey11)
        noFill()
        beginShape()
        for (i in 0..2600) {
            val t = frameCount * zNoise
            val zoom = zoom.toDouble()
            val a: Float =
                PI * (SimplexNoise().eval(offset + xx * zoom, offset + yy * zoom, t.toDouble())).toFloat()
            xx += cos(a)
            yy += sin(a)
            vertex(xx, yy)
        }
        endShape()
    }
}