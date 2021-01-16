import processing.core.PApplet
import kotlin.random.Random

open class BaseSketch(
    val screen: Screen = Screen(1600, 900),
    private val renderer: String? = null,
    private val longClickClear: Boolean = false
) : PApplet() {

    /** General config **/

    /** Colors **/
    internal val grey11 = color(111, 111, 111)
    internal val grey9 = color(99, 99, 99)
    internal val grey7 = color(77, 77, 77)
    internal val grey5 = color(55, 55, 55)
    internal val grey3 = color(33, 33, 33)
    internal val grey1 = color(11, 11, 11)
    internal val red = color(200, 70, 0)
    internal val green = color(70, 200, 70)
    internal val blue = color(0, 70, 200)
    internal val purple = color(100, 70, 200)
    internal val orange = color(200, 150, 70)
    internal val yellow = color(150, 150, 70)
    internal val transparent = color(0, 0, 0, 255)
    internal val transparentDark = color(0, 0, 0, 100)
    internal val transparentLight = color(255, 255, 255, 100)

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

    override fun draw() {
        val mouseDownTime = millis() - mousePressedMillis
        if (longClickClear && mousePressed && mouseDownTime > 1000) {
            noStroke()
            val overlayAlpha = map(mouseDownTime.toFloat(), 1000f, 2000f, 0f, 255f)
            fill(grey1, overlayAlpha)
            rect(0f, 0f, widthF, heightF)
        }
    }

    internal fun random(max: Int) = map(Random.nextFloat(), 0F, 1F, 0F, max.toFloat())

    /** Initial settings **/
    override fun settings() {
        if (screen.fullscreen) {
            fullScreen()
            screen.width = width
            screen.height = height
        } else {
            if (renderer != null) {
                size(screen.width, screen.height, renderer)
            } else {
                size(screen.width, screen.height)
            }
        }
    }

    /** Mouse long click clears sketch **/
    private var mousePressedMillis: Int = 0
    override fun mousePressed() {
        mousePressedMillis = millis()
    }

    override fun mouseReleased() {
        if (longClickClear && millis() - mousePressedMillis > 2000) {
            reset()
        }
    }

    open fun reset() {
        background(grey1)
    }

    /** Helper methods **/

    protected fun map(value: Int, start1: Int, stop1: Int, start2: Int, stop2: Int): Float {
        return map(value.toFloat(), start1.toFloat(), stop1.toFloat(), start2.toFloat(), stop2.toFloat())
    }

    protected fun distFromScreenCenter(x1: Float, y1: Float) =
        dist(x1, y1, screen.widthF / 2, screen.heightF / 2)

    protected fun distFromMouse(x1: Float, y1: Float) =
        dist(x1, y1, mouseXF, mouseYF)

    protected fun recordAndExit(numFrames: Int) {
        saveFrame("###.png")
        if (frameCount == numFrames) {
            exit()
        }
    }
}

class Screen(var width: Int, var height: Int, val fullscreen: Boolean = false) {
    val widthF: Float = width.toFloat()
    val heightF: Float = height.toFloat()
    val centerX: Float = width / 2F
    val centerY: Float = height / 2F
    val halfWidth: Float = width / 2F
    val halfHeight: Float = height / 2F
}