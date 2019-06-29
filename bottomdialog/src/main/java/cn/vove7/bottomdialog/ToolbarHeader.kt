package cn.vove7.bottomdialog

import android.view.View
import android.widget.Toolbar
import cn.vove7.bottomdialog.interfaces.ContentBuilder
import cn.vove7.bottomdialog.util.listenToUpdate
import kotlinx.android.synthetic.main.header_toolbar.view.*

/**
 * # ToolbarHeader
 *
 * @author Vove
 * 2019/6/28
 */
class ToolbarHeader(title: CharSequence?) : ContentBuilder() {
    var title by listenToUpdate(title, this)

    override val layoutRes: Int = R.layout.header_toolbar

    lateinit var toolBar: Toolbar

    override fun init(view: View) {
        toolBar = view.tool_bar
    }

    override fun updateContent(type: Int, data: Any?) {
        toolBar.title = title
    }

}