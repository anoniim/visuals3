package sketch.patterns

import BaseSketch
import Screen
import processing.core.PVector

class TimesTablesCircle : BaseSketch(Screen(800, 800, fullscreen = false)) {

    /* Some interesting multipliers: 17, 31, 49, 51, 56.x, 300 */
    private val maxNumber = 200 // 10-800 (200)
    private val minMultiplier = 295f // 0
    private val maxMultiplier = 300f // 300 (20)
    private val loopTime: Float = 100f // 1000f

    private val numbers: MutableList<PVector> = mutableListOf()
    private val size = 750f
    private val textSize = 24f
    private val textScale = 1.3f
    private var lastMultiplier = 0f
    private var frameCountF = 0f

    override fun setup() {
        initDots()
    }

    override fun draw() {
        translate(width / 2f, height / 2f)
        background(grey3)
        drawCircle()
        drawDots()
//        val multiplier = mousePositionControl()
        val multiplier = mousePressControl()
        drawMultiplierText(multiplier)
        drawLines(multiplier)
    }

    private fun drawMultiplierText(multiplier: Float) {
        fill(grey9)
        textSize(textSize)
        text(multiplier, -widthF/2 + textSize * textScale, -heightF/2 + textSize * textScale)
    }

    private fun mousePressControl(): Float {
        if (mousePressed) {
            if (mouseX < width / 2) {
                frameCountF--
                if(frameCountF < 0) frameCountF = loopTime
            } else {
                frameCountF++
            }
            lastMultiplier = map(frameCountF % loopTime, 0f, loopTime-1, minMultiplier, maxMultiplier)
        }
        return lastMultiplier
    }

    private fun mousePositionControl() = map(mouseXF, 0f, widthF, minMultiplier, maxMultiplier)

    private fun drawLines(multiplier: Float) {
        strokeWeight(1f)
        for (number in numbers.withIndex()) {
//            val red = map(number.index % 255f, 0f, 255f, 50f, 190f)
//            val blue = map((number.index + 150) % 255f, 0f, 255f, 0f, 150f)
//            stroke(red, 100f, blue, 150f)
            stroke(grey9)
            val product = floor(number.index * multiplier % maxNumber)
            val start = number.value
            val end = numbers[product]
            line(start.x, start.y, end.x, end.y)
        }
    }

    private fun initDots() {
        val radius = size / 2
        for (i in 0 .. maxNumber) {
            val angle = i * TWO_PI / maxNumber
            numbers.add(PVector(cos(angle) * radius, sin(angle) * radius))
        }
    }

    private fun drawDots() {
        stroke(grey11)
        strokeWeight(4f)
        for (number in numbers) {
            point(number.x, number.y)
        }
    }

    private fun drawCircle() {
        stroke(grey5)
        strokeWeight(1f)
        noFill()
        ellipse(0f, 0f, size, size)
    }
}