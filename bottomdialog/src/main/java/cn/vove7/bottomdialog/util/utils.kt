package cn.vove7.bottomdialog.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import cn.vove7.bottomdialog.R


/**
 * # utils
 *
 * @author Vove
 * 2019/6/28
 */


fun runOnUi(block: () -> Unit) {
    Looper.getMainLooper().also {
        if (Looper.myLooper() == it) {
            block()
        } else {
            Handler(it).post(block)
        }
    }
}


fun View.fadeOut(duration: Long = 800, endStatus: Int = View.GONE) {
    visibility = endStatus
    startAnimation(AlphaAnimation(1f, 0f).apply {
        setDuration(duration)
    })
}

fun View.fadeIn(duration: Long = 500) {
    visibility = View.VISIBLE
    startAnimation(AlphaAnimation(0f, 1f).apply {
        setDuration(duration)
    })
}

fun Context.attrColor(@AttrRes attrId: Int, @ColorInt dc: Int): Int {
    val attrsArray = intArrayOf(attrId)
    val typedArray: TypedArray = obtainStyledAttributes(attrsArray)
    val c = typedArray.getColor(0, dc)
    typedArray.recycle()
    return c
}

fun Context.attrDimension(@AttrRes attrId: Int, dv: Float): Float {
    val attrsArray = intArrayOf(attrId)
    val typedArray: TypedArray = obtainStyledAttributes(attrsArray)
    val c = typedArray.getDimension(0, dv)
    typedArray.recycle()
    return c
}

val Context.primaryColor: Int?
    get() = ContextCompat.getColor(this, R.color.colorPrimary)

val Context.accentColor: Int?
    get() = ContextCompat.getColor(this, R.color.colorAccent)

val Context.isDarkMode
    get() = (resources!!.configuration.uiMode
            and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
