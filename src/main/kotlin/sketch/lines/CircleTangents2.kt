package sketch.lines

import BaseSketch
import Screen
import com.hamoid.VideoExport
import util.Interpolation

/**
 * What seems straight, simple and obvious at the beginning may later turn out to be something more complex and quite different.
 * Don't despair.
 * Take a step back, see the big picture, decompose the problem and solve it bit by bit.
 * You'll often find that there is a straight path to the solution after all.
 */
class CircleTangents2 : BaseSketch(
    Screen(800, 800),
    fullscreen = true    ,
) {

    companion object {

        private var globalState: GlobalState = GlobalState.Revealing
    }

    private val minSpeed = 10f // 10
    private val maxSpeed = 260f // 70
    private val startScale = 200 // 200
    private val endScale = 2.5f // 1-5 (4)
    private val fullOpaque = 250f // 250
    private val defaultStrokeProgress = 1.3f  // 1.3
    private val zoomOutLengthFrames = 400f
    private val zoomOutInterpolation = Interpolation(zoomOutLengthFrames)
    private val zoomInDelayFrames = 60f

    private var revealingLineIndex = 0
    private var globalScale = 1f

    private val maxLineLength = 0.7f * screen.height
    private val numOfLines = 64
    private val angle = TWO_PI / numOfLines
    private val lines = composeLineList()
    private val numOfSpiralLines = 85
    private val spiralLines = composeSpiralLineList()

    private val videoExport: VideoExport by lazy {
        VideoExport(this).apply {
            setFrameRate(60f)
        }
    }

    override fun setup() {
        lines.first().reveal()
//        videoExport.startMovie() // record
    }

    override fun draw() {
        background(grey3)

        when (val currentGlobalState = globalState) {
            is GlobalState.ZoomOut -> updateZoomOutState(currentGlobalState)
            is GlobalState.ZoomIn -> updateZoomInState(currentGlobalState)
            is GlobalState.SpiralIn -> updateSpiralInState(currentGlobalState)
        }

        lines.forEach {
            it.update()
            it.show()
        }
        videoExport.saveFrame()
    }

    private fun updateZoomOutState(currentGlobalState: GlobalState.ZoomOut) {
        currentGlobalState.update()
        globalScale = zoomOutInterpolation.interpolate(1f, endScale, currentGlobalState.zoomProgress)
        if (hasZoomFinished(currentGlobalState)) {
            lines.sortBy { it.index }
            globalState = GlobalState.ZoomIn
        }
    }

    private fun hasZoomFinished(currentGlobalState: GlobalState.ZoomOut) =
        currentGlobalState.zoomProgress > zoomOutLengthFrames

    private fun updateZoomInState(currentGlobalState: GlobalState.ZoomIn) {
        currentGlobalState.update()
        if (hasPauseFinished(currentGlobalState)) {
            flashAndFadeNextLine(currentGlobalState)
        }
    }

    private fun hasPauseFinished(currentGlobalState: GlobalState.ZoomIn): Boolean {
        return currentGlobalState.zoomProgress > zoomInDelayFrames
    }

    private fun flashAndFadeNextLine(currentGlobalState: GlobalState.ZoomIn) {
        val progressAfterDelay = currentGlobalState.zoomProgress - zoomInDelayFrames
        if (progressAfterDelay > 0) {
            val flashingLineIndex = floor(progressAfterDelay / 10)
            if (flashingLineIndex <= lines.lastIndex) {
                lines[flashingLineIndex].flashAndFade()
            }
            if (flashingLineIndex == lines.lastIndex - 1) {
                lines[flashingLineIndex + 1].flashAndFade()
                globalState = GlobalState.SpiralIn(currentGlobalState.zoomProgress)
            }
        }
    }

    private fun updateSpiralInState(currentGlobalState: GlobalState.SpiralIn) {
        currentGlobalState.update()
        showNextSpiralLine(currentGlobalState)
        spiralLines.forEach {
            it.update()
            it.show()
        }
    }

    private fun showNextSpiralLine(currentGlobalState: GlobalState.SpiralIn) {
        val spiralLineIndex = floor(currentGlobalState.spiralProgress / 10)
        if (spiralLineIndex <= spiralLines.lastIndex) {
            spiralLines[spiralLineIndex].flashAndFade()
        }
    }

    private fun revealNextLine() {
        if (revealingLineIndex == 3) {
            Thread.sleep(1000) // TODO Thread.sleep is not recorded
        }
        revealingLineIndex++
        if (revealingLineIndex < lines.lastIndex) {
            lines[revealingLineIndex].reveal(getProgressiveRevealSpeed())
        } else {
            Thread.sleep(300) // TODO Thread.sleep is not recorded
            globalState = GlobalState.ZoomOut
        }
    }

    private fun getProgressiveRevealSpeed() =
        map(revealingLineIndex.toFloat(), 0f, lines.lastIndex.toFloat(), minSpeed, maxSpeed)

    private fun composeSpiralLineList(): List<Line> {
        var scale = startScale.toFloat()
        return List(numOfSpiralLines) {
            val theta = it * angle
            val x = scale * cos(theta) + screen.centerX
            val y = scale * sin(theta) + screen.centerY
            scale -= 2.8f
            Line(x, y, theta, it, State.SPIRAL, getSpiralStrokeColor(it))
        }
    }

    private fun composeLineList(): MutableList<Line> {
        val originalList = MutableList(numOfLines + 1) {
            val theta = it * angle
            val x = startScale * cos(theta) + screen.centerX
            val y = startScale * sin(theta) + screen.centerY
            Line(x, y, theta, it, strokeColor = getStrokeColor(it))
        }
        return reorderLines(originalList)
    }

    private fun getStrokeColor(index: Int): Int {
        return if (index % 2 == 0) yellow else purple
    }

    private fun getSpiralStrokeColor(index: Int): Int {
        val colorsCount = colors.yellowToPurple.size
        val colorsWithinSegment = numOfSpiralLines / (colorsCount - 1)
        val colorIndexWithinSegment = index % colorsWithinSegment
        val maxIndexWithinSegment = colorsWithinSegment - 1f
        val startColorIndex = index / colorsWithinSegment
        val startColor = colors.yellowToPurple[startColorIndex]
        val endColorIndex = startColorIndex + 1
        val endColor = if (endColorIndex <= colors.yellowToPurple.lastIndex) colors.yellowToPurple[endColorIndex]
        else purple
        val amt = map(colorIndexWithinSegment.toFloat(), 0f, maxIndexWithinSegment, 0f, 1f)
        return lerpColor(startColor, endColor, amt)
    }

    private fun reorderLines(originalList: MutableList<Line>): MutableList<Line> {
        val middleIndex = originalList.lastIndex / 2
        val quarterIndex = originalList.lastIndex / 4
        val threeQuartersIndex = 3 * quarterIndex
        return mutableListOf<Line>().apply {
            addAll(
                listOf(
                    originalList[0],
                    originalList[middleIndex],
                    originalList[quarterIndex],
                    originalList[threeQuartersIndex],
                    originalList[quarterIndex - 2],
                    originalList[quarterIndex + 6],
                    originalList[quarterIndex - 4],
                    originalList[middleIndex - 4],
                    originalList[middleIndex - 8],
                    originalList[middleIndex + 6],
                )
            )
            originalList.run {
                removeAt(0)
                // -1 because the first one has been removed
                removeAt(threeQuartersIndex - 1)
                removeAt(middleIndex + 6 - 1)
                removeAt(middleIndex - 1)
                removeAt(middleIndex - 4 - 1)
                removeAt(middleIndex - 8 - 1)
                removeAt(quarterIndex + 6 - 1)
                removeAt(quarterIndex - 1)
                removeAt(quarterIndex - 2 - 1)
                removeAt(quarterIndex - 4 - 1)
            }
            addAll(originalList.shuffled())
        }
    }

    private inner class Line(
        private val x: Float,
        private val y: Float,
        private val angle: Float,
        var index: Int = 0,
        private var state: State = State.DEFAULT,
        private val strokeColor: Int
    ) {

        private var revealSpeed: Float = minSpeed
        private var revealProgress: Float = if (state == State.SPIRAL) maxLineLength else -maxLineLength

        private var strokeWeight = 1f // 1
        private val strokeStep = 0.05f // 0.05
        private var strokeProgress = defaultStrokeProgress

        private var fadingProgress = if (state == State.SPIRAL) 0f else fullOpaque
        private val fadingStep = if (state == State.SPIRAL) 1f else 10f

        fun reveal(revealSpeed: Float = minSpeed) {
            this.revealSpeed = revealSpeed
            state = State.REVEALING
        }

        fun flashAndFade() {
            if (state != State.FLASHING) {
                state = State.FLASHING
            }
        }

        fun update() {
            when (state) {
                State.REVEALING -> updateReveal()
                State.FLASHING -> updateFlashing()
                State.FADING -> updateFading()
                else -> { /* do not update */
                }
            }
        }

        private fun updateReveal() {
            if (!hasFinishedRevealing()) {
                revealProgress += revealSpeed
            } else {
                state = State.DEFAULT
                revealNextLine()
            }
        }

        private fun updateFlashing() {
            if (!hasFinishedFlashing()) {
                strokeWeight += cos(strokeProgress)
                strokeProgress += strokeStep
                if (fadingProgress < fullOpaque) {
                    fadingProgress += 20 * fadingStep
                }
            } else {
                state = State.FADING
            }
        }

        private fun updateFading() {
            if (!hasFinishedFading()) {
                fadingProgress -= fadingStep
            } else {
                reset()
            }
        }

        private fun reset() {
            revealProgress = -maxLineLength
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
            line(0f, -maxLineLength, 0f, revealProgress)
            popMatrix()
        }

        private fun scaleXY(): Pair<Float, Float> {
            return when (val currentGlobalState = globalState) {
                is GlobalState.ZoomOut -> getScaled(globalScale)
                is GlobalState.ZoomIn -> zoomBackIn(currentGlobalState)
                is GlobalState.SpiralIn -> spiralIn(currentGlobalState)
                else -> Pair(x, y)
            }
        }

        private fun zoomBackIn(currentGlobalState: GlobalState.ZoomIn): Pair<Float, Float> {
            if (currentGlobalState.zoomProgress < zoomInDelayFrames) return getScaled(endScale)
            val newScale = map(currentGlobalState.zoomProgress, zoomInDelayFrames, 900f, endScale, 1f)
            return getScaled(newScale)
        }

        private fun spiralIn(currentGlobalState: GlobalState.SpiralIn): Pair<Float, Float> {
            val newScale = map(currentGlobalState.zoomProgress, zoomInDelayFrames, 900f, endScale, 1f)
            return getScaled(newScale)
        }

        private fun getScaled(newScale: Float): Pair<Float, Float> {
            val scaledX = (x - screen.centerX) / newScale + screen.centerX
            val scaledY = (y - screen.centerY) / newScale + screen.centerY
            return Pair(scaledX, scaledY)
        }

        private fun hasFinishedRevealing() = revealProgress > maxLineLength
        private fun hasFinishedFlashing() = strokeWeight < 1f
        private fun hasFinishedFading() = fadingProgress < 0f
    }

}

private enum class State {
    DEFAULT,
    REVEALING,
    FLASHING,
    FADING,
    SPIRAL,
}

private sealed class GlobalState {
    object Revealing : GlobalState()
    object ZoomOut : GlobalState() {
        var zoomProgress = 0f
        fun update() {
            zoomProgress++
        }
    }

    object ZoomIn : GlobalState() {
        var zoomProgress = 0f
        fun update() {
            zoomProgress++
        }
    }

    class SpiralIn(var zoomProgress: Float) : GlobalState() {
        var spiralProgress = 0f
        fun update() {
//                zoomProgress += ( spiralProgress) / 300f
            spiralProgress++
        }
    }
}