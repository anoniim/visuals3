package util

import processing.core.PVector

class Vectors {
    companion object {
        fun zero() = PVector(0f, 0f)
    }
}

operator fun PVector.times(multiplier: Float): PVector = this.mult(multiplier)
operator fun PVector.plus(addend: PVector): PVector = this.add(addend)
operator fun PVector.minus(subtrahend: PVector): PVector = this.sub(subtrahend)