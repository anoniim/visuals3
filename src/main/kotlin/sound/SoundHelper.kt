package sound

import processing.core.PApplet
import processing.sound.*
import processing.sound.SoundFile


class SoundHelper(private val applet: PApplet) {

    init {
        println(Sound.list())
        val sound = Sound(applet)
        sound.inputDevice(1)
    }

    private val mic by lazy {
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
}