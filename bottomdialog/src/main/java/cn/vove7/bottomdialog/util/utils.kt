package cn.vove7.bottomdialog.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.annotation.AttrRes
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

fun Context.attr(@AttrRes attrId: Int): TypedValue = TypedValue().also {
    theme.resolveAttribute(attrId, it, true)
}

val Context.primaryColor: Int?
    get() = ContextCompat.getColor(this, R.color.colorPrimary)
val Context.accentColor: Int?
    get() = ContextCompat.getColor(this, R.color.colorAccent)