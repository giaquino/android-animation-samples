package com.giaquino.animations.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.giaquino.animations.R

class WidgetActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_widget)
  }

  fun onClickFlowLayout(view: View) = startActivity(
      LayoutActivity.create(this, R.layout.layout_flow_layout))
}