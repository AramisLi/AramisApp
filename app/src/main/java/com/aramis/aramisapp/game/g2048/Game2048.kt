package com.aramis.aramisapp.game.g2048

import android.content.Context
import com.aramis.library.extentions.logE
import kotlin.math.pow

/**
 *Created by Aramis
 *Date:2018/10/11
 *Description:
 */

class Game2048(val context: Context, val mView: G2048View) {

    val numSquaresX = 4
    val numSquaresY = 4
    private val endingMaxValue: Int = 2.0.pow(mView.numCellTypes - 1).toInt()
    //animation
    val grid: Grid = Grid(numSquaresX, numSquaresY)
    val aGrid = AnimationGrid(numSquaresX, numSquaresY)
    //game state
    var gameState = GAME_NORMAL
        private set
    var lastGameState = GAME_NORMAL
        private set
    private var bufferGameState = GAME_NORMAL
    //score
    var score: Long = 0
    var highScore: Long = 0
    var lastScore: Long = 0
    private var bufferScore: Long = 0

    fun newGame() {

        grid.cleanGrid()

        addStartTile()

        grid.field.forEach {
            it.forEach { tile -> logE("tile:$tile") }
        }
    }

    //新游戏开始时，添加2个随机单元格
    private fun addStartTile() {
        val startTiles = 2
        (0 until startTiles).forEach { addRandomTile() }
    }

    //添加随机单元格
    private fun addRandomTile() {
//        if (grid.isCellsAvailable()) {
        val value = if (Math.random() < 0.9) 2 else 4
        val randomTile = grid.randomAvailableCell()
        randomTile?.apply {
            val tile = Tile(this, value)
            logE("添加随机单元格:$tile")
            grid.insertTile(tile)
        }

//        }
    }

    //游戏是否进行中
    fun isActive() = true

    /**
     * 移动
     * @param direction 0=上移，1=右移，2=下移，3=左移
     */
    fun move(direction: Int) {

        aGrid.cancelAnimations()
        if (!isActive()) {
            return
        }
        prepareUndoState()
        val vector = getVector(direction)
        val traversalsX = buildTraversalsX(vector)
        val traversalsY = buildTraversalsY(vector)
//        logE("traversalsX:$traversalsX")
//        logE("traversalsY:$traversalsY")
//        logE("vector:$vector")
        var moved = false
        prepareTiles()

        for (xx in traversalsX) {
            for (yy in traversalsY) {
//                logE("xx:$xx,yy:$yy")
                val cell = Cell(xx, yy)
                val tile = grid.getCellContent(cell)
                if (tile != null) {
                    val positions = findFarthestPosition(cell, vector)
                    //在vector向量上，"最远"的单元格
                    val next = grid.getCellContent(positions[1])
                    if (next != null && next.value == tile.value && next.mergedForm == null) {
                        val merged = Tile(positions[1], tile.value * 2)
                        val temp = arrayOf(tile, next)
                        merged.mergedForm = temp

                        grid.insertTile(merged)
                        grid.removeTile(tile)

                        tile.updatePosition(positions[1])

                        val extras = arrayOf(xx, yy)
                        aGrid.startAnimation(merged.x, merged.y, MOVE_ANIMATION, MOVE_ANIMATION_TIME.toLong(),
                                0, extras)
                        aGrid.startAnimation(merged.x, merged.y, MERGE_ANIMATION, SPAWN_ANIMATION_TIME.toLong(),
                                MOVE_ANIMATION_TIME.toLong(), null)

                        score += merged.value

                    } else {
                        moveTile(tile, positions[0])
                        val extras = arrayOf(xx, yy, 0)
                        aGrid.startAnimation(positions[0].x, positions[0].y, MOVE_ANIMATION, MOVE_ANIMATION_TIME.toLong(), 0, extras)
                    }

                    if (!positionsEqual(cell, tile)) {
                        moved = true
                    }
                }
            }
        }

        if (moved) {
            saveUndoState()
            addRandomTile()
            checkLose()
        }
        mView.invalidate()
    }

    private fun checkLose() {

    }

    private fun saveUndoState() {

    }

    private fun positionsEqual(first: Cell, second: Cell): Boolean {
        return first.x == second.x && first.y == second.y
    }

    private fun moveTile(tile: Tile, cell: Cell) {
        grid.field[tile.x][tile.y] = null
        grid.field[cell.x][cell.y] = tile
        tile.updatePosition(cell)
    }

    private fun findFarthestPosition(cell: Cell, vector: Cell): Array<Cell> {
        var nextCell = Cell(cell.x, cell.y)
        var previous: Cell

        do {
            previous = nextCell
            nextCell = Cell(previous.x + vector.x, previous.y + vector.y)
        } while (grid.isCellWithinBounds(nextCell) && grid.isCellsAvailable())

        return arrayOf(previous, nextCell)
    }

    private fun prepareTiles() {
        grid.field.forEach { arr ->
            arr.forEach {
                if (it != null && grid.isCellOccupied(it)) {
                    it.mergedForm = null
                }
            }
        }
    }


    private fun buildTraversalsX(vector: Cell): List<Int> {
        val traversals = (0 until numSquaresX).toMutableList()
        if (vector.x == 1) {
            traversals.reverse()
        }
        return traversals
    }

    private fun buildTraversalsY(vector: Cell): List<Int> {
        val traversals = (0 until numSquaresY).toMutableList()
        if (vector.y == 1) {
            traversals.reverse()
        }
        return traversals
    }

    //向量cell集合 0=up 1=right 2=down 3=left
    private val directionVectorArray = arrayOf(Cell(0, -1), Cell(1, 0), Cell(0, 1), Cell(-1, 0))

    private fun getVector(direction: Int): Cell {
        return directionVectorArray[direction]
    }

    private fun prepareUndoState() {
        grid.prepareSaveTiles()
        bufferScore = score
        bufferGameState = gameState
    }

    companion object {
        const val GAME_WIN = 1
        const val GAME_LOST = -1
        const val GAME_NORMAL = 0

        const val SPAWN_ANIMATION = -1
        const val MOVE_ANIMATION = 0
        const val MERGE_ANIMATION = 1

        const val FADE_GLOBAL_ANIMATION = 0
        private const val MOVE_ANIMATION_TIME = G2048View.BASE_ANIMATION_TIME
        private const val SPAWN_ANIMATION_TIME = G2048View.BASE_ANIMATION_TIME
        private const val NOTIFICATION_DELAY_TIME = MOVE_ANIMATION_TIME + SPAWN_ANIMATION_TIME
        private const val NOTIFICATION_ANIMATION_TIME = G2048View.BASE_ANIMATION_TIME * 5
        private const val startingMaxValue = 2048

        private const val GAME_ENDLESS = 2
        private const val GAME_ENDLESS_WON = 3
        private const val HEGH_SOCRE = "high score"
        private const val FIRST_RUN = "first run"

    }


}








