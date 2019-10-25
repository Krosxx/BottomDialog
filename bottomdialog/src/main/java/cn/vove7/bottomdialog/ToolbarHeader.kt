package cn.vove7.bottomdialog

import android.view.MenuItem
import android.view.View
import android.support.v7.widget.Toolbar
import cn.vove7.bottomdialog.builder.BottomDialogBuilder
import cn.vove7.bottomdialog.builder.OnClick
import cn.vove7.bottomdialog.interfaces.ContentBuilder
import cn.vove7.bottomdialog.util.listenToUpdate
import kotlinx.android.synthetic.main.header_toolbar.view.*

/**
 * # ToolbarHeader
 *
 * @author Vove
 * 2019/6/28
 */

/**
 * 头部使用Toolbar
 */
fun BottomDialogBuilder.toolbar(action: ToolbarHeader.() -> Unit): BottomDialogBuilder {
    headerBuilder = ToolbarHeader("NULL").apply(action)
    return this
}

/**
 * 更新Toolbar
 * @receiver BottomDialog
 * @param f [@kotlin.ExtensionFunctionType] Function1<ToolbarHeader, Unit>
 */
fun BottomDialog.updateToolBar(f: ToolbarHeader.() -> Unit) {
    (headerBuilder as ToolbarHeader).apply(f)
}

class ToolbarHeader(title: CharSequence? = null) : ContentBuilder() {
    /**
     * 指定更新type=1
     */
    var title by listenToUpdate(title, this, type = 1)

    /**
     * 导航栏图标
     */
    var navIconId: Int? by listenToUpdate(null, this, type = 2)

    var onIconClick: OnClick? by listenToUpdate(null, this, type = 3)

    var menuRes: Int? by listenToUpdate(null, this, type = 4)

    var onMenuItemClick: ((MenuItem) -> Boolean)? = null

    override val layoutRes: Int = R.layout.header_toolbar

    lateinit var toolBar: Toolbar

    /**
     * 初始化View
     * @param view View
     */
    override fun init(view: View) {
        toolBar = view.tool_bar
    }

    /**
     * 进行视图更新
     * @param type Int listenToUpdate中指定的type，第一次刷新type值为-1
     * 可根据type值来选择更新视图，而不是全部更新
     * @param data Any? 传递值
     */
    override fun updateContent(type: Int, data: Any?) {
        if (type >= -1) toolBar.title = title

        if (type <= -1)
            navIconId?.also {
                toolBar.setNavigationIcon(it)
            } ?: toolBar.setNavigationIcon(null)

        if (type == -1 || type == 3) {
            toolBar.setNavigationOnClickListener {
                onIconClick?.invoke(dialog)
            }
        }
        if (type == -1 || type == 4) {
            menuRes?.also {
                toolBar.inflateMenu(it)
            }
            toolBar.setOnMenuItemClickListener(onMenuItemClick)
        }
    }

}