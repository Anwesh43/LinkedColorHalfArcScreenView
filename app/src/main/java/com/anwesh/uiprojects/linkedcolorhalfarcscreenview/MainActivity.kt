package com.anwesh.uiprojects.linkedcolorhalfarcscreenview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.colorhalfarcscreenview.ColorHalfArcScreenView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ColorHalfArcScreenView.create(this)
    }
}
