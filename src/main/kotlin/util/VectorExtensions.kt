package util

import processing.core.PVector

class Vector {
    companion object {
        fun zero() = PVector(0f, 0f)
    }
}

fun zeroVector() = PVector(0f, 0f)

operator fun PVector.times(multiplier: Float): PVector = this.mult(multiplier)