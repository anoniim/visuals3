package sketch.patterns

import BaseSketch
import util.translateToCenter

class ToiletPaper : BaseSketch(renderer = P3D) {

    override fun draw() {
        translateToCenter()
        noStroke()
        fill(grey11)
        val plane = createShape(BOX)
        box(widthF, heightF, 1f)

        noLoop()
    }

}
