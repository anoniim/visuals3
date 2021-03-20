package util

import kotlin.math.pow
import kotlin.math.roundToInt

fun Float.roundTo(numDecimalDigits: Int): Float {
    val factor = 10.0f.pow(numDecimalDigits.toFloat())
    return (this * factor).roundToInt() / factor
}

fun Int.isOdd(): Boolean {
    return this % 2 == 1
}