package util

import Screen
import kotlin.random.Random

@Suppress("MemberVisibilityCanBePrivate")
open class Grid<Item: Grid.SimpleItem>(
    val width: Float,
    val height: Float,
    val itemSize: Float = DEFAULT_ITEM_SIZE,
    val orientation: Orientation = Orientation.HORIZONTAL
) {
    constructor(
        screen: Screen,
        itemSize: Float = DEFAULT_ITEM_SIZE,
        orientation: Orientation = Orientation.HORIZONTAL
    ): this(screen.widthF, screen.heightF, itemSize, orientation)

    val numOfCols = (width / itemSize).toInt()
    val numOfRows = (height / itemSize).toInt()
    val size: Int = numOfCols * numOfRows
    val items: MutableList<Item> = mutableListOf()

    fun initItems(initItem: (Int, Float, Float) -> Item) {
        for (n: Int in 0 until size) {
            val x: Float = (n % numOfCols) * itemSize
            val y: Float = (n / numOfCols) * itemSize
            items.add(initItem(n, x, y))
        }
    }

    open fun updateAndDraw() {
        for (item in items) {
            item.update()
            item.draw()
        }
    }

    companion object {
        const val DEFAULT_ITEM_SIZE: Float = 30F
    }

    abstract class SimpleItem {
        abstract fun update()
        abstract fun draw()
    }

    abstract class ChangingItem: SimpleItem() {
        abstract fun refresh()
    }

    enum class Orientation {
        HORIZONTAL,
        VERTICAL
    }
}

class RollingGrid<Item: Grid.ChangingItem>(
    width: Float,
    height: Float,
    itemSize: Float = DEFAULT_ITEM_SIZE,
    orientation: Orientation = Orientation.HORIZONTAL
): Grid<Item>(width, height, itemSize, orientation) {

    constructor(
        screen: Screen,
        itemSize: Float = DEFAULT_ITEM_SIZE,
        orientation: Orientation = Orientation.HORIZONTAL
    ): this(screen.widthF, screen.heightF, itemSize, orientation)

    val numOfRefreshLines: Int = when (orientation) {
        Orientation.HORIZONTAL -> numOfRows
        Orientation.VERTICAL -> numOfCols
    }
    val itemLineIterators = MutableList(numOfRefreshLines) {
        ItemLineIterator(orientation, numOfCols, numOfRows, it)
    }

    override fun updateAndDraw() {
        for ((index, item) in items.withIndex()) {
            if (index in itemLineIterators.map { it.currentIndex }) {
                item.refresh()
            }
            item.update()
            item.draw()
        }
        itemLineIterators.forEach {
            it.update()
        }
    }

    class ItemLineIterator(val orientation: Orientation, val numOfCols: Int, numOfRows: Int, forLine: Int) {

        private var startIndex: Int = when (orientation) {
            Orientation.HORIZONTAL -> forLine * numOfCols
            Orientation.VERTICAL -> forLine
        }
        private var endIndex: Int = when (orientation) {
            Orientation.HORIZONTAL -> ((forLine + 1) * numOfCols) - 1
            Orientation.VERTICAL -> (numOfRows - 1) * numOfCols + forLine - 1
        }
        var currentIndex: Int = Random.nextInt(endIndex)

        fun update() {
            when (orientation) {
                Orientation.HORIZONTAL -> currentIndex++
                Orientation.VERTICAL -> currentIndex += numOfCols
            }
            if (currentIndex >= endIndex) {
                currentIndex = startIndex - Random.nextInt(100)
            }
        }
    }
}