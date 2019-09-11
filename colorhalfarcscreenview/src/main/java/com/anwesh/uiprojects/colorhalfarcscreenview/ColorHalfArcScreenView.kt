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

fun Canvas.drawCHASNode(i : Int, scale : Float, sc : Float, currI : Int, paint : Paint) {
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
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += dir * scGap
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class CHASNode(var i : Int, val state : State = State()) {

        private var next : CHASNode? = null
        private var prev : CHASNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = CHASNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, sc : Float, currI : Int, paint : Paint) {
            canvas?.drawCHASNode(i, state.scale, sc, currI, paint)
            if (state.scale > 0f) {
                next?.draw(canvas, state.scale, currI, paint)
            }
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : CHASNode {
            var curr : CHASNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class ColorHalfArcScreen(var i : Int) {

        private val root : CHASNode = CHASNode(0)
        private var curr : CHASNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, 0f, curr.i, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : ColorHalfArcScreenView) {

        private val animator : Animator = Animator(view)
        private val chas : ColorHalfArcScreen = ColorHalfArcScreen(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            chas.draw(canvas, paint)
            animator.animate {
                chas.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            chas.startUpdating {
                animator.start()
            }
        }
    }
}
