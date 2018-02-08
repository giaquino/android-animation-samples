package com.giaquino.animations.activity.transition.btn_to_dialog

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.giaquino.animations.R

class BtnToDialogActivity1 : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_transition_btn_to_dialog_1)
  }

  fun onClickButton(view: View) {
    val opts = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view,
        getString(R.string.transition_btn_to_dialog)).toBundle()
    ActivityCompat.startActivity(this, Intent(this, BtnToDialogActivity2::class.java), opts)
  }
}