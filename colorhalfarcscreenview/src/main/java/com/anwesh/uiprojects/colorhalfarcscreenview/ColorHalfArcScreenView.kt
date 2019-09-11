package com.anwesh.uiprojects.colorhalfarcscreenview

/**
 * Created by anweshmishra on 11/09/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

val colors : Array<String> = arrayOf("#9C27B0", "#1565C0", "#FF6F00", "#00C853", "#f44336")
val arcs : Int = 5
val scGap : Float = 0.01f / 5
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
val strokeFactor : Int = 25

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.max(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawHalfArc(i : Int, sc1 : Float, sc2 : Float, size : Float, shouldFill : Boolean, paint : Paint) {
    val sc1i : Float = sc1.divideScale(i, arcs)
    val sc2i : Float = sc2.divideScale(i, arcs)
    var sweepDeg : Float = 0f
    if (sc2i > 0f) {
        sweepDeg = 180f * sc2
    }
    if (shouldFill) {
        sweepDeg = 180f * (1 - sc1)
    }
    save()
    translate(i * 2 * size + size, 0f)
    drawArc(RectF(-size, -size, size, size), 180f + 180f * sc1i, sweepDeg, false, paint)
    restore()
}

fun Canvas.drawMultipleHalfArcs(sc1 : Float, sc2 : Float,  size : Float, shouldFill: Boolean, paint : Paint) {
    for (j in 0..(arcs - 1)) {
        drawHalfArc(j, sc1, sc2, size, shouldFill, paint)
    }
}

fun Canvas.drawMHASNode(i : Int, scale : Float, sc : Float, currI : Int, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val size : Float = w / (2 * arcs)
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    save()
    translate(0f, h / 2)
    drawMultipleHalfArcs(scale, sc, size, currI == i, paint)
    restore()
}

class ColorHalfArcScreenView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
    
}