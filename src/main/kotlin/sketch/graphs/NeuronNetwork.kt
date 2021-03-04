package sketch.graphs

import BaseSketch
import Screen
import processing.core.PVector
import kotlin.random.Random

class NeuronNetwork : BaseSketch(Screen(1200, 800), longClickClear = true) {

    private val initialStrength = 8f // 8
    private val signalSpeed = 2f // 2
    private val connectionHardness = 1.4f // 1-2

    private val maxDist = sqrt(sq(screen.widthF) + sq(screen.heightF))
    private lateinit var network: MutableList<Neuron>
    private var networkNew = mutableListOf<Neuron>()

    override fun setup() {
        generateNetwork()
    }

    private fun generateNetwork() {
        network = MutableList(30) {
            Neuron(
                PVector(
                    random(0f, screen.widthF),
                    random(0f, screen.heightF)
                )
            )
        }
        network.forEach {
            it.initSynapses()
        }
    }

    override fun draw() {
        background(grey3)
        network.forEach {
            it.show()
            it.update()
        }
        if (networkNew.isNotEmpty()) {
            network.addAll(networkNew)
            networkNew.clear()
        }
        drawLongPressOverlay()
    }

    override fun reset() {
        generateNetwork()
    }

    inner class Neuron(
        val position: PVector,
        var strength: Float = initialStrength,
        var potential: Float = strength/2,
    ) {

        private val signals = mutableListOf<Signal>()
        private val synapses = mutableListOf<Neuron>()
        val synapsesActive = mutableListOf<Neuron>()
        var splitInto = Random.nextInt(5) + 1

        fun update() {
            sendSignal()
            updateSignals()
//            checkTooBig() // XXX
        }

        private fun sendSignal() {
            if (synapses.isNotEmpty() && Random.nextFloat() < 0.01 * potential) {
                val other = synapses.random()
                if (!synapsesActive.contains(other) && !other.synapsesActive.contains(this)) {
                    synapsesActive.add(other)
                    other.synapsesActive.add(this)
                    signals.add(Signal(this, other, potential/2))
                    potential /= 2
                }
            }
        }

        private fun updateSignals() {
            signals.forEach {
                it.update()
                it.show()
            }
            signals.removeIf { it.hasArrived }
        }

        private fun checkTooBig() {
            if (synapsesActive.isEmpty() && strength > splitInto * initialStrength) {
                for (n in 0..splitInto) {
                    spawnNew(splitInto)
                }
                strength /= splitInto
                splitInto = Random.nextInt(8) + 4
            }
        }

        private fun spawnNew(fraction: Int) {
            networkNew.add(Neuron(generateNewPosition(), strength / fraction)
                .apply {
                    connectWith(this)
                    initSynapses()
                })
        }

        private fun generateNewPosition() = position.copy().add(PVector.random2D().setMag(10 * strength))

        fun show() {
            strokeWeight(potential)
            stroke(yellow)
            fill(grey9)
            circle(position.x, position.y, strength)

            strokeWeight(1f)
            stroke(grey9)
            synapses.forEach {
                line(position.x, position.y, it.position.x, it.position.y)
            }
        }

        fun initSynapses() {
            for (other in network) {
                if (canConnect(other)) {
                    if (chanceByDistance(other)) {
                        connectWith(other)
                    }
                }
            }
        }

        private fun connectWith(other: Neuron) {
            synapses.add(other)
            other.synapses.add(this)
        }

        private fun canConnect(other: Neuron) =
            other != this && !other.synapses.contains(this) && !synapses.contains(other)

        private fun chanceByDistance(other: Neuron): Boolean {
            val dist = map(position.dist(other.position), 0f, maxDist, 0f, 1f)
            // TODO Use exponential decay e^(-5x)
            return Random.nextFloat() * connectionHardness < log(1.01f) / dist
        }
    }

    inner class Signal(
        val start: Neuron,
        val end: Neuron,
        val potential: Float
    ) {

        private val totalDistance = start.position.dist(end.position)
        private var currentPosition = start.position.copy()
        var progress: Float = 0f
        var hasArrived: Boolean = false

        fun update() {
            progress = updateProgress()
            move()
            checkArrived()
        }

        private fun checkArrived() {
            if (progress > 1) {
                end.potential += potential
                end.strength += potential/4
                start.synapsesActive.remove(end)
                end.synapsesActive.remove(start)
                hasArrived = true
            }
        }

        private fun move() {
            currentPosition = start.position.copy().lerp(end.position, progress)
        }

        private fun updateProgress(): Float {
            val distFromStart = start.position.dist(currentPosition) + signalSpeed
            return map(distFromStart, 0f, totalDistance, 0f, 1f)
        }

        fun show() {
            strokeWeight(potential * 2)
            stroke(white)
            point(currentPosition.x, currentPosition.y)
        }
    }
}