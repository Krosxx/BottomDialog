package cn.vove7.bottomdialog.extension

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Build
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import cn.vove7.bottomdialog.StatusCallback
import cn.vove7.bottomdialog.builder.BottomDialogBuilder
import cn.vove7.bottomdialog.interfaces.ContentBuilder
import cn.vove7.bottomdialog.util.fadeIn
import cn.vove7.bottomdialog.util.fadeOut
import cn.vove7.bottomdialog.util.isDarkMode
import cn.vove7.bottomdialog.util.listenToUpdate
import kotlinx.android.synthetic.main.header_awesome.view.*


fun BottomDialogBuilder.awesomeHeader(title: String, isDark: Boolean = context.isDarkMode) {
    header(AwesomeHeader()) {
        this.title = title
        isDarkHeader = isDark
    }
}

/**
 * # AwesomeHeader
 *
 * @author Vove
 * 2019/7/11
 */
class AwesomeHeader : ContentBuilder(), StatusCallback {
    override val layoutRes: Int
        get() = R.layout.header_awesome

    var title: String? by listenToUpdate(null, this)

    lateinit var littleTitle: TextView
    lateinit var titleView: TextView

    lateinit var titleLay: View
    lateinit var fillView: View
    lateinit var filllay: View
    override fun init(view: View) {
        view.close_btn.setOnClickListener {
            dialog.dismiss()
        }
        fillView = view.fill_view
        filllay = view.fill_layout
        titleLay = view.title_lay
        filllay.layoutParams = filllay.layoutParams.also {
            it.height = dialog.stateBarHeight + 50
        }
        dialog.immersionStatusBar = true
        littleTitle = view.little_title
        titleView = view.title

        dialog.headerView.setBackgroundColor(0x0)
        listenStatus(this)

    }

    var isDarkHeader: Boolean = false

    var status = 0
    var lastOff = 0f

    override fun onSlide(slideOffset: Float) {
        if (slideOffset >= 0.99f && (lastOff < slideOffset) && status != 1) {
            status = 1
            littleTitle.fadeOut(400, endStatus = View.INVISIBLE)
            titleLay.fadeIn(400)
            fill()
            setStatusbarColor()
        } else if (slideOffset < 0.99f && (lastOff > slideOffset) && status != 0) {
            status = 0
            littleTitle.fadeIn(400)
            titleLay.fadeOut(400, endStatus = View.INVISIBLE)
            unFill()
            setStatusbarColor()
        }
        lastOff = slideOffset
    }

    fun fill() {
        startAnimation(filllay.height)
    }

    fun unFill() {
        startAnimation(0)
    }

    var animator: Animator? = null
    fun startAnimation(to: Int) {
        val begin = fillView.height

        animator?.cancel()

        animator = ValueAnimator.ofInt(begin, to).apply {
            duration = 400

            interpolator = DecelerateInterpolator()

            addUpdateListener {
                fillView.layoutParams = fillView.layoutParams.also { g ->
                    g.height = it.animatedValue as Int
                }
            }
            start()
        }
    }

    /**
     * 改变状态栏主题
     * 暂不支持6.0以下系统
     */
    private fun setStatusbarColor() {
        if (isDarkHeader) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (status == 0) {
                dialog.window?.decorView?.systemUiVisibility =
                    SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    dialog.window?.decorView?.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }
    }

    override fun updateContent(type: Int, data: Any?) {
        littleTitle.text = title
        titleView.text = title
    }
}