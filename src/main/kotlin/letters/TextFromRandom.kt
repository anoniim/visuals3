package letters

import BaseSketch
import util.Grid
import kotlin.random.Random


class TextFromRandom : BaseSketch() {

    private val fillColor = grey9
    private val fillColor2 = yellow
    private val backgroundColor = grey3

    private lateinit var grid: Grid<Letter>
    private val letterSize: Float = 20F
    val text = "The night is young. The time is now!"
    val startIndex = 1700

    override fun setup() {
        frameRate(120F)

        background(backgroundColor)
        textSize(letterSize)
//       TODO textFont
        textAlign(LEFT, TOP)

        grid = Grid(screen, 20F, Grid.Orientation.VERTICAL)
        grid.initItems { n, x, y ->
            Letter(n, x, y)
        }
    }

    override fun draw() {
        background(backgroundColor)
        grid.updateAndDraw()
    }

    inner class Letter(
        val index: Int,
        private val x: Float = random(0F, screen.widthF),
        private val y: Float = random(0F, screen.heightF)
    ): Grid.SimpleItem() {

        private val minAlpha = 0
        private val maxAlpha = 255 // 255
        private val alphaDecay = 5F
        private var alpha: Float = Random.nextInt(maxAlpha).toFloat()
        var letter = ' '

        private var endState: Boolean = false

        override fun update() {
            if (!endState && index in startIndex until startIndex + text.length) {
                val indexWithinText = index - startIndex
                endState = text[indexWithinText].equals(letter, ignoreCase = true)
            }
            if (alpha > minAlpha) {
                alpha -= alphaDecay
            } else {
                refresh()
            }
        }

        fun refresh() {
            if (!endState) {
                letter = Random.nextInt(48, 129).toChar() // alphanumeric and extra symbols
            }
            alpha = maxAlpha.toFloat()
        }

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


