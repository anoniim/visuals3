package sketch.lines

import BaseSketch
import Screen

class BrokenPianoStrings : BaseSketch(
    Screen(fullscreen = false),
    renderer = P3D,
    smoothLevel = 8
) {

    // config
    private val spacing: Float = 25f
    private val lineWidth = 3f

    private val upLines: MutableList<Line> by lazy {
        val numOfLines = ceil(widthF / spacing)
        MutableList(numOfLines) { Line(it * spacing, 1) }
    }
    private val downLines: MutableList<Line> by lazy {
        val numOfLines = ceil(widthF / spacing)
        MutableList(numOfLines) { Line(it * spacing, -1) }
    }

    override fun draw() {
        translate(0f, 2 * heightF / 3f)
        background(grey3)
        strokeWeight(lineWidth)
        stroke(yellow)
        noFill()

        upLines.forEach {
            it.update()
            it.draw()
        }
        downLines.forEach {
            it.update()
            it.draw()
        }
    }

    private inner class Line(
        private var x: Float = 0f,
        private val direction: Int
    ) {

        fun update() {
            x += 1f
            if (x > widthF) {
                x -= upLines.size * spacing
            }
        }

        fun draw() {
            line(x, 10 * sin(x/10f), 0f, x, -direction * heightF, 1000f)
        }
    }
}
