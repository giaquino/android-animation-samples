package com.giaquino.animations.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity

class LayoutActivity : AppCompatActivity() {

  companion object {
    private const val EXTRA_LAYOUT = "layout"
    @JvmStatic fun create(context: Context, @LayoutRes layout: Int)
        = Intent(context, LayoutActivity::class.java).putExtra(EXTRA_LAYOUT, layout)
  }

  private var layout: Int = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    layout = savedInstanceState?.getInt(EXTRA_LAYOUT) ?: intent.getIntExtra(EXTRA_LAYOUT, -1)
    setContentView(layout)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt(EXTRA_LAYOUT, layout)
  }
}