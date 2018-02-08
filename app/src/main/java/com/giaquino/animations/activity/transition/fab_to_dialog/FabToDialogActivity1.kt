package com.giaquino.animations.activity.transition.fab_to_dialog

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.giaquino.animations.R

/**
 * Source: this is base on Plaid with little changes (for educational purposes)
 *
 * Perform activity transition that will morph a circular view (FloatingActionBar) to
 * a Dialog.
 *
 * - Imitate a dialog using a translucent activity.
 * - Add overlay that look like the circular view.
 * - Perform translate while doing circular reveal animation to make it circular going to rectangle.
 *
 * Transition is set via style using transition res and will only work for api 21 and above.
 *
 * @see [com.giaquino.animations.common.transition.CircularTransform]
 */
class FabToDialogActivity1 : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_transition_fab_to_dialog_1)
  }

  fun onClickFab(view: View) {
    val opts = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view,
        getString(R.string.transition_fab_to_dialog)).toBundle()
    ActivityCompat.startActivity(this, Intent(this, FabToDialogActivity2::class.java), opts)
  }
}