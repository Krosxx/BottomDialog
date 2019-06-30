@file:Suppress("unused")

package cn.vove7.bottomdialog.builder

import android.content.Context
import android.support.design.widget.BottomSheetBehavior.PEEK_HEIGHT_AUTO
import android.view.MenuItem
import cn.vove7.bottomdialog.R
import cn.vove7.bottomdialog.ToolbarHeader
import cn.vove7.bottomdialog.interfaces.ContentBuilder
import cn.vove7.bottomdialog.util.ObservableList

/**
 * # BottomDialogInterface
 *
 * @author Vove
 * 2019/6/29
 */
@Suppress("unused")
class BottomDialogBuilder(var context: Context) {
    var themeId: Int = R.style.BottomDialog

    /**
     * 头部布局
     */
    internal var headerBuilder: ContentBuilder? = null

    /**
     * 内容布局
     */
    internal var contentBuilder: ContentBuilder? = null

    /**
     * 底部布局（悬浮）
     */
    internal var footerBuilder: ContentBuilder? = null

    var expand: Boolean = false

    var mCancelable = true

    /**
     * 导航栏背景色(若存在导航栏)
     */
    var navBgColor: Int? = null

    var peekHeight: Int = PEEK_HEIGHT_AUTO

    fun peekHeight(peekHeight: Int) {
        this.peekHeight = peekHeight
    }

    fun cancelable(flag: Boolean) {
        mCancelable = flag
    }

    fun expand() {
        expand = true
    }

    fun halfExpand() {
        expand = false
    }

    fun theme(tId: Int) {
        themeId = tId
    }

    /**
     * 自定义头部布局
     * @param headerBuilder T
     * @param action [@kotlin.ExtensionFunctionType] Function1<T, Unit>
     */
    fun <T : ContentBuilder> header(headerBuilder: T, action: (T.() -> Unit)? = null): BottomDialogBuilder {
        action?.also { it.invoke(headerBuilder) }
        this.headerBuilder = headerBuilder
        return this
    }

    /**
     * 自定义内容布局
     * @param contentBuilder T
     * @param action [@kotlin.ExtensionFunctionType] Function1<T, Unit>
     */
    fun <T : ContentBuilder> content(contentBuilder: T, action: (T.() -> Unit)? = null): BottomDialogBuilder {
        action?.also { it.invoke(contentBuilder) }
        this.contentBuilder = contentBuilder
        return this
    }

    /**
     * 自定义底部布局
     * @param footerBuilder T
     * @param action [@kotlin.ExtensionFunctionType] Function1<T, Unit>
     */
    fun <T : ContentBuilder> footer(footerBuilder: T, action: (T.() -> Unit)? = null): BottomDialogBuilder {
        action?.also { it.invoke(footerBuilder) }
        this.footerBuilder = footerBuilder
        return this
    }

}


/**
 * 一个宽度占满的Button，背景色为 colorPrimary
 * @param text String
 *
 * @param listener [@kotlin.ExtensionFunctionType] Function1<CallbackSetter, Unit>?
 * @return BottomDialogBuilder
 */
fun BottomDialogBuilder.oneButton(
        text: String,
        autoDismiss: Boolean = true,
        listener: (ClickListenerSetter.() -> Unit)? = null
): BottomDialogBuilder {
    val cbSetter = ClickListenerSetter()
    listener?.invoke(cbSetter)
    footerBuilder = OneActionBuilder(text, autoDismiss, cbSetter._onClick, cbSetter._onLongClick)
    return this
}

@Suppress("PropertyName", "unused")
class ClickListenerSetter {
    internal var _onClick: OnClick? = null
    internal var _onLongClick: OnClick? = null

    fun onClick(onClick: OnClick) {
        _onClick = onClick
    }

    fun onLongClick(onLongClick: OnClick) {
        _onLongClick = onLongClick
    }
}

fun BottomDialogBuilder.buttons(block: ButtonsBuilder.() -> Unit): BottomDialogBuilder {
    footerBuilder = ButtonsBuilder().also { block.invoke(it) }
    return this
}

/**
 * 简单消息
 * contentBuilder = [MessageContentBuilder]
 * @receiver BottomDialogBuilder
 * @param text String 消息文本
 * @param selectable Boolean 文字是否可选择
 * @return BottomDialogBuilder
 */
fun BottomDialogBuilder.message(text: String, selectable: Boolean = false): BottomDialogBuilder {
    contentBuilder = MessageContentBuilder(text, selectable)
    return this
}

/**
 * 简单列表
 * contentBuilder = [SimpleListBuilder]
 * @receiver BottomDialogBuilder
 * @param items List<String?>
 * @param autoDismiss Boolean
 * @param onItemClick Function3<[@kotlin.ParameterName] Dialog, [@kotlin.ParameterName] Int, [@kotlin.ParameterName] String?, Unit>
 * @return BottomDialogBuilder
 */
fun BottomDialogBuilder.simpleList(items: List<String?>, autoDismiss: Boolean = true, onItemClick: OnItemClick<String?>): BottomDialogBuilder {
    contentBuilder = SimpleListBuilder(
            if (items is ObservableList<String?>) items
            else ObservableList(items.toMutableList()),
            autoDismiss, onItemClick
    )
    return this
}

/**
 * 可动态更新列表
 * 操作items，列表自动刷新
 * contentBuilder = [SimpleListBuilder]
 *
 * ```
 * item.remove(..)
 * item.add(..)
 * ```
 * @receiver BottomDialogBuilder
 * @param items ObservableList<String?> [ObservableList]
 * @param autoDismiss Boolean
 * @param onItemClick Function3<[@kotlin.ParameterName] Dialog, [@kotlin.ParameterName] Int, [@kotlin.ParameterName] String?, Unit>
 * @return BottomDialogBuilder
 */
fun BottomDialogBuilder.mutableList(
        items: ObservableList<String?>,
        autoDismiss: Boolean = true,
        onItemClick: OnItemClick<String?>
): BottomDialogBuilder {
    contentBuilder = SimpleListBuilder(items, autoDismiss, onItemClick)
    return this
}

/**
 * 使用toolbar设置标题
 * headerBuilder = [ToolbarHeader]
 *
 * @receiver BottomDialogBuilder
 * @param title CharSequence?
 * @return BottomDialogBuilder
 */
fun BottomDialogBuilder.title(title: CharSequence?): BottomDialogBuilder {
    if (headerBuilder == null) {
        headerBuilder = ToolbarHeader(title)
    } else {
        (headerBuilder as ToolbarHeader).title = title
    }
    return this
}

/**
 * Toolbar 菜单
 * @receiver BottomDialogBuilder
 * @param menuResId Int
 */
fun BottomDialogBuilder.menu(menuResId: Int): BottomDialogBuilder {
    if (headerBuilder == null) {
        headerBuilder = ToolbarHeader()
    }
    if (headerBuilder is ToolbarHeader) {
        (headerBuilder as ToolbarHeader).apply {
            toolBar.inflateMenu(menuResId)
        }
    } else {
        throw RuntimeException("此方法 必须设置 headerBuilder 为 ToolbarHeader")
    }
    return this
}

fun BottomDialogBuilder.ensureHeaderIsToolbar() {

    if (headerBuilder == null) {
        headerBuilder = ToolbarHeader()
    }
    if (headerBuilder !is ToolbarHeader) {
        throw RuntimeException("此方法 必须设置 headerBuilder 为 ToolbarHeader")
    }
}

/**
 * 设置简单菜单项
 * @receiver BottomDialogBuilder
 * @param menuResId Int 菜单资源
 * @param onClick Function1<MenuItem, Boolean>
 */
fun BottomDialogBuilder.inflateMenu(menuResId: Int, onClick: (MenuItem) -> Boolean) {
    ensureHeaderIsToolbar()
    (headerBuilder as ToolbarHeader).apply {
        menuRes = menuResId
        onMenuItemClick = onClick
    }
}

fun BottomDialogBuilder.withCloseIcon() {
    inflateMenu(R.menu.menu_close_icon) {
        headerBuilder?.dialog?.dismiss()
        true
    }
}