package sketch.letters

import BaseSketch
import com.hamoid.VideoExport
import util.Grid
import util.isOdd
import kotlin.random.Random


class TextFromRandom : BaseSketch() {

    private val fillColor = grey9
    private val fillColor2 = yellow
    private val backgroundColor = grey3

    // config
    private val text = "The night is young. The time is now!"
    private val speedUpChance = 0.0f // 0.0 - 0.08
    private val letterSize: Float = 20F

    private val itemMargin: Float = letterSize/10f
    private lateinit var grid: Grid<Letter>
    private var textStartIndex = 0

    private val videoExport: VideoExport by lazy {
        VideoExport(this).apply {
            setFrameRate(60f)
        }
    }

    override fun setup() {
        frameRate(60F)
//        videoExport.startMovie() // record

        background(backgroundColor)
        textSize(letterSize)
//       TODO textFont
        textAlign(LEFT, TOP)

        grid = Grid(screen, letterSize + itemMargin, Grid.Orientation.VERTICAL)
        val midGridIndex = if (grid.numOfRows.isOdd()) grid.size / 2 else grid.size / 2 - grid.numOfCols / 2
        textStartIndex = midGridIndex - text.length / 2 // centered
        grid.initItems { n, x, y ->
            Letter(n, x, y)
        }
    }

    override fun draw() {
        background(backgroundColor)
        grid.updateAndDraw()
        videoExport.saveFrame()
    }

    private inner class Letter(
        val index: Int,
        private val x: Float = random(0F, screen.widthF),
        private val y: Float = random(0F, screen.heightF)
    ) : Grid.SimpleItem() {

        private val alphaRange = 0..255 step 5
        private val indexWithinText = index - textStartIndex

        private var alpha: Float = Random.nextInt(alphaRange.last).toFloat()
        private var letter = ' '
        private var endState: Boolean = false

        override fun update() {
            updateEndState()
            updateAlphaOrRefresh()
        }

        private fun updateEndState() {
            if (!endState && isWithinText()) {
                endState = text[indexWithinText].equals(letter, ignoreCase = true)
            }
        }

        private fun updateAlphaOrRefresh() {
            if (alpha > alphaRange.first) {
                alpha -= alphaRange.step
            } else {
                refresh()
            }
        }

        private fun refresh() {
            if (!endState) {
                letter = newLetter()
            }
            alpha = alphaRange.last.toFloat()
        }

        private fun newLetter() = if (isWithinText() && Random.nextFloat() < speedUpChance) {
            text[indexWithinText].apply { if (Random.nextBoolean()) toUpperCase() }
        } else {
            Random.nextInt(48, 129).toChar() // alphanumeric and extra symbols
        }

        private fun isWithinText() = index in textStartIndex until textStartIndex + text.length

        override fun draw() {
            noStroke()
            if (endState) {
                fill(fillColor2, alpha)
            } else {
                fill(fillColor, alpha)
            }
            text(letter, x, y)
        }
    }
}


