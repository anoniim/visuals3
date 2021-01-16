package lines

import BaseSketch
import Screen

class CircleTangents : BaseSketch(Screen(800, 800)) {

    private val minSpeed = 10f // 10
    private val maxSpeed = 70f // 70
    private val startScale = 200 // 200
    private val endScale = 1.2f // 1-5 (4)
    private val angleStep = 0.1f // 0.1
    private val zoomOutLengthFrames = 500f // 700
    private val zoomInDelayFrames = 200f // 200
    private val fullOpaque = 250f // 250
    private val defaultStrokeProgress = 1.3f  // 1.3

    private val lines = mutableListOf<Line>()
    private val lineLength = 0.7f * screen.height
    private var revealingLineIndex = 0
    private var flashingLineIndex = Int.MAX_VALUE
    private var fadingLineIndex = -1
    private var globalStrokeProgress = fullOpaque
    private var globalFadingProgress = defaultStrokeProgress

    override fun setup() {
        var angle = 0f
        while (angle < TWO_PI) {
            val x = cos(angle) * startScale + screen.centerX
            val y = sin(angle) * startScale + screen.centerY
            lines.add(Line(x, y, angle))
            angle += angleStep
        }
        reshuffleLines()
    }

    override fun draw() {
        background(grey3)

        for (line in lines) {
            line.update()
            line.show()
        }
        if (flashingLineIndex < 0) {
            flashingLineIndex++
        }
    }


    inner class Line(val x: Float, val y: Float, val angle: Float) {

        var index: Int = 0
        private val strokeColor by lazy { if (index % 2 == 0) yellow else purple }

        private var revealProgress = -lineLength
        private val revealStep // progressive speed
            get() = map(revealingLineIndex.toFloat(), 0f, lines.lastIndex.toFloat(), minSpeed, maxSpeed)

        private var strokeWeight = 1f // 1
        private val strokeStep = 0.05f // 0.05
        private var strokeProgress = defaultStrokeProgress

        private var fadingProgress = fullOpaque // 250
        private val fadingStep = 10f // 10

        fun update() {
            updateReveal()
            updateFlashing()
            updateFading()
        }

        private fun updateReveal() {
            if (isBeingRevealed()) {
                if (!hasFinishedRevealing()) {
                    revealProgress += revealStep
                } else {
                    revealingLineIndex++
                    if (revealingLineIndex > lines.lastIndex) {
                        flashingLineIndex = -zoomOutLengthFrames.toInt() // Start flashing/fading
                    }
                }
            }
        }

        private fun updateFlashing() {
            if (isFlashing()) {
                if (!hasFinishedFlashing()) {
                    strokeWeight += cos(strokeProgress)
                    strokeProgress += strokeStep
                    globalStrokeProgress = calculateGlobalStrokeProgress(strokeProgress)
                } else {
                    fadingLineIndex = index
                }
            }
        }

        private fun updateFading() {
            if (isFlashing() && isFading()) {
                if (!hasFinishedFading()) {
                    fadingProgress -= fadingStep
                    globalFadingProgress = fadingProgress
                } else {
                    reset()
                    flashingLineIndex++
                    globalFadingProgress = fullOpaque
                    globalStrokeProgress = defaultStrokeProgress
                    if (flashingLineIndex > lines.lastIndex) {
                        // Reset to the beginning
                        flashingLineIndex = Int.MAX_VALUE
                        revealingLineIndex = 0
                        reshuffleLines()
                    }
                }
            }
        }

        private fun reset() {
            revealProgress = -lineLength
            strokeWeight = 1f
            strokeProgress = defaultStrokeProgress
            fadingProgress = fullOpaque
        }


        fun show() {
            strokeWeight(strokeWeight)
            stroke(strokeColor, fadingProgress)
            pushMatrix()
            val (scaledX, scaledY) = scaleXY()
            translate(scaledX, scaledY)
            rotate(angle)
            line(0f, -lineLength, 0f, revealProgress)
            popMatrix()
        }

        private fun scaleXY(): Pair<Float, Float> {
            return if (flashingLineIndex < 0f) {
                zoomOut()
            } else if (flashingLineIndex >= 0f && flashingLineIndex < Int.MAX_VALUE) {
                zoomBackIn()
            } else {
                return Pair(x, y)
            }
        }

        private fun zoomOut(): Pair<Float, Float> {
            val newScale =
                constrain(map(flashingLineIndex.toFloat(), -zoomOutLengthFrames, -zoomInDelayFrames, 1f, endScale), 1f, endScale)
            val scaledX = (x - screen.centerX) / newScale + screen.centerX
            val scaledY = (y - screen.centerY) / newScale + screen.centerY
            return Pair(scaledX, scaledY)
        }

        private fun zoomBackIn(): Pair<Float, Float> {
            val newScale = map(
                (lines.lastIndex - flashingLineIndex) * 2 * fullOpaque + globalFadingProgress + globalStrokeProgress,
                (lines.lastIndex + 1) * 2 * fullOpaque, 0f,
                endScale, 1f
            )
            val scaledX = (x - screen.centerX) / newScale + screen.centerX
            val scaledY = (y - screen.centerY) / newScale + screen.centerY
            return Pair(scaledX, scaledY)
        }

        private fun isBeingRevealed() = index == revealingLineIndex
        private fun hasFinishedRevealing() = revealProgress > lineLength

        private fun isFlashing() = index == flashingLineIndex
        private fun hasFinishedFlashing() = strokeWeight < 1f

        private fun isFading() = index == fadingLineIndex
        private fun hasFinishedFading() = fadingProgress < 0f

        private fun calculateGlobalStrokeProgress(strokeProgress: Float) =
            map(strokeProgress, defaultStrokeProgress, 1.89999f, 250f, 0f)
    }

    private fun reshuffleLines() {
        lines.shuffle()
        for (line in lines.withIndex()) {
            line.value.index = line.index
        }
    }
}