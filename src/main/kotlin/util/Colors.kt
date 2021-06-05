package util

import processing.core.PApplet
import java.util.*

class Colors(val applet: PApplet) {
    val pastel = with(applet) {
        listOf(
            color(255, 173, 173),
            color(255, 214, 165),
            color(253, 255, 182),
            color(202, 255, 191),
            color(155, 246, 255),
            color(160, 196, 255),
            color(189, 178, 255),
            color(255, 198, 255),
            color(255, 255, 252),
        )
    }

    val blues = with(applet) {
        LinkedList(
            listOf(
                color(111, 111, 111),
                color(100, 100, 111),
                color(100, 100, 145),
                color(100, 130, 150),
                color(95, 135, 160),
                color(70, 140, 160),
                color(70, 155, 175),
                color(50, 165, 185),
                color(29, 173, 211),
                color(29, 173, 211),
                color(19, 160, 211),
                color(19, 160, 211),
                color(19, 173, 228),
                color(19, 173, 228),
                color(20, 183, 243),
                color(20, 183, 243),
                color(0, 160, 255),
                color(0, 160, 255),
                color(80, 180, 255),
                color(120, 200, 255),
                color(180, 230, 255),
                color(220, 255, 255),
                color(255, 255, 255)
            )
        )
    }

    private val augustiniColors = with(applet) {
        listOf(
            color(0, 0, 0)
        )
    }

    val random: Int
        get() {
            with(applet) {
                return color(random(50f, 200f), random(50f, 200f), random(50f, 200f))
            }
        }
}