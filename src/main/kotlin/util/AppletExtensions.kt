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

fun PApplet.translateToCenter() {
    translate(width/2f, height/2f)
}

fun PApplet.vertex(vector: PVector) {
    vertex(vector.x, vector.y)
}

fun PApplet.curveVertex(vector: PVector) {
    curveVertex(vector.x, vector.y)
}

fun PApplet.point(vector: PVector) {
    point(vector.x, vector.y)
}

fun PApplet.line(start: PVector, end: PVector) {
    line(start.x, start.y, end.x, end.y)
}

fun PApplet.triangle(v1: PVector, v2: PVector, v3: PVector) {
    triangle(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y)
}

fun PApplet.quad(v1: PVector, v2: PVector, v3: PVector, v4: PVector) {
    quad(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y)
}

fun PApplet.square(vector: PVector, extent: Float) {
    square(vector.x, vector.y, extent)
}

fun PApplet.circle(vector: PVector, extent: Float) {
    circle(vector.x, vector.y, extent)
}