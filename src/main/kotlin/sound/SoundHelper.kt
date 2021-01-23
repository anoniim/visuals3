package sound

import processing.core.PApplet
import processing.sound.*
import processing.sound.SoundFile


class SoundHelper(private val applet: PApplet) {

    init {
        println(Sound.list())
    }

    private val mic by lazy {
        val sound = Sound(applet)
        sound.inputDevice(1)
        AudioIn(applet, 0).apply { start() }
    }

    fun amplitude(file: SoundFile? = null): Amplitude {
        val amplitude = Amplitude(applet)
        amplitude.input(file ?: mic)
        return amplitude
    }

    /**
     * Returns Waveform of given [soundFile] (or microphone if no file provided).
     * Define how many [samples] of the Waveform you want to be able to read at once.
     * Each sample value is between -1 and +1.
     **/
    fun waveform(samples: Int, soundFile: SoundFile? = null): Waveform {
        val waveform = Waveform(applet, samples)
        waveform.input(soundFile ?: mic)
        return waveform
    }

    /**
     * Returns Fast Fourier Transform analysis of a stream of sound.
     * Change the number of [bands] to get more spectral bands (this needs to be a power of two)
     * at the expense of more coarse-grained time resolution of the spectrum.
     */
    fun fft(bands: Int, file: SoundFile? = null): FFT {
        val fft = FFT(applet, bands)
        fft.input(file ?: mic)
        return fft
    }

    @Deprecated("useless method, use the constructor")
    fun loadFile(filePath: String, loop: Boolean = true) {
        SoundFile(applet, filePath).apply {
            if (loop) {
                loop()
            }
        }
    }

    open class SceneCue {

        private var hasTriggered = false
        private var lastTriggeredMillis: Long = 0L
        private var repeatable: Boolean = false
        private var throttleMillis: Long = 0L
        private var triggerLimit: Int = Int.MAX_VALUE
        private var triggerCount: Int = 0

        fun setRepeatable(
            repeatable: Boolean = true,
            throttleMillis: Long = 0L,
            triggerLimit: Int = Int.MAX_VALUE
        ) {
            this.repeatable = repeatable
            this.throttleMillis = throttleMillis
            this.triggerLimit = triggerLimit
        }

        fun setEnabled(enabled: Boolean) {
            triggerLimit = if (enabled) {
                Int.MAX_VALUE
            } else {
                0
            }
        }

        protected fun check(condition: () -> Boolean, onTrigger: ((Int) -> Unit)?) {
            if (hasTriggered) {
                onTrigger?.invoke(triggerCount)
                return
            }
            if (condition() && !triggeredRecently() && !triggerLimitReached()) {
                if (repeatable) {
                    lastTriggeredMillis = System.currentTimeMillis()
                } else {
                    hasTriggered = true
                }
                triggerCount++
                onTrigger?.invoke(triggerCount)
            }
        }

        private fun triggerLimitReached(): Boolean {
            return triggerCount >= triggerLimit
        }

        private fun triggeredRecently(): Boolean {
            val now = System.currentTimeMillis()
            return now - lastTriggeredMillis < throttleMillis
        }

        fun hasTriggered(): Boolean {
            return hasTriggered
        }

        fun reset() {
            hasTriggered = false
        }
    }

    class FftSceneCue(
        private val fftBinIndices: List<Int>,
        private val threshold: Float
    ) : SceneCue() {

        fun checkAverage(fft: FFT, onTrigger: ((Int) -> Unit)? = null) {
            val isAverageAboveThreshold = {
                val average = getSelectedFftBinAverage(fft, fftBinIndices)
                average >= threshold
            }
            check(isAverageAboveThreshold, onTrigger)
        }

        fun checkAny(fft: FFT, onTrigger: ((Int) -> Unit)? = null) {
            val condition = {
                var hasAnyReachedThreshold = false
                fftBinIndices.forEach {
                    if (fft.spectrum[it] >= threshold) hasAnyReachedThreshold = true
                }
                hasAnyReachedThreshold
            }
            check(condition, onTrigger)
        }
    }

    companion object {

        fun getSelectedFftBinAverage(fft: FFT, fftBinIndices: List<Int>): Float {
            var fftBinSum = 0f
            fftBinIndices.forEach {
                fftBinSum += fft.spectrum[it]
            }
            return fftBinSum / fftBinIndices.size
        }
    }
}