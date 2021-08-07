package util

import processing.core.PApplet

class Interpolation(
    private val animationLength: Float,
    private val method: Method = Method.Smooth
) {

    var accumulatedProgress = 0f

    internal fun interpolate(from: Float, to: Float, animationProgress: Float): Float {
        val normalizedProgress = PApplet.map(animationProgress, 0f, animationLength, 0f, PApplet.TWO_PI)
        accumulatedProgress += method.formula(normalizedProgress)
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