package com.giaquino.animations.activity.animation

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.giaquino.animations.R
import com.giaquino.animations.common.transition.ProgressTransition
import com.giaquino.animations.databinding.ActivityProgressBinding
import kotlin.LazyThreadSafetyMode.NONE

class ProgressActivity : AppCompatActivity() {

  private val binding by lazy(NONE) {
    DataBindingUtil.setContentView<ActivityProgressBinding>(this, R.layout.activity_progress)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding.activity = this
  }

  fun onClickAnimate() {
    TransitionManager.beginDelayedTransition(binding.root as ViewGroup, ProgressTransition())
    binding.progress.progress = if (binding.progress.progress == 0) binding.progress.max else 0
  }
}
