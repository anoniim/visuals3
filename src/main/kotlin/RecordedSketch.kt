abstract class RecordedSketch(
    screen: Screen = Screen(1600, 900),
    fullscreen: Boolean = false
): BaseSketch(screen, fullscreen) {

    private val defaultNumOfRecordedFrames = 120
    private var recording: Boolean = false
    protected var totalFrames = defaultNumOfRecordedFrames
    private var label: String = "1"
    private var skipFrames: Int = 0

    protected fun record(
        frames: Int = defaultNumOfRecordedFrames,
        label: String = "1",
        skipFrames: Int = 0,
        record: Boolean = true
    ) {
        recording = record
        totalFrames = frames
        this.label = label
        this.skipFrames = skipFrames
    }

    override fun draw() {
        val percent = calculateAnimationProgress(recording)
        render(percent)
        if (recording) {
            if (skipFrames == 0 || frameCount % skipFrames == 1) {
                saveFrame("recordings/${this::class.simpleName}/$label/###.png")
            }
            if (frameCount > totalFrames) {
                exit()
            }
        }
    }

    private fun calculateAnimationProgress(recording: Boolean): Float {
        val frameCount: Float = frameCount.toFloat()
        return if (recording) {
            frameCount / totalFrames
        } else {
            (frameCount % totalFrames) / totalFrames
        }
    }

    abstract fun render(percent: Float)
}