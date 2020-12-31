package walkers

import BaseSketch
import kotlin.random.Random

class PixelatedWalkers : BaseSketch() {

    private val cell: Float = 5F

    override fun setup() {
        background(grey1)
    }

    private val walkers: MutableList<Walker> = mutableListOf()

    override fun draw() {
        for (walker in walkers) {
            walker.run {
                if(!isOut()) {
                    move()
                    draw()
                }
            }
        }
    }

    override fun mouseClicked() {
        walkers.add(Walker(mouseXF, mouseYF))
    }

    inner class Walker(var x: Float, var y: Float) {

        init {
            draw()
        }

        fun draw() {
            fill(transparentLight)
            noStroke()
            /** Shape **/
            rect(x, y, cell, cell)
//            ellipse(x, y, cell, cell)
            /** end shape **/
        }

        fun move() {
            val direction = Random.nextFloat()
            when (direction) {
                in 0F..0.25F -> y -= cell // Go up
                in 0.25F..0.5F -> x += cell // Go right
                in 0.5F..0.75F -> y += cell // Go down
                in 0.75F..1F -> x -= cell // Go left
            }
        }

        fun isOut(): Boolean {
            return x < 0 || x > width || y < 0 || y > height
        }
    }
}