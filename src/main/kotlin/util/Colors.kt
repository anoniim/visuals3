package util

import BaseSketch
import processing.core.PApplet
import java.util.*

class Colors(val applet: BaseSketch) {

    val flame = with(applet) {
        listOf(
            color(236, 167, 44),
            color(238, 86, 34),
            color(34, 30, 34),
            color(49, 38, 62),
            color(68, 53, 91)
        )
    }

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

    val sunrise = with(applet) {
        listOf(
            color(249, 65, 68),
            color(243, 114, 44),
            color(248, 150, 30),
            color(249, 132, 74),
            color(249, 199, 79),
            color(144, 190, 109),
            color(67, 170, 139),
            color(77, 144, 142),
            color(87, 117, 144),
            color(39, 125, 161),
        )
    }

    val blackAndWhite = with(applet) {
        listOf(
            grey3,
            white,
        )
    }

    val dirtyBeach = with(applet) {
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

    val blues = with(applet) {
        LinkedList(listOf(
            color(3, 4, 94),
            color(3, 44, 104),
            color(2, 62, 138),
            color(2, 72, 148),
            color(2, 82, 153),
            color(2, 92, 158),
            color(0, 119, 182),
            color(0, 150, 199),
            color(0, 165, 205),
            color(0, 180, 216),
            color(0, 190, 220),
            color(72, 202, 228),
            color(144, 224, 239),
            color(173, 232, 244),
            color(202, 240, 248),
        ))
    }

    val reds = with(applet) {
        LinkedList(listOf(
            color(89, 13, 34),
            color(128, 15, 47),
            color(164, 19, 60),
            color(184, 21, 68),
            color(201, 24, 74),
            color(221, 34, 84),
            color(241, 64, 94),
            color(255, 77, 109),
            color(255, 117, 143),
            color(255, 143, 163),
            color(255, 179, 193),
            color(255, 204, 213),
            color(255, 240, 243),
        ))
    }

    val greens = with(applet) {
        LinkedList(listOf(
            color(0, 75, 35),
            color(0, 100, 0),
            color(0, 114, 0),
            color(0, 128, 0),
            color(20, 148, 0),
            color(40, 168, 0),
            color(56, 176, 0),
            color(76, 196, 0),
            color(112, 224, 0),
            color(158, 240, 26),
            color(204, 255, 51),
        ))
    }

    val yellowToPurple = with(applet) {
        LinkedList(listOf(
            color(150, 150, 70),
            color(237, 226, 12),
            color(237, 124, 58),
            color(230, 77, 122),
            color(222, 29, 186),
            color(100, 70, 200),
        ))
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