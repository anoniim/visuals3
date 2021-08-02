package util

import processing.core.PConstants.PI
import processing.core.PConstants.TWO_PI

@Suppress("unused")
internal enum class Polygon(val innerAngle: Float) {
    TRIANGLE(2 * PI / 3f),
    SQUARE(PI / 2f),
    PENTAGON(TWO_PI / 5f),
    HEXAGON(PI / 3f),
    OCTAGON(PI / 4f),
}