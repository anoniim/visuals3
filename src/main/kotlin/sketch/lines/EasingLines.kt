package sketch.lines

import BaseSketch
import com.hamoid.VideoExport
import org.gicentre.utils.move.Ease
import processing.core.PApplet
import processing.core.PVector
import util.*

fun main() {
    PApplet.main(EasingLines::class.java)
}

class EasingLines : BaseSketch(
    screen = Screen.LG_ULTRAWIDE
) {

    // config
    private val lineWidth = 10f
    private val spacing = 10f
    private val recordingEnabled = false // record

    private val numOfHorizontalLines by lazy { ceil(widthF / (lineWidth + spacing)) }
    private val videoExport by lazy { VideoExport(this) }

    private val topDown by lazy {
        LineSet(List(numOfHorizontalLines) {
            val x = lineWidth / 2f + it * (lineWidth + spacing)
            val origin = PVector(x, 0f)
            val destination = PVector(x, heightF)
            val delayDelta = 3
            val middle = numOfHorizontalLines / 2
            Line(
                origin, easeOut = Ease::cubicOut,
                destination, easeIn = Ease::cubicIn,
                color = getColor(abs(middle - it), 0, numOfHorizontalLines - middle, colors.flame),
                animationLength = 60 * 2,
                delayFrames = abs((middle - it) * delayDelta),
                shouldLoop = false,
            )
        })
    }

    private val bottomUp by lazy {
        LineSet(List(numOfHorizontalLines) {
            val x = lineWidth / 2f + it * (lineWidth + spacing)
            val origin = PVector(x, heightF)
            val destination = PVector(x, 0f)
            val delayDelta = 3
            val middle = numOfHorizontalLines / 2
            Line(
                origin, easeOut = Ease::cubicOut,
                destination, easeIn = Ease::cubicIn,
                color = getColor(abs(middle - it), 0, numOfHorizontalLines - middle, colors.flame),
                animationLength = 60 * 2,
                delayFrames = abs((middle - it) * delayDelta),
                shouldLoop = false,
            )
        })
    }

    private val leftRight by lazy {
        LineSet(List(numOfHorizontalLines) {
            val x = lineWidth / 2f + it * (lineWidth + spacing)
            val origin = PVector(x, 0f)
            val destination = PVector(x, heightF)
            val delayDelta = 1
            Line(
                origin, easeOut = Ease::cubicOut,
                destination, easeIn = Ease::cubicIn,
                color = getColor(it, numOfHorizontalLines, 0, colors.sunrise),
                animationLength = 60 * 2,
                delayFrames = abs((0 - it) * delayDelta),
                shouldLoop = false,
            )
        })
    }

    private val rightLeft by lazy {
        LineSet(List(numOfHorizontalLines) {
            val x = lineWidth / 2f + it * (lineWidth + spacing)
            val origin = PVector(x, 0f)
            val destination = PVector(x, heightF)
            val delayDelta = 1
            Line(
                origin, easeOut = Ease::cubicOut,
                destination, easeIn = Ease::cubicIn,
                color = getColor(it, 0, numOfHorizontalLines, colors.sunrise),
                animationLength = 60 * 2,
                delayFrames = abs((numOfHorizontalLines - it) * delayDelta),
                shouldLoop = false,
            )
        })
    }

    private val allSets by lazy { listOf(bottomUp, topDown, leftRight, rightLeft) }

    override fun setup() {
        super.setup()
        if (recordingEnabled) videoExport.startMovie()
    }

    override fun draw() {
        background(grey3)

        allSets.forEach {
            it.update()
        }

        videoExport.saveFrame()
    }

    override fun keyPressed() {
        when(key) {
            '5' -> topDown.activate()
            '2' -> bottomUp.activate()
            '1' -> leftRight.activate()
            '3' -> rightLeft.activate()
        }
    }

    private fun getColor(i: Int, min: Int, max: Int, colors: List<Int>): Int {
        val amt = map(i.toFloat(), min.toFloat(), max.toFloat(), 0f, 1f)
        return lerpColors(amt, *colors.toIntArray())
    }

    private inner class LineSet(val lines: List<Line>) {

        fun activate() {
            lines.forEach { it.isActive = true }
        }

        fun update() {
            lines.forEach {
                it.update()
                it.draw()
            }
        }
    }

    private inner class Line(
        val origin: PVector,
        easeOut: (Float) -> Float,
        destination: PVector,
        easeIn: (Float) -> Float,
        val color: Int,
        val animationLength: Int = 60 * 1,
        val delayFrames: Int = 0,
        val shouldLoop: Boolean = false,
    ) {

        private val step = 1f / animationLength

        var isActive = false
        private var start = origin
        private var end = start
        private var progressIndex = 0
        private var currentDelayFrames = delayFrames

        private val direction = destination - start
        private val startPath = generatePath(easeOut)
        private val endPath = generatePath(easeIn)

        private fun generatePath(easingFn: (Float) -> Float): List<PVector> {
            val path = mutableListOf<PVector>()
            var p = 0f
            while (p < 1f) {
                val point = origin + direction * easingFn(p)
                path.add(point)
                p += step
            }
            return path
        }

        fun update() {
            if (isActive) {
                if (!hasDelayFinished()) {
                    currentDelayFrames--
                } else {
                    start = startPath[progressIndex]
                    end = endPath[progressIndex]
                    updateProgressIndex()
                    deactivateIfNeeded()
                }
            }
        }

        private fun updateProgressIndex() {
            progressIndex = if (shouldLoop) {
                (progressIndex + 1) % startPath.size
            } else {
                constrain(progressIndex + 1, 0, startPath.lastIndex)
            }
        }

        private fun deactivateIfNeeded() {
            if (!shouldLoop && progressIndex >= startPath.lastIndex) {
                isActive = false
                currentDelayFrames = delayFrames
                progressIndex = 0
                start = origin
                end = start
            }
        }

        fun draw() {
            if (!isActive || !hasDelayFinished()) return
            strokeWeight(lineWidth)
            stroke(color)
            line(start, end)
        }

        private fun hasDelayFinished() = currentDelayFrames <= 0
    }
}