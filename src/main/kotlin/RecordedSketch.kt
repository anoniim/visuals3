abstract class RecordedSketch: BaseSketch() {

    private val defaultNumOfRecordedFrames = 120
    private var recording: Boolean = false
    protected var totalFrames = defaultNumOfRecordedFrames
    private var take: Int = 1

    protected fun record(
        frames: Int = defaultNumOfRecordedFrames,
        take: Int = 1,
        record: Boolean = true
    ) {
        recording = record
        totalFrames = frames
        this.take = take
    }

    override fun draw() {
        val percent = calculateAnimationProgress(recording)
        render(percent)
        if (recording) {
            saveFrame("recordings/${this::class.simpleName}/$take/###.png")
            if (frameCount == totalFrames) {
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