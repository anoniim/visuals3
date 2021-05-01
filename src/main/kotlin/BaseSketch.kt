import processing.core.PApplet
import processing.core.PShape
import sound.SoundHelper

private const val defaultWidth = 1600
private const val defaultHeight = 900

open class BaseSketch(
    val screen: Screen = Screen(defaultWidth, defaultHeight),
    private val renderer: String? = null,
    private val longClickClear: Boolean = false
) : PApplet() {

    /* Helpers */
    val sound: SoundHelper by lazy { SoundHelper(this) }

    /* Colors */
    internal val black = color(0, 0, 0)
    internal val white = color(200, 200, 200)
    internal val grey11 = color(111, 111, 111)
    internal val grey9 = color(99, 99, 99)
    internal val grey7 = color(77, 77, 77)
    internal val grey5 = color(55, 55, 55)
    internal val grey3 = color(33, 33, 33)
    internal val grey1 = color(11, 11, 11)
    internal val red = color(160, 40, 0)
    internal val darkRed = color(120, 30, 0)
    internal val green = color(70, 200, 70)
    internal val darkGreen = color(40, 100, 40)
    internal val blue = color(0, 70, 200)
    internal val purple = color(100, 70, 200)
    internal val orange = color(200, 150, 70)
    internal val yellow = color(150, 150, 70)
    internal val pink = color(240, 140, 240)
    internal val transparent = color(0, 0, 0, 255)
    internal val transparentDark = color(0, 0, 0, 100)
    internal val transparentLight = color(255, 255, 255, 100)

    /* Floats */
    val mouseXF: Float
        get() = mouseX.toFloat()
    val mouseYF: Float
        get() = mouseY.toFloat()
    val widthF: Float
        get() = width.toFloat()
    val heightF: Float
        get() = height.toFloat()
    val halfWidthF: Float
        get() = width / 2f
    val halfHeightF: Float
        get() = height / 2f

    /* Initial settings */
    override fun settings() {
        if (screen.fullscreen) {
            fullScreen()
            System.err.println("Fullscreen, don't forget to set the screen size in setup()\n" +
                    "screen.width = width\n" +
                    "screen.height = height")
        } else {
            if (renderer != null) {
                size(screen.width, screen.height, renderer)
            } else {
                size(screen.width, screen.height)
            }
        }
    }

    /* Mouse long click clears sketch */
    private val longClickResetMillis = 2000
    private val longClickResetCueMillis = 1500
    private var mousePressedMillis: Int = 0

    override fun mousePressed() {
        mousePressedMillis = millis()
    }

    override fun mouseReleased() {
        if (longClickClear && millis() - mousePressedMillis > longClickResetMillis) {
            reset()
        }
    }

    open fun reset() {
        background(grey1)
    }

    private val resetOverlay: PShape by lazy {
        createShape(RECT, 0f, 0f, widthF, heightF).apply {
            setStroke(false)
        }
    }

    /**
     * Call at the end of [draw] to darken the screen a second before the reset.
     */
    fun drawLongPressOverlay(reverseTransformation: (() -> Unit)? = null) {
        val mouseDownTime = millis() - mousePressedMillis
        if (longClickClear && mousePressed && mouseDownTime > longClickResetCueMillis) {
            reverseTransformation?.invoke()
            // Darken screen before resetting
            shape(resetOverlay.apply {
                setFill(true) // needs to be called in case noFill() was set before creating the shape
                val overlayAlpha = map(mouseDownTime.toFloat(), 1000f, 2000f, 0f, 255f)
                setFill(color(11f, 11f, 11f, overlayAlpha))
            })
        }
    }

    /* === Helper methods === */

    /* map() that takes Int */
    protected fun map(value: Int, start1: Int, stop1: Int, start2: Int, stop2: Int): Float {
        return map(value.toFloat(), start1.toFloat(), stop1.toFloat(), start2.toFloat(), stop2.toFloat())
    }

    /* random() that takes Int */
    internal fun random(max: Int) = random(max.toFloat())

    protected fun distFromScreenCenter(x1: Float, y1: Float) =
        dist(x1, y1, screen.widthF / 2, screen.heightF / 2)

    protected fun distFromMouse(x1: Float, y1: Float) =
        dist(x1, y1, mouseXF, mouseYF)

    /* Recording */
    protected fun recordAndExit(numFrames: Int) {
        saveFrame("###.png")
        if (frameCount == numFrames) {
            exit()
        }
    }
}

class Screen(var width: Int = defaultWidth, var height: Int = defaultHeight, val fullscreen: Boolean = false) {
    val widthF: Float by lazy { width.toFloat() }
    val heightF: Float by lazy { height.toFloat() }
    val centerX: Float by lazy { width / 2F }
    val centerY: Float by lazy { height / 2F }
    val halfWidth: Float by lazy { width / 2F }
    val halfHeight: Float by lazy { height / 2F }
}