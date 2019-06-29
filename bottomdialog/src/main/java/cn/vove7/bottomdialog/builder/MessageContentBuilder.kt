package cn.vove7.bottomdialog.builder

import android.view.View
import android.widget.TextView
import cn.vove7.bottomdialog.R
import cn.vove7.bottomdialog.interfaces.ContentBuilder
import cn.vove7.bottomdialog.util.listenToUpdate
import kotlinx.android.synthetic.main.content_message.view.*

/**
 * # MessageContentBuilder
 *
 * @author Vove
 * 2019/6/25
 */
class MessageContentBuilder(text: String, selectable: Boolean) : ContentBuilder() {
    var text: String by listenToUpdate(text, this, -2)
    var selectable: Boolean by listenToUpdate(selectable, this, 2)

    private lateinit var textView: TextView
    override val layoutRes: Int
        get() = R.layout.content_message


    override fun init(view: View) {
        textView = view.message_text
    }

    override fun updateContent(type: Int, data: Any?) {
        if (type <= -1) {
            textView.text = text
        }
        if (type >= -1) {
            textView.setTextIsSelectable(selectable)
        }
    }
}