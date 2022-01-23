package util

import processing.core.PVector

operator fun PVector.times(multiplier: Float): PVector = this.copy().mult(multiplier)
operator fun PVector.timesAssign(multiplier: Float) { this.mult(multiplier) }

operator fun PVector.plus(addend: PVector): PVector = this.copy().add(addend)
operator fun PVector.plusAssign(addend: PVector) { this.add(addend) }

operator fun PVector.minus(subtrahend: PVector): PVector = this.copy().sub(subtrahend)
operator fun PVector.minusAssign(subtrahend: PVector) { this.sub(subtrahend) }