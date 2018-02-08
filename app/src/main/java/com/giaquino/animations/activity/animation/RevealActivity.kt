package com.giaquino.animations.activity.animation

import android.databinding.DataBindingUtil
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.View
import android.view.ViewGroup
import com.giaquino.animations.R
import com.giaquino.animations.common.transition.RevealTransition
import com.giaquino.animations.common.util.linkify
import com.giaquino.animations.databinding.ActivityRevealBinding
import kotlin.LazyThreadSafetyMode.NONE

class RevealActivity : AppCompatActivity() {

  private val binding: ActivityRevealBinding by lazy(NONE) {
    DataBindingUtil.setContentView<ActivityRevealBinding>(this, R.layout.activity_reveal)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding.activity = this
    binding.content

    binding.content.text = "Hello <a href=\"https://www.w3schools.com\">Visit W3Schools</a> World"
    linkify(binding.content,
        ContextCompat.getColorStateList(this, R.color.link),
        ContextCompat.getColor(this, R.color.primary))
  }

  @RequiresApi(VERSION_CODES.LOLLIPOP)
  fun onClickAnimate() {
    val transition = TransitionSet()
    transition.ordering = TransitionSet.ORDERING_SEQUENTIAL
    val view = binding.content
    if (view.visibility == View.VISIBLE) {
      transition.addTransition(RevealTransition()).addTransition(ChangeBounds())
      TransitionManager.beginDelayedTransition(binding.root as ViewGroup, transition)
      view.visibility = View.GONE
    } else {
      transition.addTransition(ChangeBounds()).addTransition(RevealTransition())
      TransitionManager.beginDelayedTransition(binding.root as ViewGroup, transition)
      view.visibility = View.VISIBLE
    }
  }
}