package sketch.patterns

import BaseSketch
import processing.core.PVector
import util.point
import util.vertex

class VoronoiTessellation : BaseSketch(
    longClickClear = true
) {

    // config
    private val numOfPoints = 19
    private val speed = 2f
    private val regionDensity = 1000

    private lateinit var allRegions: List<Region>

    override fun setup() {
//        frameRate(10f)
        background(grey3)
        allRegions = generateRegions()
    }

    override fun draw() {
//        background(grey3)
        allRegions.forEach {
            it.grow()
            it.draw()
//            it.drawBorder()
//            it.drawOrigin()
        }
        drawLongPressOverlay()
    }

    override fun reset() {
        super.reset()
        allRegions = generateRegions()
    }

    private fun generateRegions() = List(numOfPoints) {
//        val theta = TWO_PI / numOfPoints
        val theta = TWO_PI / 5
        val angle = it * theta
        val x = halfWidthF + sin(it/2f) * 350f * cos(angle)
        val y = halfHeightF + cos(it/3f) * 350f * sin(angle)
        Region(PVector(x, y), colors.random)
    }

    private inner class Region(val origin: PVector, val color: Int) {

        private val otherRegions by lazy { allRegions.filter { it != this } }
        private val points = List(regionDensity + 1) { // +1 to close the shape
            val theta = TWO_PI / regionDensity
            Point(this, it * theta)
        }
        private val border = mutableMapOf<PVector, MutableSet<PVector>>()

        private var radius: Float = 10f

        fun grow() {
            radius += speed
        }

        fun draw() {
            fill(color)
            noStroke()
            beginShape()
            points.forEach { point ->
                if (!point.includedInBorder) {
                    val borderingRegionOrigin = getBorderingRegion(point)?.origin
                    if (borderingRegionOrigin != null) {
                        includeInBorder(borderingRegionOrigin, point)
                    } else {
                        point.setNewRadius(radius)
                    }
                }
                vertex(point.location)
            }
            endShape()
        }

        private fun getBorderingRegion(point: Point): Region? {
            var borderingRegion: Region? = null
            otherRegions.forEach { otherRegion ->
                if (point.distFrom(otherRegion.origin) <= point.distFromHomeOrigin) {
                    borderingRegion = otherRegion
                }
            }
            return borderingRegion
        }

        private fun includeInBorder(
            otherRegionOrigin: PVector,
            point: Point
        ) {
            val currentBorder = border[otherRegionOrigin]
            val borderWithOtherRegion: MutableSet<PVector> = if (!currentBorder.isNullOrEmpty())
                currentBorder else mutableSetOf()
            borderWithOtherRegion.add(point.location)
            point.includedInBorder = true
            border[otherRegionOrigin] = borderWithOtherRegion
        }

        fun drawBorder() {
            strokeWeight(1f)
            stroke(grey3)
            noFill()
            border.forEach { (_, path) ->
                beginShape()
                path.forEach {
                    vertex(it)
                }
                endShape()
            }

        }

        fun drawOrigin() {
            strokeWeight(5f)
            stroke(grey3)
            point(origin)
        }
    }

    private inner class Point(
        val homeRegion: Region,
        val angle: Float
    ) {

        val distFromHomeOrigin: Float
            get() = location.dist(homeRegion.origin)
        var location: PVector = homeRegion.origin.copy()
        var includedInBorder: Boolean = false

        fun setNewRadius(radius: Float) {
            val dX = radius * cos(angle)
            val dY = radius * sin(angle)
            location.set(homeRegion.origin.x + dX, homeRegion.origin.y + dY)
        }

        fun distFrom(otherLocation: PVector): Float {
            return location.dist(otherLocation)
        }
    }
}