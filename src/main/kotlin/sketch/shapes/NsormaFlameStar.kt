package sketch.shapes

import BaseSketch
import processing.core.PShape
import processing.core.PVector
import util.translateToCenter
import java.util.*

class NsormaFlameStar : BaseSketch(
    renderer = P2D
) {

    private var numOfTips = 8f
    private var angle = TWO_PI / numOfTips
    private val historyColors = colors.flame.reversed()
    private val maxHistorySize = historyColors.size

    private val extRadius = 470f
    private val extRadiusFactor = 0.4f
    private val intRadius = 100f

    private val points = mutableListOf<PVector>()
    private val noiseMap = mutableMapOf<Float, Float>()
    private val shapeHistory = LinkedList<PShape>()

    override fun draw() {
        background(grey3)
        translateToCenter()
        autoRotate() // config
        drawStarShape()
        updateNoiseMap()
    }

    private var rotationAngle = 0f

    private fun autoRotate() {
        rotate(rotationAngle)
        rotationAngle += noise(frameCountF / 50) / 300
    }

    private fun updateNoiseMap() {
        noiseMap.keys.forEach { id ->
            noiseMap[id] = getNoiseForId(id) + 0.005f
        }
    }

    private fun drawStarShape() {
        noStroke()
        val currentShape = exteriorVertices()
        addShapeToHistory(currentShape)
        shapeHistory.forEachIndexed { i, shape ->
            shape.setFill(historyColors[i])
            shape.scale(0.95f)
            shape(shape)
        }
        interiorVertices()
    }

    private fun addShapeToHistory(currentShape: PShape) {
        if (frameCount % 1 == 0) {
            if (shapeHistory.size >= maxHistorySize) {
                shapeHistory.removeLast()
            }
            shapeHistory.addFirst(currentShape)
        }
    }

    private fun exteriorVertices(): PShape {
        val shape = createShape()
        shape.beginShape()
        repeat(ceil(numOfTips)) {
            addBeam(shape, it)
        }
        shape.endShape()
        return shape
    }

    private fun addBeam(shape: PShape, it: Int) {
        val tipAngle = it * angle
        val v1 = PVector.fromAngle(tipAngle).setMag(extRadius * extRadiusFactor).rotate(-angle / 2)
        shape.vertex(v1.x, v1.y)

        val tipLength = if (it % 2 == 0) extRadius else extRadius * 0.9f
        addFlareVertices(shape, tipAngle, tipLength, -1)

        val v2 = PVector.fromAngle(tipAngle).setMag(tipLength)
        shape.vertex(v2.x, v2.y)

        addFlareVertices(shape, tipAngle, tipLength, 1)

        val v3 = PVector.fromAngle(tipAngle).setMag(extRadius * extRadiusFactor).rotate(angle / 2)
        shape.vertex(v3.x, v3.y)
    }

    private fun addFlareVertices(shape: PShape, tipAngle: Float, length: Float, direction: Int) {
        val vertices = mutableListOf<PVector>()
        vertices.add(PVector.fromAngle(tipAngle).setMag(length * 0.3f).rotate(direction * 2 * angle / 3))
        vertices.add(PVector.fromAngle(tipAngle).setMag(length * 0.4f).rotate(direction * angle / 2))
        vertices.add(
            PVector.fromAngle(tipAngle).setMag(length * noiseLength(10 + tipAngle, 0.3f, 0.6f))
                .rotate(direction * angle / 3)
        )
        vertices.add(
            PVector.fromAngle(tipAngle).setMag(length * noiseLength(20 + tipAngle, 0.4f, 0.7f))
                .rotate(direction * angle / 4)
        )
        vertices.add(
            PVector.fromAngle(tipAngle).setMag(length * noiseLength(30 + tipAngle, 0.6f, 0.9f))
                .rotate(direction * angle / 6)
        )
        vertices.add(
            PVector.fromAngle(tipAngle).setMag(length * noiseLength(40 + tipAngle, 0.7f, 0.8f))
                .rotate(direction * angle / 8)
        )
        vertices.add(
            PVector.fromAngle(tipAngle).setMag(length * noiseLength(40 + tipAngle, 0.8f, 0.9f))
                .rotate(direction * angle / 10)
        )
        vertices.add(PVector.fromAngle(tipAngle).setMag(length * 1.0f))
        vertices.add(PVector.fromAngle(tipAngle).setMag(length * 1.1f).rotate(-1 * direction * angle / 5))
        if (direction == 1) {
            vertices.reverse()
        }
        vertices.forEach {
            shape.curveVertex(it.x, it.y)
        }
    }

    private fun noiseLength(id: Float, min: Float, max: Float) = map(noise(getNoiseForId(id)), 0f, 1f, min, max)

    private fun getNoiseForId(id: Float): Float {
        val noise = noiseMap[id]
        if (noise == null) {
            noiseMap[id] = random(100f)
        }
        return noise ?: 0f
    }

    private fun interiorVertices() {
        noStroke()
        fill(grey3)
        beginShape()
        val intVerticesCount = 5
        val intAngle = TWO_PI / intVerticesCount
        for (i in -1..intVerticesCount + 1) {
            addCurveVertex(intAngle * i, intRadius)
        }
        endShape()
    }

    private fun addCurveVertex(angle: Float, radius: Float) {
        val x = radius * cos(angle)
        val y = radius * sin(angle)
        curveVertex(x, y)
        points.add(PVector(x, y))
    }
}