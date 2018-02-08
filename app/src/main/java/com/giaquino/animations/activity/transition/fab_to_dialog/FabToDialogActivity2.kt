package com.giaquino.animations.activity.transition.fab_to_dialog

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.giaquino.animations.R

class FabToDialogActivity2 : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_transition_fab_to_dialog_2)
  }

  fun onClickClose(view: View) = supportFinishAfterTransition()
}
