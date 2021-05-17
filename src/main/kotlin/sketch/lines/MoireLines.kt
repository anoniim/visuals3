package sketch.lines

import BaseSketch
import Screen
import processing.core.PVector

class MoireLines : BaseSketch(Screen(1500, 800)) {

    private val spikes = Config(
        spacing = 200f,
        strokeWeight = 90f,
        translation = Translation(
            translation = PVector(screen.centerX + 150f, screen.centerY),
            translationStep = PVector(-0.02f, 0f),
            translationFn = Translation.Fn.SINE,
            sineRange = 0.3f
        ),
        rotation = Rotation(
            angle = radians(1f)
        ),
        mouseControl = false
    )

    private val rollingBars = Config(
        spacing = 200f,
        strokeWeight = 115f,
        translation = Translation(
            translationStep = PVector(0.9f, 0f),
            sineRange = 80f
        ),
        rotation = Rotation(),
        mouseControl = true
    )

    private val rockingSquares = Config(
        spacing = 55f,
        strokeWeight = 50f, // 5f
        translation = Translation(
            translation = PVector(screen.centerX, screen.centerY),
            translationStep = PVector(0.04f, 0f),
            translationFn = Translation.Fn.SINE,
            sineRange = 6f
        ),
        rotation = Rotation(
            angle = 0f,
            angleStep = 0.06f,
            rotationFn = Rotation.Fn.SINE,
            angleRange = QUARTER_PI
        ),
        mouseControl = false
    )

//    private val config = spikes
        private val config = rollingBars
//    private val config = rockingSquares

    override fun setup() {
        noFill()
    }

    override fun draw() {
        background(grey3)
        stroke(grey9)
        strokeWeight(config.strokeWeight)
        drawLines(0..config.numLines)

        val translation = getTranslation()
        translate(translation.x, translation.y)
        rotate(getAngle())
        strokeWeight(config.strokeWeight2)
        drawLines(-config.numLinesWithBuffer..config.numLinesWithBuffer)
    }

    private fun getTranslation(): PVector {
        return when {
            config.mouseControl -> PVector(mouseXF, mouseYF)
            config.translation.translationFn != Translation.Fn.NONE -> config.translation.updateTranslation()
            else -> PVector()
        }
    }

    private fun getAngle(): Float {
        when {
            config.mouseControl -> {
                if (mousePressed && mouseButton == RIGHT) {
                    config.rotation.increaseAngle()
                } else if (mousePressed && mouseButton == LEFT) {
                    config.rotation.decreaseAngle()
                }
            }
            config.rotation.rotationFn != Rotation.Fn.NONE -> config.rotation.updateRotation()
        }
        return config.rotation.angle
    }

    private fun drawLines(lineRange: IntRange) {
        val lengthWithBuffer = 1.2f * widthF
        for (i in lineRange) {
            val x = i * config.spacing
            line(x, -lengthWithBuffer, x, lengthWithBuffer)
        }
    }

    inner class Config(
        val spacing: Float = 10f,
        val strokeWeight: Float = 3f,
        val strokeWeight2: Float = strokeWeight,
        val translation: Translation,
        val rotation: Rotation,
        val mouseControl: Boolean = true
    ) {

        val numLines = floor(screen.widthF / spacing)
        val numLinesWithBuffer = floor(1.5f * numLines)

        init {
            translation.config = this
            rotation.config = this
        }
    }

    class Translation(
        val translation: PVector = PVector(0f, 0f),
        val translationFn: Fn = Fn.LINEAR,
        val translationStep: PVector = PVector(1f, 0f),
        val sineRange: Float = 200f
    ) {

        var oscillationProgress = -HALF_PI
        lateinit var config: Config

        fun updateTranslation(): PVector {
            val target = applyTranslationFn()
            checkBounds(target)
            return target
        }

        private fun applyTranslationFn(): PVector = when (translationFn) {
            Fn.NONE -> translation
            Fn.LINEAR -> translation.add(translationStep)
            Fn.SINE -> {
                oscillationProgress += translationStep.x
                val x = sineRange * sin(oscillationProgress)
                translation.add(PVector(x, 0f))
            }
        }

        private fun checkBounds(target: PVector) {
            val maxTranslation = config.numLinesWithBuffer * config.spacing
            if (target.x > maxTranslation) {
                target.add(PVector(-maxTranslation, 0f))
            } else if (target.x < -maxTranslation) {
                target.add(PVector(maxTranslation, 0f))
            }
        }

        enum class Fn {
            NONE,
            LINEAR,
            SINE
        }
    }

    class Rotation(
        var angle: Float = radians(0f),
        val angleStep: Float = 0.01f,
        val rotationFn: Fn = Fn.NONE,
        val angleRange: Float = TWO_PI
    ) {

        var currentAngle = angle
        lateinit var config: Config

        fun updateRotation() {
            when (rotationFn) {
                Fn.LINEAR -> increaseAngle()
                Fn.SINE -> {
                    currentAngle += angleStep
                    angle = HALF_PI + angleRange * sin(currentAngle)
                }
                Fn.NONE -> {
                }
            }
        }

        fun increaseAngle() {
            angle += angleStep
        }

        fun decreaseAngle() {
            angle -= angleStep
        }

        enum class Fn {
            NONE,
            LINEAR,
            SINE;
        }
    }
}