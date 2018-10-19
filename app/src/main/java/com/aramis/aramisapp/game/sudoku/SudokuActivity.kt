package com.aramis.aramisapp.game.sudoku

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.aramis.aramisapp.R
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import kotlinx.android.synthetic.main.activity_sudoku.*
import org.jetbrains.anko.toast

/**
 *Created by Aramis
 *Date:2018/9/27
 *Description:
 */
class SudokuActivity : AraBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sudoku)
        title = "数独"

        val dialog = AlertDialog.Builder(this).setMessage("恭喜，解谜成功")
                .setPositiveButton("再来一局") { dialog, which ->
                    toast("再来一局")
                    sudokuView.getRandomSudokuQuestion()
                }.setNegativeButton("取消") { dialog, which ->
                    dialog.dismiss()
                }
                .create()

        sudokuView.showAuxiliaryLine = true
        sudokuView.showAuxiliaryPoint = true
        sudokuView.seeCount=25
        sudokuView.onSolveSuccessListener = {
            sudokuControlView.solveSuccess()
            dialog.show()
        }
        sudokuView.onItemClickListener = { a: Int, b: Int, viewData: SudokuView.ViewData ->
            if (!sudokuControlView.isTimeRunning) {
                sudokuControlView.startTime()
            }
        }
        sudokuControlView.onNumberClickListener = {
            sudokuView.setValue(it)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        sudokuControlView.stopTime()
    }

    override fun getPresenter(): AraBasePresenter? = null

}