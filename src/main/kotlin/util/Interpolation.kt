package util

import processing.core.PApplet

class Interpolation(
    private val animationLength: Float,
    private val method: Method = Method.Smooth
) {

    internal fun interpolate(from: Float, to: Float, animationProgress: Float): Float {
        var accumulatedProgress = 0f
        repeat(animationProgress.toInt()) {
            val normalizedProgress = PApplet.map(it.toFloat(), 0f, animationLength, 0f, PApplet.TWO_PI)
            val increment = method.formula(normalizedProgress)
            accumulatedProgress += increment
        }
        return PApplet.map(accumulatedProgress, 0f, maxAccumulatedProgress, from, to)
    }

    private val maxAccumulatedProgress = run {
//        val increment = PApplet.TWO_PI / animationLength
//        var sum = 0f
//        repeat(animationLength.toInt()) {
//            sum += method.formula(it * increment)
//        }
//        sum

        // the result of the above calculation happens to be animationLength for Method.Smooth formula
        animationLength
    }

    sealed class Method {

        abstract val formula: (Float) -> Float

        object Smooth : Method() {
            override val formula = { normalizedProgress: Float -> -1 * PApplet.cos(normalizedProgress) + 1 }
        }
    }
}