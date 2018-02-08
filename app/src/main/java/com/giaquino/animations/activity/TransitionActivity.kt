package com.giaquino.animations.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.giaquino.animations.R
import com.giaquino.animations.activity.transition.btn_to_dialog.BtnToDialogActivity1
import com.giaquino.animations.activity.transition.fab_to_dialog.FabToDialogActivity1

class TransitionActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_transition)
  }

  fun onClickFabToDialog(view: View) = startActivity(Intent(this, FabToDialogActivity1::class.java))

  fun onClickButtonToDialog(view: View)
      = startActivity(Intent(this, BtnToDialogActivity1::class.java))
}
