package com.example.kidsdrawingapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context,
                  attrs: AttributeSet) : View(context, attrs){
    //creating var we need
    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    //set a arraylist to keep the path we draw in
    private val mpath = ArrayList<CustomPath>()
    //set a arraylist to keep the last path to undo path
    private val mUndoPath = ArrayList<CustomPath>()
    // set the var we created
    init {
        setDrawingView()
    }

    fun onClickUndo(){
        if (mpath.size > 0){
            mUndoPath.add(mpath.removeAt(mpath.size-1))
            invalidate()
        }
    }

    private fun setDrawingView(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        //mBrushSize = 20.toFloat()
    }
    //set the bitmap
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }
    //drawing on canvas
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)
        for(path in mpath){
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas?.drawPath(path, mDrawPaint!!)
        }


        mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness

        mDrawPaint!!.color = mDrawPath!!.color

        canvas?.drawPath(mDrawPath!!, mDrawPaint!!)
    }
    //touching the screen
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                mDrawPath = CustomPath(color, mBrushSize)

                mDrawPath!!.moveTo(touchX!!, touchY!!)

                //mDrawPath!!.brushThickness = mBrushSize

                //mDrawPath!!.color = color

            }
            MotionEvent.ACTION_MOVE ->{
                mDrawPath!!.lineTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_UP ->{
                mpath.add(mDrawPath!!)

                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate()

        return true
    }

    fun setSizeForBrush(newsize: Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            newsize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }


    fun setColor(newColor: String){
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path()

}