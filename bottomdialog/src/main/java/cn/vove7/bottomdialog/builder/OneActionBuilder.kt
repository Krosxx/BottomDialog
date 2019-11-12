package cn.vove7.bottomdialog.builder

import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import cn.vove7.bottomdialog.BottomDialog
import cn.vove7.bottomdialog.R
import cn.vove7.bottomdialog.interfaces.ContentBuilder
import cn.vove7.bottomdialog.util.listenToUpdate
import cn.vove7.bottomdialog.util.primaryColor
import kotlinx.android.synthetic.main.one_action_button.view.*

/**
 * # OneActionBuilder
 *
 * @author Vove
 * 2019/6/25
 */
class OneActionBuilder(
    buttonText: String,
    private val autoDismiss: Boolean,
    private val onClick: OnClick?,
    private val onLongClick: OnClick?,
    @ColorRes private val colorRes: Int?
) : ContentBuilder() {
    var buttonText: String by listenToUpdate(buttonText, this)

    override val layoutRes: Int = R.layout.one_action_button

    lateinit var actionButton: Button
    override fun init(view: View) {
        actionButton = view.action_button
        val appC = actionButton.context.applicationContext
        if (colorRes != null) {
            (actionButton.parent as ViewGroup).setBackgroundColor(ContextCompat.getColor(appC, colorRes))
        } else {
            appC.primaryColor?.also {
                (actionButton.parent as ViewGroup).setBackgroundColor(it)
            }
        }

    }

    override fun updateContent(type: Int, data: Any?) {
        actionButton.text = buttonText
        actionButton.setOnClickListener {
            onClick?.invoke(dialog)
            if (autoDismiss) dialog.dismiss()
        }
        onLongClick?.also { longClick ->
            actionButton.setOnLongClickListener {
                longClick(dialog)
                true
            }
        }
    }
}

typealias OnClick = (dialog: BottomDialog) -> Unit