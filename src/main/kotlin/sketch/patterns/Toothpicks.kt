package sketch.patterns

import BaseSketch
import Screen

class Toothpicks : BaseSketch(Screen(900, 900)) {

    private val size: Float = 100F // 100F

    private var allPicks = mutableListOf<Toothpick>()
    private val nextPicks = mutableListOf<Toothpick>()
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
        strokeWeight(3F)

        allPicks.addAll(nextPicks)
        nextPicks.clear()
        for (pick in allPicks) {
            if (pick.isNew) {
                pick.isNew = false
                stroke(orange)
                pick.show()
                pick.generateNext(allPicks, ::addToNext)
                updateMinMax(pick)
            } else {
                stroke(grey11)
                pick.show()
            }
        }

        // NEGATIVE
        if (allPicks.size > 100) {
            allPicks = allPicks.drop(allPicks.size/110).toMutableList()
        }
    }

    private fun addToNext(it: Toothpick): Boolean = nextPicks.add(it)

    private fun setScale() {
        val scaleFactor = width / (maxX - minX)
        scale(scaleFactor)
    }

    private fun updateMinMax(pick: Toothpick) {
        minX = min(pick.ax, minX)
        maxX = max(pick.ax, maxX)
    }
}

class Toothpick(private val applet: Toothpicks, x: Float, y: Float, val size: Float, val direction: Direction) {

    val ax: Float
    private val ay: Float
    private val bx: Float
    private val by: Float
    var isNew = true

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

    fun generateNext(otherNew: List<Toothpick>, addNew: (Toothpick) -> Boolean) {
        var aAvailable = true
        var bAvailable = true
        for (pick in otherNew) {
            if (aAvailable && pick != this && pick.touches(ax, ay, direction)) {
                aAvailable = false
            }
            if (bAvailable && pick != this && pick.touches(bx, by, direction)) {
                bAvailable = false
            }
        }
        if (aAvailable) addNew(newPerpendicular(ax, ay))
        if (bAvailable) addNew(newPerpendicular(bx, by))
    }

    private fun touches(x: Float, y: Float, direction: Direction): Boolean {
        return this.direction == direction && ((ax == x && ay == y) || (bx == x && by == y))
    }

    private fun newPerpendicular(x: Float, y: Float): Toothpick {
        return Toothpick(applet, x, y, size, direction.perpendicular())
    }

    fun show() {
        applet.line(ax, ay, bx, by)
    }

    enum class Direction {
        HORIZONTAL,
        VERTICAL;

        fun perpendicular(): Direction {
            return when (this) {
                HORIZONTAL -> VERTICAL
                VERTICAL -> HORIZONTAL
            }
        }
    }
}