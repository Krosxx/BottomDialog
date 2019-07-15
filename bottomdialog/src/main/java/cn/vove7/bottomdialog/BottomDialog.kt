package cn.vove7.bottomdialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.core.widget.NestedScrollView
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import cn.vove7.bottomdialog.builder.BottomDialogBuilder
import cn.vove7.bottomdialog.interfaces.ContentBuilder
import com.google.android.material.appbar.AppBarLayout


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

    internal val headerElevation = builder.headerElevation

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


    /**
     * 沉浸状态栏
     */
    var immersionStatusBar = false

    private var peekHeight: Int = builder.peekHeight

    val headerView: ViewGroup get() = findViewById(R.id.header_container)
    val footerView: ViewGroup get() = findViewById(R.id.footer_contains)
    val contentView: ViewGroup get() = findViewById(R.id.content)

    private lateinit var behaviorController: BehaviorController

    private var mCancelable = builder.mCancelable

    var navColor: Int? = builder.navBgColor

    private val onDismiss: (() -> Unit)? = builder.onDismiss
    /**
     * 底部布局高度
     */
    private var bottomHeight = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_content)
        buildContent()
    }

    val stateBarHeight: Int
        get() {
            var result = 0
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    lateinit var statusCallbacks: MutableList<StatusCallback>

    private val lis: StatusCallback = object : StatusCallback {
        override fun onSlide(slideOffset: Float) {
            if (this@BottomDialog::statusCallbacks.isInitialized) {
                statusCallbacks.forEach { it.onSlide(slideOffset) }
            }
            footerBuilder ?: return
            if (slideOffset < 0) {
                footerView.scrollY = (slideOffset * bottomHeight).toInt()
            } else {
                footerView.scrollY = 0
            }
        }

        override fun onExpand() {
            if (this@BottomDialog::statusCallbacks.isInitialized) {
                statusCallbacks.forEach { it.onExpand() }
            }
        }

        override fun onCollapsed() {
            if (this@BottomDialog::statusCallbacks.isInitialized) {
                statusCallbacks.forEach { it.onCollapsed() }
            }
        }

        override fun onHidden() {
            if (this@BottomDialog::statusCallbacks.isInitialized) {
                statusCallbacks.forEach { it.onHidden() }
            }
            sDismiss()
        }
    }

    private fun sDismiss() {
        super.dismiss()
        if (::statusCallbacks.isInitialized) {
            statusCallbacks.clear()
        }
    }


    @Synchronized
    fun listenStatus(lis: StatusCallback) {
        if (!::statusCallbacks.isInitialized) {
            statusCallbacks = mutableListOf()
        }
        statusCallbacks.add(lis)
    }

    override fun setCancelable(flag: Boolean) {
        super.setCancelable(flag)
        behaviorController.isHideable = flag
    }

    override fun dismiss() {
        behaviorController.hide()
    }

    val bsView get() = findViewById<View>(R.id.bs_root)


    /**
     * 使用ContentBuilder构建布局
     */
    private fun buildContent() {
        behaviorController = BehaviorController(bsView, lis)
        behaviorController.hide()
        behaviorController.peekHeight = peekHeight

        val rootView = findViewById<ViewGroup>(R.id.root)
        rootView.setOnClickListener {
            if (mCancelable) cancel()
        }

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

        onDismiss?.also {
            setOnDismissListener { it() }
        }

        val tag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        val sf = findViewById<View>(R.id.statusbar_fill)
        if (immersionStatusBar) {
            sf.visibility = View.GONE
        } else {//状态栏高度
            sf.layoutParams = sf.layoutParams.also { it.height = stateBarHeight }
        }

        rootView.systemUiVisibility = tag


    }


    //是否已有阴影
    var hasAppbarElevation = false

    private fun setContentMarginBottom(value: Int) {
        val container = this@BottomDialog.findViewById<NestedScrollView>(R.id.container)
        //阴影
        if (headerElevation) {
            val appbarLayout = findViewById<AppBarLayout>(R.id.appbar_lay)
            container.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
                if (scrollY == 0 && hasAppbarElevation) {
                    hasAppbarElevation = false
                    appbarLayout.elevation = 0f
                } else if (!hasAppbarElevation) {
                    hasAppbarElevation = true
                    appbarLayout.elevation = 10f
                }
            }
        }
        container.layoutParams = (container.layoutParams as LinearLayout.LayoutParams).also { p ->
            p.setMargins(0, 0, 0, value)
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

        if (activity is BottomDialogActivity) {
            showInternal()
        } else {
            Handler().postDelayed({ showInternal() }, 1)
        }
    }

    private fun showInternal() {
        if (expand) behaviorController.expand()
        else behaviorController.collapsed()

        behaviorController.isHideable = mCancelable
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
        behaviorController.collapsed()
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