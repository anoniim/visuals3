package stars

import BaseSketch
import processing.core.PVector
import kotlin.random.Random


class Starfield : BaseSketch() {

    private val strokeColor = grey9
    private val fillColor = grey5
    private val fillColor2 = grey9
    private val backgroundColor = grey3

    private val stars: List<Star> = List(600) { Star() }
    private var directionCoords = Pair(screen.centerX, screen.centerY)

    override fun setup() {
//        smooth()
//        frameRate(1F)
//        fill(fillColor)
    }

    override fun draw() {
        background(backgroundColor)
//        translate(screen.widthF / 2, screen.heightF / 2)
        if (mousePressed) {
            directionCoords = Pair(mouseXF, mouseYF)
        }
        translate(directionCoords.first, directionCoords.second)

        for (star in stars) {
            star.update()
            star.draw()
        }
    }

    open inner class Star {

        private var x: Float = random(-screen.widthF, screen.widthF)
        private var y: Float = random(-screen.heightF, screen.heightF)
        private var z: Float = random(screen.widthF)
        private var maxSize = 12F
        private val speed: Float = 30F

        fun update() {
            z -= speed
            if (z < 1) {
                reset()
            }
        }

        fun draw() {
            noStroke()
            fill(fillColor2)

            val sx = map(x/z, 0F, 1F, 0F, screen.widthF)
            val sy = map(y/z, 0F, 1F, 0F, screen.heightF)
            val size = map(z, screen.widthF, 0F, 1F, maxSize)
            ellipse(sx, sy, size, size)
        }

        private fun reset() {
            x = random(-screen.widthF, screen.widthF)
            y = random(-screen.heightF, screen.heightF)
            z = screen.widthF
        }
    }
}


