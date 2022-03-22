package com.mmspring.sudoku.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.mmspring.sudoku.model.Cell
import kotlin.math.min

class BoardView(context: Context, attributeSet: AttributeSet) : View(context,attributeSet){

    private var sqrtSize = 3
    private var size = 9

    private var cellSizePixel = 0F
    private var noteSizePixels = 0F

    private var selectedRow = 1
    private var selectedCol = 1

    private var listener: OnTouchListener? = null

    private var cells: List<Cell>? = null

    private val thickLinePaint = Paint().apply{
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4F
    }
    private val thinLinePaint = Paint().apply{
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2F
    }
    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#6ead3a")
    }
    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#efedef")
    }

    private val startingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#eeeeee")
    }

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        textSize = 32F
    }
    private val conflictTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.RED
        textSize = 32F
    }
    private val startingCellTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        typeface = Typeface.DEFAULT_BOLD
        textSize = 32F
    }

    private val noteTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val bPixelSize = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(bPixelSize,bPixelSize)
    }
    override fun onDraw(canvas: Canvas) {
        updateMeasurements(width)
        cellSizePixel = (width/size.toFloat())
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLinePaint)
        fillCells(canvas)
        drawLines(canvas)
        drawText(canvas)
    }
    private fun updateMeasurements(width: Int) {
        cellSizePixel = width / size.toFloat()
        noteSizePixels = cellSizePixel / sqrtSize.toFloat()
        noteTextPaint.textSize = cellSizePixel / sqrtSize.toFloat()
        textPaint.textSize = cellSizePixel / 1.5F
        startingCellTextPaint.textSize = cellSizePixel / 1.5F
    }
    private fun fillCells(canvas: Canvas){
            cells?.forEach {
                val r = it.row
                val c = it.col
                if (it.isStartingCell) {
                    fillCell(canvas, r, c, startingCellPaint)
                } else if (r == selectedRow && c == selectedCol) {
                    fillCell(canvas, r, c, selectedCellPaint)
                } else if (r == selectedRow || c == selectedCol) {
                    fillCell(canvas, r, c, conflictingCellPaint)
                } else if (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedCol / sqrtSize) {
                    fillCell(canvas, r, c, conflictingCellPaint)
                }
            }
    }
    private fun fillCell(canvas: Canvas, r:Int, c:Int, paint: Paint){
        canvas.drawRect(c * cellSizePixel,
            r * cellSizePixel,
            (c + 1) * cellSizePixel,
            (r + 1) * cellSizePixel, paint)
    }
    private fun drawLines(canvas: Canvas){
        canvas.drawRect(0F,0F,width.toFloat(),height.toFloat(),thickLinePaint)
        for (i in 1 until size){
            val paintToUse = when(i % sqrtSize){
                0 -> thickLinePaint
                else -> thinLinePaint
            }
            canvas.drawLine(i * cellSizePixel,
                0F,
                i * cellSizePixel,
                height.toFloat(),
                paintToUse)
            canvas.drawLine(
                0F,
                i * cellSizePixel,
                width.toFloat(),
                i * cellSizePixel,
                paintToUse
            )

        }
    }
    //draw text
    private fun drawText(canvas: Canvas){
        cells?.forEach { cell ->
            val value = cell.value

            if (value == 0) {
                // draw notes
                cell.notes.forEach { note ->
                    val rowInCell = (note - 1) / sqrtSize
                    val colInCell = (note - 1) % sqrtSize
                    val valueString = note.toString()

                    val textBounds = Rect()
                    noteTextPaint.getTextBounds(valueString, 0, valueString.length, textBounds)
                    val textWidth = noteTextPaint.measureText(valueString)
                    val textHeight = textBounds.height()
                    canvas.drawText(
                        valueString,
                        (cell.col * cellSizePixel) + (colInCell * noteSizePixels) + noteSizePixels / 2 - textWidth / 2f,
                        (cell.row * cellSizePixel) + (rowInCell * noteSizePixels) + noteSizePixels / 2 + textHeight / 2f,
                        noteTextPaint
                    )
                }
            } else {
                val row = cell.row
                val col = cell.col
                val valueString = cell.value.toString()
                val paintText = if(cell.isInvalid) textPaint else conflictTextPaint
                val paintToUse = if (cell.isStartingCell) startingCellTextPaint else paintText

                val textBounds = Rect()
                paintToUse.getTextBounds(valueString, 0, valueString.length, textBounds)
                val textWidth = paintToUse.measureText(valueString)
                val textHeight = textBounds.height()
                //val paintText = if(cell.isInvalid) paintToUse else conflictTextPaint
                canvas.drawText(valueString, (col * cellSizePixel) + cellSizePixel / 2 - textWidth / 2,
                    (row * cellSizePixel) + cellSizePixel / 2 + textHeight / 2, paintToUse)
            }
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    private fun handleTouchEvent(x: Float, y: Float) {
        val possibleSelectedRow = (y / cellSizePixel).toInt()
        val possibleSelectedCol = (x / cellSizePixel).toInt()
        listener?.onCellTouched(possibleSelectedRow, possibleSelectedCol)
    }
    fun updateSelectedCellUi(row:Int, col:Int){
        selectedRow = row
        selectedCol = col
        invalidate()
    }
    fun registerListener(listener: OnTouchListener) {
        this.listener = listener
    }

    fun updateCells(cells: List<Cell>) {
            this.cells = cells
        invalidate()
    }

    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int)

    }
}