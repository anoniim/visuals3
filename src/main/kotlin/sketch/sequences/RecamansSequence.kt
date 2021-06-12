package sketch.sequences

import BaseSketch
import Screen

class RecamansSequence : BaseSketch(
    Screen(1600, 900),
    fullscreen = true
) {

    private val seen = mutableListOf(0)
    private val arcs = mutableListOf<Arc>()
    private var current = 0 // 0
    private var sequenceStep = 1 // 1
    private var max = 20f // 20f
    private var scaleBuffer = 20f
    private var scaleStep = 0f
    private val scaleFactor: Float
        get() = screen.widthF / (max + scaleBuffer)
    private var animationStep = 0.005f // 0.005f

    override fun setup() {
//        noLoop()
//        frameRate(20f)
    }

    override fun mousePressed() {
        redraw()
    }

    override fun draw() {
        translate(0f, screen.centerY)
        scale(scaleFactor)
        val isArcFinished = showArcs()
        if (isArcFinished) {
            current = nextStep()
        }
        recalculateScaleFactor(current)
    }

    private fun showArcs(): Boolean {
        stroke(grey9)
        noFill()
        background(grey3)
        strokeWeight(2 / scaleFactor)
        for (arc in arcs.withIndex()) {
            arc.value.update()
            if (arc.index != arcs.lastIndex) {
                arc.value.show()
            } else {
                return arc.value.show()
            }
        }
        return true
    }

    private fun nextStep(): Int {
        val next = findNext()
        arcs.add(Arc(current, next, sequenceStep))
        sequenceStep++
        return next
    }

    private fun findNext(): Int {
        var next = current - sequenceStep
        if (next < 0 || seen.contains(next)) {
            next = current + sequenceStep
        }
        return next.also { seen.add(it) }
    }

    private fun recalculateScaleFactor(next: Int) {
        if (next.toFloat() > (max + scaleBuffer)) {
            scaleStep = (next - max) * animationStep * 2
        }
        if (scaleStep > 0.01) {
            max += scaleStep
            scaleStep *= 0.9f
        }
    }

    private inner class Arc(
        val start: Float,
        val end: Float,
        val isUp: Boolean,
        val isLeftToRight: Boolean
    ) {

        constructor(start: Int, end: Int, sequenceStep: Int) :
                this(
                    start.toFloat(), end.toFloat(),
                    isUp = sequenceStep % 2 == 1,
                    isLeftToRight = start < end
                )

        private var progress = 0f
        private var complete = 1f

        fun show(): Boolean {
            val diameter = abs(end - start)
            val section: Pair<Float, Float> = calculateSection()
            arc((start + end) / 2, 0f, diameter, diameter, section.first, section.second)
            return this.progress >= complete
        }

        private fun calculateSection(): Pair<Float, Float> {
            return if (isUp && isLeftToRight) {
                Pair(-PI, PI * (progress - 1))
            } else if (isUp && !isLeftToRight) {
                Pair(-PI * (progress), 0f)
            } else if (!isUp && isLeftToRight) {
                Pair(PI * (1 - progress), PI)
            } else {
                Pair(0f, PI * (progress))
            }
        }

        fun update() {
            if (progress < complete) {
                progress += animationStep
            }
        }
    }
}

