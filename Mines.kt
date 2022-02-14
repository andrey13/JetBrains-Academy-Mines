package minesweeper

import kotlin.random.Random

class Cell(
    var mine: Boolean = false,
    var free: Boolean = false,
    var mark: Boolean = false,
    var sumMines: Int = 0,
) {
    fun printData() {
        when {
            mine && free -> print("X")
            free && sumMines == 0 -> print("/")
            free && sumMines != 0 -> print(sumMines.toString())
            mark -> print("*")
            else -> print(".")
        }
    }
    fun printDataAll() {
        when {
            mine -> print("X")
            sumMines != 0 -> print(sumMines.toString())
            else -> print(".")
        }
    }
}

class MineField(val nRows: Int, val nCols: Int, val nMines: Int) {
    val data = mutableListOf<MutableList<Cell>>()

    init {
        val emptyCell = mutableListOf<MutableList<Int>>()
        repeat(nRows) { row ->
            val mineRow = mutableListOf<Cell>()
            repeat(nCols) { col ->
                mineRow.add(Cell())
                emptyCell.add(mutableListOf(row, col))
            }
            data.add(mineRow)
        }

        val randomGenerator = Random.Default
        repeat(nMines) {
            val randomCellIndex =  if (emptyCell.size == 1) 0 else randomGenerator.nextInt(0, emptyCell.size - 1)
            val row = emptyCell[randomCellIndex][0]
            val col = emptyCell[randomCellIndex][1]
            data[row][col].mine = true
            emptyCell.removeAt(randomCellIndex)
        }

        repeat(nRows) { row ->
            repeat(nCols) { col ->
                data[row][col].sumMines = sumMinesAround(row, col)
            }
        }

    }
    fun printData() {
        print(" │")
        for (i in 1..nCols) print("$i")
        println("│")

        print("-│")
        for (i in 1..nCols) print("-")
        println("│")

        repeat(nRows) {row ->
            print("${row + 1}│")
            repeat(nCols) { col ->
                    data[row][col].printData()
            }
            println("│")
        }

        print("-│")
        for (i in 1..nCols) print("-")
        println("│")
    }

    fun printDataAll() {
        print(" │")
        for (i in 1..nCols) print("$i")
        println("│")

        print("-│")
        for (i in 1..nCols) print("-")
        println("│")

        repeat(nRows) {row ->
            print("${row + 1}│")
            repeat(nCols) { col ->
                data[row][col].printDataAll()
            }
            println("│")
        }

        print("-│")
        for (i in 1..nCols) print("-")
        println("│")
    }

    fun sumMinesAround(row: Int, col: Int): Int {
        var sum = 0
        for (r in -1..1) {
            for (c in -1..1) {
                if (row + r in 0..nRows-1 && col + c in 0..nCols-1) {
                    sum += if (data[row + r][col + c].mine) 1 else 0
                }
            }
        }
        return sum
    }

    fun markMine(row: Int, col: Int) {
        if (data[row][col].free) return
        var mark = data[row][col].mark
        data[row][col].mark = !mark
    }

    fun openMine(row: Int, col: Int) {
        if (row < 0 || col < 0 || row > nRows - 1 || col > nCols - 1) return
        if (data[row][col].mine) data[row][col].free = true
    }


    fun freeMine(row: Int, col: Int) {
        if (row < 0 || col < 0 || row > nRows - 1 || col > nCols - 1) return
        val cell = data[row][col]
        if (cell.free) return
        if (cell.mine) return

        data[row][col].free = true

//        if (cell.sumMines > 0) {
//            openMine(row - 1, col - 1)
//            openMine(row - 0, col - 1)
//            openMine(row - 1, col - 0)
//            openMine(row + 0, col + 1)
//            openMine(row + 1, col + 0)
//            openMine(row + 1, col + 1)
//            return
//        }


        freeMine(row - 1, col - 1)
        freeMine(row - 0, col - 1)
        freeMine(row - 1, col - 0)
        freeMine(row + 0, col + 1)
        freeMine(row + 1, col + 0)
        freeMine(row + 1, col + 1)
    }

    fun testGame(): Boolean {
        var nFree = 0
        var nMark = 0
        repeat(nRows) {row ->
            repeat(nCols) {col->
                if (data[row][col].free) ++nFree
                if (data[row][col].mark) ++nMark
            }
        }
        return nFree + nMark == nRows * nCols
    }
}

fun main() {
    val nRow = 9
    val nCol = 9
    print("How many mines do you want on the field? ")
    val nMines = readLine()!!.toInt()

    val mineField = MineField(nRow, nCol, nMines)

    mineField.printData()
    while(true) {
        print("Set/unset mines marks or claim a cell as free: ")
        val (sCol, sRow, cmd) = readLine()!!.split(" ")
        val row = sRow.toInt() - 1
        val col = sCol.toInt() - 1

        when(cmd) {
            "mine" -> mineField.markMine(row, col)
            "free" -> {
                if (mineField.data[row][col].mine) {
                    mineField.printDataAll()
                    println("You stepped on a mine and failed!")
                    break
                }
                mineField.freeMine(row, col)
            }
            else -> continue
        }
        mineField.printData()
        if (mineField.testGame()) {
            println("Congratulations! You found all the mines!")
            break
        }
    }
}
