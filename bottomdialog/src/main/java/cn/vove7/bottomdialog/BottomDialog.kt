package cn.vove7.bottomdialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import cn.vove7.bottomdialog.builder.BottomDialogBuilder
import cn.vove7.bottomdialog.interfaces.ContentBuilder


/**
 * # BottomDialog
 * 高自定义的对话框
 * 可自定义头布局，内容布局，底部布局
 *
 * @author Vove
 * 2019/6/24
 */
@Suppress("UNCHECKED_CAST", "unused")
class BottomDialog internal constructor(
        builder: BottomDialogBuilder
) : Dialog(builder.context, builder.themeId) {

    companion object {
        /**
         * @param activity Activity 为适配导航栏 需要传入Activity
         * 非Activity显示对话框，使用：[BottomDialogActivity]
         * @param action [@kotlin.ExtensionFunctionType] Function1<BottomDialogBuilder, Unit>
         */
        fun builder(activity: Activity, show: Boolean = true, action: BottomDialogBuilder.() -> Unit): BottomDialog {
            val b = BottomDialogBuilder(activity).apply(action)
            return BottomDialog(b).also {
                if (show) {
                    it.show()
                }
            }
        }
    }

    val activity: Activity = builder.context as Activity

    private val expand: Boolean = builder.expand

    /**
     * 头部布局
     */
    var headerBuilder: ContentBuilder? = builder.headerBuilder
        private set
    /**
     * 内容布局
     */
    var contentBuilder: ContentBuilder? = builder.contentBuilder
        private set

    /**
     * 底部布局（悬浮）
     */
    var footerBuilder: ContentBuilder? = builder.footerBuilder
        private set

    private var peekHeight: Int = builder.peekHeight

    private val headerView: ViewGroup get() = findViewById(R.id.header_container)
    private val footerView: ViewGroup get() = findViewById(R.id.footer_contains)

    private lateinit var behaviorController: BehaviorController

    private var mCancelable = builder.mCancelable

    var navColor: Int? = builder.navBgColor

    /**
     * 底部布局高度
     */
    private var bottomHeight = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_content)
        buildContent()
    }

    private val lis: StatusCallback = object : StatusCallback {
        override fun onSlide(slideOffset: Float) {
            footerBuilder ?: return
            if (slideOffset < 0) {
                footerView.scrollY = (slideOffset * bottomHeight).toInt()
            } else {
                footerView.scrollY = 0
            }
        }

        override fun onHidden() {
            sDismiss()
        }
    }

    private fun sDismiss() {
        super.dismiss()
    }

    override fun setCancelable(flag: Boolean) {
        super.setCancelable(flag)
        behaviorController.isHideable = flag
    }

    override fun dismiss() {
        behaviorController.hide()
    }

    /**
     * 使用ContentBuilder构建布局
     */
    private fun buildContent() {
        behaviorController = BehaviorController(findViewById(R.id.bs_root), lis)
        behaviorController.isHideable = mCancelable
        behaviorController.peekHeight = peekHeight
        behaviorController.hide()

        findViewById<ViewGroup>(R.id.root).setOnClickListener {
            if (mCancelable) cancel()
        }

        val contentView = findViewById<ViewGroup>(R.id.content)

        headerBuilder?.apply {
            headerView.addView(build(context, this@BottomDialog))
        }
        contentBuilder?.also {
            contentView.addView(it.build(context, this))
        }
        val navHeight = getNavigationBarHeight()
        footerBuilder?.also {
            footerView.addView(it.build(context, this).apply {
                post {
                    bottomHeight = this.height + navHeight
                    setContentMarginBottom(bottom + navHeight)
                }
            })
        } ?: {
            footerView.visibility = View.GONE
            setContentMarginBottom(navHeight)
        }.invoke()

        /**
         * 填充导航栏
         */
        findViewById<View>(R.id.fill_nav)?.also {
            navColor?.also { c ->
                it.setBackgroundColor(c)
            }
            it.layoutParams = it.layoutParams.apply {
                height = navHeight
            }
        }

        //fix: 高度为MATCH_PARENT时，状态栏黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
    }

    private fun setContentMarginBottom(value: Int) {
        val container = this@BottomDialog.findViewById<NestedScrollView>(R.id.container)
        container.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT).also { p ->
            p.setMargins(0, 0, 0, value)
            container.layoutParams = p
        }

    }

    private fun getNavigationBarHeight(): Int {
        val resources = context.resources

        return if (checkHasNavigationBar(activity)) {//判断是否有导航栏
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            resources.getDimensionPixelSize(resourceId)
        } else 0

    }

    /**
     * 用于获取各种属性
     * @param action [@kotlin.ExtensionFunctionType] Function1<BottomDialog, T>
     * @return T
     */
    fun <T> get(action: BottomDialog.() -> T): T {
        return with(this, action)
    }

    private val NAVIGATION = "navigationBarBackground"

    /**
     * 判断是否有NavigationBar
     *
     * @param activity
     * @return
     */
    @SuppressLint("ObsoleteSdkInt")
    fun checkHasNavigationBar(activity: Activity): Boolean {
        val windowManager = activity.windowManager
        val d = windowManager.defaultDisplay

        val realDisplayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics)
        }

        val realHeight = realDisplayMetrics.heightPixels
        val realWidth = realDisplayMetrics.widthPixels

        val displayMetrics = DisplayMetrics()
        d.getMetrics(displayMetrics)

        val displayHeight = displayMetrics.heightPixels
        val displayWidth = displayMetrics.widthPixels

        return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
    }

    override fun show() {
        super.show()
        /**
         * 设置全屏，要设置在show的后面
         */
        window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            window?.attributes = this
        }
        window?.decorView?.apply {
            setPadding(0, 0, 0, 0)
        }
        window?.navigationBarColor = Color.parseColor("#000000")
        if (expand) behaviorController.expand()
        else behaviorController.halfExpand()
    }

    /**
     * 更新头部布局
     *
     * @param f [@kotlin.ExtensionFunctionType] Function1<T, Unit>
     */
    fun <T : ContentBuilder> updateHeader(f: T.() -> Unit) {
        (headerBuilder as T).apply(f)
    }

    fun <T : ContentBuilder> updateContent(f: T.() -> Unit) {
        f.invoke(contentBuilder as T)
    }

    fun <T : ContentBuilder> updateFooter(f: T.() -> Unit) {
        f.invoke(footerBuilder as T)
    }


    fun expand() {
        behaviorController.expand()
    }

    fun halfExpand() {
        behaviorController.halfExpand()
    }
}

/**
 * 更新Toolbar
 * @receiver BottomDialog
 * @param f [@kotlin.ExtensionFunctionType] Function1<ToolbarHeader, Unit>
 */
fun BottomDialog.updateToolBar(f: ToolbarHeader.() -> Unit) {
    (headerBuilder as ToolbarHeader).apply(f)
}