package cn.vove7.bottomdialog.builder

import android.view.View
import android.widget.TextView
import cn.vove7.bottomdialog.R
import cn.vove7.bottomdialog.interfaces.ContentBuilder
import cn.vove7.bottomdialog.util.isDarkMode
import kotlinx.android.synthetic.main.action_buttons.view.*

/**
 * # ButtonsBuilder
 *
 * @author Vove
 * 2019/6/25
 */
class ButtonsBuilder : ContentBuilder() {

    override val layoutRes: Int = R.layout.action_buttons

    lateinit var buttonPositive: TextView
        private set
    lateinit var buttonNegative: TextView
        private set
    lateinit var buttonNeutral: TextView
        private set

    private val map by lazy { Array<Pair<CharSequence, OnClick>?>(3) { null } }

    override fun init(view: View) {
        buttonPositive = view.dialog_button_positive
        buttonNegative = view.dialog_button_negative
        buttonNeutral = view.dialog_button_neutral
        if (!dialog.context.isDarkMode) {
            dialog.lightNavBar = true
        }
    }

    fun positiveButton(text: CharSequence = "确认", onClick: OnClick) {
        save(0, text, onClick)
    }

    fun negativeButton(text: CharSequence = "取消", onClick: OnClick = { dialog.dismiss() }) {
        save(1, text, onClick)
    }

    fun neutralButton(text: CharSequence, onClick: OnClick) {
        save(2, text, onClick)
    }

    private fun save(index: Int, text: CharSequence, onClick: OnClick) {
        map[index] = (text to onClick)
    }

    private fun setButton(
        btn: TextView,
        text: CharSequence,
        onClick: OnClick = { dialog.dismiss() }
    ) {
        btn.text = text
        btn.visibility = View.VISIBLE
        btn.setOnClickListener { onClick(dialog) }
    }


    override fun updateContent(type: Int, data: Any?) {
        val idMap = arrayOf(
            R.id.dialog_button_positive,
            R.id.dialog_button_negative,
            R.id.dialog_button_neutral
        )
        arrayOf(buttonNeutral, buttonPositive, buttonNegative).forEach {
            val p = map.getOrNull(idMap.indexOf(it.id)) ?: return@forEach
            setButton(it, p.first, p.second)
        }
    }
}