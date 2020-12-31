package letters

import BaseSketch
import util.Grid
import util.RollingGrid
import kotlin.random.Random


class MatrixLetters : BaseSketch() {

    private val strokeColor = grey9
    private val fillColor = grey5
    private val fillColor2 = grey9
    private val backgroundColor = grey3

    private lateinit var grid: RollingGrid<Letter>
//    private val letters: List<Letter> = List(600) { Letter() }
    private val letterSize: Float = 20F

    override fun setup() {
//        smooth()
        frameRate(20F)
//        fill(fillColor)

        background(backgroundColor)
        textSize(letterSize)
//       TODO textFont
        textAlign(LEFT, TOP)

        grid = RollingGrid(screen, 20F, Grid.Orientation.VERTICAL)
        grid.initItems { n, x, y ->
            Letter(x, y)
        }
    }

    override fun draw() {
        background(backgroundColor)
        grid.updateAndDraw()
    }

    inner class Letter(
        private var x: Float = random(0F, screen.widthF),
        private var y: Float = random(0F, screen.heightF)
    ): Grid.ChangingItem() {

        private val minAlpha = 160
        private val maxAlpha = 300 // 255
        private val alphaDecay = 5F
        private var alpha: Float = Random.nextInt(maxAlpha).toFloat()
        var letter = ' '

        override fun update() {
            if (alpha > minAlpha) {
                alpha -= alphaDecay
            }
        }

        override fun refresh() {
            letter = Random.nextInt(48, 129).toChar() // alphanumeric and extra symbols
            alpha = maxAlpha.toFloat()
        }

        override fun draw() {
            noStroke()
            fill(fillColor2, alpha)
            text(letter, x, y)
        }
    }
}


