package util

import processing.core.PApplet
import processing.core.PVector

fun PApplet.translate(translation: PVector) {
    if (translation.z == 0f) {
        translate(translation.x, translation.y)
    } else {
        translate(translation.x, translation.y, translation.z)
    }
}

fun PApplet.vertex(vector: PVector) {
    vertex(vector.x, vector.y)
}