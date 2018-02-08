package com.giaquino.animations.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.giaquino.animations.R.layout

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_main)
  }

  fun onClickWidgets(view: View) = startActivity(Intent(this, WidgetActivity::class.java))

  fun onClickAnimations(view: View) = startActivity(Intent(this, AnimationActivity::class.java))

  fun onClickTransitions(view: View) = startActivity(Intent(this, TransitionActivity::class.java))
}