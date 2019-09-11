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

