package pixels

import BaseSketch
import Screen
import processing.core.PGraphics

import processing.core.PImage

/** Daniel Shiffman
 * http://codingtra.in
 * http://patreon.com/codingtrain
 * Fire Effect
 * Video: https://youtu.be/X0kjv0MozuY
 * Algorithm: https://web.archive.org/web/20160418004150/http://freespace.virgin.net/hugo.elias/models/m_fire.htm
 */
class FireEffect : BaseSketch(Screen(600, 400)) {

    var w = 600
    var h = 400
    lateinit var buffer1: PGraphics
    lateinit var buffer2: PGraphics
    var cooling: PImage = createImage(w, h, RGB)

    private var yStart = 0.0f
    private val increment = 0.04f
    private val circleSize = 50f

    override fun setup() {
        buffer1 = createGraphics(w, h)
        buffer2 = createGraphics(w, h)
    }

    fun cool() {
        cooling.loadPixels()
        var xoff = 0.0f // Start xoff at 0
        // For every x,y coordinate in a 2D space, calculate a noise value and produce a brightness value
        for (x in 0 until w) {
            xoff += increment // Increment xoff
            var yoff = yStart // For every xoff, start yoff at 0
            for (y in 0 until h) {
                yoff += increment // Increment yoff

                // Calculate noise and scale by 255
                val n = noise(xoff, yoff)
                val bright = pow(n, 3f) * 255

                // Try using this line instead
                //float bright = random(0,255);

                // Set each pixel onscreen to a grayscale value
                cooling.pixels[x + y * w] = color(bright)
            }
        }
        cooling.updatePixels()
        yStart += increment
    }

    fun fire(rows: Int) {
        buffer1.beginDraw()
        buffer1.loadPixels()
        for (x in 0 until w) {
            for (j in 0 until rows) {
                val y = h - (j + 1)
                val index = x + y * w
                buffer1.pixels[index] = color(255)
            }
        }
        buffer1.updatePixels()
        buffer1.endDraw()
    }

    override fun draw() {
//        scale(1.5f)
        fire(10)
        if (mousePressed) {
            buffer1.beginDraw()
            buffer1.fill(255)
            buffer1.noStroke()
            buffer1.ellipse(mouseX.toFloat(), mouseY.toFloat(), circleSize, circleSize)
            buffer1.endDraw()
        }
        cool()
        background(0)
        buffer2.beginDraw()
        buffer1.loadPixels()
        buffer2.loadPixels()
        for (x in 1 until w - 1) {
            for (y in 1 until h - 1) {
                val index0 = x + y * w
                val index1 = x + 1 + y * w
                val index2 = x - 1 + y * w
                val index3 = x + (y + 1) * w
                val index4 = x + (y - 1) * w
                val c1: Int = buffer1.pixels[index1]
                val c2: Int = buffer1.pixels[index2]
                val c3: Int = buffer1.pixels[index3]
                val c4: Int = buffer1.pixels[index4]
                val c5: Int = cooling.pixels[index0]
                var newC = brightness(c1) + brightness(c2) + brightness(c3) + brightness(c4)
                newC = (newC * 0.25 - brightness(c5)).toFloat()
                buffer2.pixels[index4] = color(newC)
            }
        }
        buffer2.updatePixels()
        buffer2.endDraw()

        // Swap
        val temp = buffer1
        buffer1 = buffer2
        buffer2 = temp
        image(buffer2, 0f, 0f)
        image(cooling, w.toFloat(), 0f)
    }
}