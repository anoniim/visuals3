package sketch.lines

import BaseSketch

class RoundingLines : BaseSketch() {

    private val strokeColor = grey9
    private val fillColor = grey5
    private val backgroundColor = grey3

    override fun setup() {
//        smooth()
//        rectMode(CENTER)
        stroke(strokeColor)
        strokeWeight(2F)
//        fill(fillColor)
        noFill()
//        frameRate(1F)

    }

    private val circles: Int = 100

    override fun draw() {
        background(backgroundColor)
        val minRadius = 1F
        val maxRadius = screen.heightF * 1
        translate(screen.centerX, screen.heightF - 50)

        val frameCoef = frameCount % circles

        for (i in 1..circles) {
            val radius = map(i.toFloat(), 1F, circles.toFloat(), maxRadius, minRadius) * frameCoef
            ellipse(0F, -1 * (radius / 2), radius, radius)
//            ellipse(0F, -1*(radius/2), radius, radius)
        }
    }

    override fun mousePressed() {
        println("mouseYF: $mouseYF")
        println("mouseXF: $mouseXF")
    }
}


