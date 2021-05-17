package util

import processing.core.PVector

operator fun PVector.times(multiplier: Float): PVector = this.mult(multiplier)
operator fun PVector.plus(addend: PVector): PVector = this.add(addend)
operator fun PVector.minus(subtrahend: PVector): PVector = this.sub(subtrahend)