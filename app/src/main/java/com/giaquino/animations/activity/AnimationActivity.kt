package com.giaquino.animations.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.giaquino.animations.R
import com.giaquino.animations.activity.animation.ProgressActivity
import com.giaquino.animations.activity.animation.RevealActivity

class AnimationActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_animation)
  }

  fun onClickReveal(view: View) = startActivity(Intent(this, RevealActivity::class.java))

  fun onClickProgress(view: View) = startActivity(Intent(this, ProgressActivity::class.java))
}