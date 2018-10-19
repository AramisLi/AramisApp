package com.aramis.aramisapp.game.g2048

import com.aramis.library.extentions.logE
import java.io.Serializable
import java.util.*
import kotlin.math.floor

/**
 *Created by Aramis
 *Date:2018/10/11
 *Description:
 */

open class Cell(var x: Int, var y: Int) : Serializable {
    override fun toString(): String {
        return "Cell(x=$x, y=$y)"
    }
}

class Tile : Cell {
    val value: Int
    var mergedForm: Array<Tile>? = null

    constructor(x: Int, y: Int, value: Int) : super(x, y) {
        this.value = value
    }

    constructor(cell: Cell, value: Int) : super(cell.x, cell.y) {
        this.value = value
    }

    fun updatePosition(cell: Cell) {
        this.x = cell.x
        this.y = cell.y
    }

    override fun toString(): String {
        return "Tile(value=$value, mergedForm=${Arrays.toString(mergedForm)})"
    }


}

/**
 * 用户操作记录
 */
class Grid(sizeX: Int, sizeY: Int) {
    val field: Array<Array<Tile?>> = Array(sizeX) { _ ->
        Array<Tile?>(sizeY) { null }
    }
    val undoField: Array<Array<Tile?>> = Array(sizeX) { _ ->
        Array<Tile?>(sizeY) { null }
    }

    private val bufferField: Array<Array<Tile?>> = Array(sizeX) { _ ->
        Array<Tile?>(sizeY) { null }
    }

    fun randomAvailableCell(): Cell? {
        val availableCells = getAvailableCells()
        if (availableCells.isNotEmpty()) {
            val index = floor(Math.random() * availableCells.size).toInt()
            logE("randomAvailableCell:$index")
            return availableCells[index]
        }
        return null
    }

    private fun getAvailableCells(): MutableList<Cell> {
        val r = mutableListOf<Cell>()
        for (xx in 0 until field.size) {
            for (yy in 0 until field[xx].size) {
                if (field[xx][yy] == null) {
                    r.add(Cell(xx, yy))
                }
            }
        }
        return r
    }

    fun isCellsAvailable(): Boolean = getAvailableCells().isNotEmpty()

    //占用
    fun isCellOccupied(cell: Cell): Boolean = getCellContent(cell) != null

    fun getCellContent(cell: Cell) = getCellContent(cell.x, cell.y)

    fun getCellContent(x: Int, y: Int): Tile? {
        if (isCellWithinBounds(x, y)) {
            return field[x][y]
        }
        return null
    }

    fun isCellWithinBounds(cell: Cell): Boolean = isCellWithinBounds(cell.x, cell.y)

    fun isCellWithinBounds(x: Int, y: Int): Boolean {
        return 0 <= x && x < field.size && 0 <= y && y < field[0].size
    }

    fun insertTile(tile: Tile) {
        logE("insertTile:$tile")
        field[tile.x][tile.y] = tile
    }

    fun removeTile(tile: Tile) {
        field[tile.x][tile.y] = null
    }

    fun saveTiles() {
        copyTiles(bufferField, undoField)
    }

    fun prepareSaveTiles() {
        copyTiles(field, bufferField)
    }

    fun revertTiles() {
        copyTiles(undoField, field)
    }

    private fun copyTiles(from: Array<Array<Tile?>>, to: Array<Array<Tile?>>, release: Boolean = false) {
        from.forEachIndexed { xx, arrayOfTiles ->
            arrayOfTiles.forEachIndexed { yy, tile ->
                to[xx][yy] = if (tile == null || release) null else Tile(xx, yy, tile.value)
            }
        }
    }

    fun cleanGrid() {
        (0 until field.size).forEach { xx ->
            (0 until field[0].size).forEach { yy ->
                field[xx][yy] = null
            }
        }
    }

    private fun clearUndoGrid() {
        copyTiles(field, undoField, true)

    }


}

fun <T, M> dyadicArray2Array(dyadicArray: List<List<T>>, t2m: (t: T) -> M): List<M> {
    val r = mutableListOf<M>()
    dyadicArray.forEach { arr ->
        arr.forEach {
            r.add(t2m.invoke(it))
        }
    }
    return r
}