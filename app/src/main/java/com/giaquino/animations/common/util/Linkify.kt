package com.giaquino.animations.common.util

import android.content.res.ColorStateList
import android.os.Parcel
import android.text.Html
import android.text.Selection
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.style.URLSpan
import android.view.MotionEvent
import android.widget.TextView

fun linkify(view: TextView, textColor: ColorStateList, highlightColor: Int) {
  if (view.text.isEmpty()) return
  val spanned = linkifyText(view.text.toString(), textColor, highlightColor)
  setLinkifiedText(view, spanned)
}

private fun linkifyText(input: String, textColor: ColorStateList,
    linkHighlightColor: Int): SpannableStringBuilder {

  var spanned = Html.fromHtml(input) as SpannableStringBuilder

  /* strip trailing newlines */
  if (spanned[spanned.length - 1] == '\n') {
    spanned = spanned.delete(spanned.length - 1, spanned.length)
  }

  /* replace urlspan with our own touchablespan */
  spanned.getSpans(0, spanned.length, URLSpan::class.java).forEach {
    val start = spanned.getSpanStart(it)
    val end = spanned.getSpanEnd(it)
    spanned.removeSpan(it)
    spanned.setSpan(TouchableUrlSpan(it.url, textColor, linkHighlightColor), start, end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
  }
  return spanned
}

private fun setLinkifiedText(view: TextView, linkified: CharSequence) {
  view.apply {
    text = linkified
    isFocusable = false
    isClickable = false
    isLongClickable = false
    movementMethod = LinkTouchMovementMethod.getInstance()
  }
}


/**
 * An extension to URLSpan which changes it's background & foreground color when clicked.
 *
 * Derived from http://stackoverflow.com/a/20905824
 */
class TouchableUrlSpan : URLSpan {

  companion object {
    private val STATE_PRESSED = intArrayOf(android.R.attr.state_pressed)
  }

  private val normalTextColor: Int
  private val pressedTextColor: Int
  private val pressedBackgroundColor: Int

  var pressed = false

  constructor(url: String, textColor: ColorStateList, pressedBackgroundColor: Int) : super(url) {
    this.normalTextColor = textColor.defaultColor
    this.pressedTextColor = textColor.getColorForState(STATE_PRESSED, normalTextColor)
    this.pressedBackgroundColor = pressedBackgroundColor
  }

  constructor(src: Parcel) : super(src) {
    normalTextColor = src.readInt()
    pressedTextColor = src.readInt()
    pressedBackgroundColor = src.readInt()
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeInt(normalTextColor)
    dest.writeInt(pressedTextColor)
    dest.writeInt(pressedBackgroundColor)
  }

  override fun updateDrawState(ds: TextPaint) {
    ds.color = if (pressed) pressedTextColor else normalTextColor
    ds.bgColor = if (pressed) pressedBackgroundColor else 0
    ds.isUnderlineText = !pressed
  }
}

/**
 * A movement method that only highlights any touched
 * [TouchableUrlSpan]s
 *
 * Adapted from  http://stackoverflow.com/a/20905824
 */
class LinkTouchMovementMethod : LinkMovementMethod() {

  companion object {
    private var instance: LinkTouchMovementMethod? = null
    fun getInstance(): MovementMethod {
      if (instance == null) instance = LinkTouchMovementMethod()
      return instance!!
    }
  }

  private var pressedSpan: TouchableUrlSpan? = null

  override fun onTouchEvent(textView: TextView, spannable: Spannable, event: MotionEvent): Boolean {
    var handled = false
    if (event.action == MotionEvent.ACTION_DOWN) {
      pressedSpan = getPressedSpan(textView, spannable, event)
      if (pressedSpan != null) {
        pressedSpan!!.pressed = true
        Selection.setSelection(spannable, spannable.getSpanStart(pressedSpan),
            spannable.getSpanEnd(pressedSpan))
        handled = true
      }
    } else if (event.action == MotionEvent.ACTION_MOVE) {
      val touchedSpan = getPressedSpan(textView, spannable, event)
      if (pressedSpan != null && touchedSpan != pressedSpan) {
        pressedSpan!!.pressed = false
        pressedSpan = null
        Selection.removeSelection(spannable)
      }
    } else {
      if (pressedSpan != null) {
        pressedSpan!!.pressed = false
        super.onTouchEvent(textView, spannable, event)
        handled = true
      }
      pressedSpan = null
      Selection.removeSelection(spannable)
    }
    return handled
  }

  private fun getPressedSpan(textView: TextView, spannable: Spannable,
      event: MotionEvent): TouchableUrlSpan? {

    var x = event.x.toInt()
    var y = event.y.toInt()

    x -= textView.totalPaddingLeft
    y -= textView.totalPaddingTop

    x += textView.scrollX
    y += textView.scrollY

    val layout = textView.layout
    val line = layout.getLineForVertical(y)
    val off = layout.getOffsetForHorizontal(line, x.toFloat())

    val link = spannable.getSpans(off, off, TouchableUrlSpan::class.java)
    var touchedSpan: TouchableUrlSpan? = null
    if (link.isNotEmpty()) touchedSpan = link[0]
    return touchedSpan
  }
}