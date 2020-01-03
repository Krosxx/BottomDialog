package cn.vove7.bottomdialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.NestedScrollView
import android.view.View
import android.view.View.NO_ID
import android.view.ViewGroup
import android.view.WindowManager
import cn.vove7.bottomdialog.builder.BottomDialogBuilder
import cn.vove7.bottomdialog.interfaces.ContentBuilder
import kotlinx.android.synthetic.main.dialog_content.*


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
        fun builder(
                activity: Activity,
                show: Boolean = true,
                action: BottomDialogBuilder.() -> Unit
        ): BottomDialog {
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
    private val expandable: Boolean = builder.expandable

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
    val footerView: ViewGroup get() = findViewById(R.id.footer_content)
    val footerContainer: ViewGroup get() = findViewById(R.id.footer_container)
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
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
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
                footerContainer.scrollY = (slideOffset * bottomHeight).toInt()
            } else {
                footerContainer.scrollY = 0
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

        if (!expandable) {
            if (peekHeight > 0) {
                bsView.layoutParams.also {
                    it.height = peekHeight
                    bsView.layoutParams = it
                }
            } else {
                throw Exception("expandable must set peekHeight")
            }

        }


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
                    bottomHeight = footerContainer.height + navHeight
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val tag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            rootView.systemUiVisibility = tag
        }

        val sf = findViewById<View>(R.id.statusbar_fill)
        if (immersionStatusBar) {
            sf.visibility = View.GONE
        } else {//状态栏高度
            sf.layoutParams = sf.layoutParams.also { it.height = stateBarHeight }
        }

        shadowListener()
    }

    private fun shadowListener() {
        //阴影
        showFooterElevation = footerBuilder != null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            container.setOnScrollChangeListener { nv: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
                //顶部阴影
                if (headerElevation && headerBuilder != null) {
                    if (scrollY == 0) {
                        if (showAppBarElevation) {
                            showAppBarElevation = false
                        }
                    } else if (!showAppBarElevation) {
                        showAppBarElevation = true
                    }
                }

                //底部阴影
                if (headerElevation && footerBuilder != null) {
                    if (contentView.height == scrollY + nv.height) {
                        showFooterElevation = false
                    } else {
                        if (!showFooterElevation) {
                            showFooterElevation = true
                        }
                    }
                }
            }
        }
    }

    private val container get() = this.findViewById<NestedScrollView>(R.id.container)

    private fun setContentMarginBottom(value: Int) {
        container.layoutParams =
            (container.layoutParams as ViewGroup.MarginLayoutParams).also { p ->
                p.setMargins(0, 0, 0, value)
            }

    }

    var showAppBarElevation: Boolean
        get() = appbar_elevation.visibility == View.VISIBLE
        set(value) {
            appbar_elevation.visibility = if (value) View.VISIBLE else View.GONE
        }

    var showFooterElevation: Boolean
        get() = footer_elevation.visibility == View.VISIBLE
        set(value) {
            footer_elevation.visibility = if (value) View.VISIBLE else View.GONE
        }

    /**
     * 获取导航栏高度
     * 可能隐藏
     * **需要保证Activity 100ms 后再显示状态栏**
     * @return Int
     */
    private fun getNavigationBarHeight(): Int {
        val resources = context.resources
        activity.window.decorView.systemUiVisibility
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
        val NAVIGATION = "navigationBarBackground"

        // 该方法需要在View完全被绘制出来之后调用，否则判断不了
        //在比如 onWindowFocusChanged（）方法中可以得到正确的结果
        val vp = activity.window.decorView as ViewGroup?
        return if (vp != null) {
            (0 until vp.childCount).map { vp.getChildAt(it) }.any {
                it.id != NO_ID && NAVIGATION == activity.resources.getResourceEntryName(it.id)
            }
        } else false
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.navigationBarColor = Color.parseColor("#000000")
        }
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
