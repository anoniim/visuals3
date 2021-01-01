package patterns

import BaseSketch
import Screen
import processing.core.PApplet

class Toothpicks : BaseSketch(Screen(900, 900)) {

    val size: Float = 100F

    private val nextPicks = mutableListOf<Toothpick>()
    private val allPicks = mutableListOf<Toothpick>()
    private var minX = -screen.widthF
    private var maxX = screen.widthF

    override fun setup() {
        val first = Toothpick(this, 0F, 0F, size, Toothpick.Direction.VERTICAL)
        nextPicks.add(first)
        frameRate(8F)
//        noLoop()
    }

    override fun mousePressed() {
        redraw()
    }

    override fun draw() {
        translate(screen.centerX, screen.centerY)
        setScale()
        background(grey3)
        stroke(grey11)
        strokeWeight(3F)

        allPicks.addAll(nextPicks)
        for (pick in allPicks) {
            pick.show()
        }

        stroke(orange)
        val next = mutableListOf<Toothpick>()
        for (pick in nextPicks) {
            var new = pick.getNextForA(allPicks)
            if (new != null) {
                next.add(new)
            }
            new = pick.getNextForB(allPicks)
            if (new != null) {
                next.add(new)
            }
            updateMinMax(pick)
            pick.show()
        }
        nextPicks.clear()
        nextPicks.addAll(next)
    }

    private fun setScale() {
        val scaleFactor = width / (maxX - minX)
        scale(scaleFactor)
    }

    private fun updateMinMax(pick: Toothpick) {
        minX = min(pick.ax, minX)
        maxX = max(pick.ax, maxX)
    }
}

class Toothpick(private val applet: PApplet, x: Float, y: Float, val size: Float, val direction: Direction) {

    val ax: Float
    private val ay: Float
    private val bx: Float
    private val by: Float

    init {
        val halfSize = size / 2
        when (direction) {
            Direction.HORIZONTAL -> {
                ax = x - halfSize
                bx = x + halfSize
                ay = y
                by = y
            }
            Direction.VERTICAL -> {
                ax = x
                bx = x
                ay = y - halfSize
                by = y + halfSize
            }
        }
    }

    fun getNextForA(otherNew: List<Toothpick>): Toothpick? {
        return getNext(ax, ay, otherNew)
    }

    fun getNextForB(otherNew: List<Toothpick>): Toothpick? {
        return getNext(bx, by, otherNew)
    }

    private fun getNext(x: Float, y: Float, otherNew: List<Toothpick>): Toothpick? {
        var available = true
        for (pick in otherNew) {
            if (pick != this && pick.touches(x, y)) {
                available = false
                // TODO break
            }
        }
        return if(available) newPerpendicular(x, y) else null
    }

    private fun newPerpendicular(x: Float, y: Float): Toothpick {
        return Toothpick(applet, x, y, size, direction.perpendicular())
    }

    private fun touches(x: Float, y: Float): Boolean {
        return (ax == x && ay == y) || (bx == x && by == y)
    }

    fun show() {
        applet.line(ax, ay, bx, by)
    }

    enum class Direction {
        HORIZONTAL,
        VERTICAL;

        fun perpendicular(): Direction {
            return when(this) {
                HORIZONTAL -> VERTICAL
                VERTICAL -> HORIZONTAL
            }
        }
    }
}