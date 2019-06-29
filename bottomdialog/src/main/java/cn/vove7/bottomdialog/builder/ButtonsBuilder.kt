package cn.vove7.bottomdialog.builder

import android.view.View
import android.widget.TextView
import cn.vove7.bottomdialog.R
import cn.vove7.bottomdialog.interfaces.ContentBuilder
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

    private val map by lazy { Array<Pair<String, OnClick>?>(3) { null } }

    override fun init(view: View) {
        buttonPositive = view.dialog_button_positive
        buttonNegative = view.dialog_button_negative
        buttonNeutral = view.dialog_button_neutral
    }

    fun positiveButton(text: String = "确认", onClick: OnClick) {
        save(0, text, onClick)
    }

    fun negativeButton(text: String = "取消", onClick: OnClick = { dialog.dismiss() }) {
        save(1, text, onClick)
    }

    fun neutralButton(text: String, onClick: OnClick) {
        save(2, text, onClick)
    }

    private fun save(index: Int, text: String, onClick: OnClick) {
        map[index] = (text to onClick)
    }

    private fun setButton(btn: TextView, text: String, onClick: OnClick = { dialog.dismiss() }) {
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